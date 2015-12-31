package com.pack.pack.oauth.token;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.pack.pack.model.PersistedUserToken;
import com.pack.pack.services.couchdb.PersistedUserTokenRepositoryService;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
public class TokenRegistry {

	public static final TokenRegistry INSTANCE = new TokenRegistry();

	private boolean isRunning = true;

	private HazelcastInstance hazelcast;

	private static final String REQUEST_TOKEN_CACHE = "requestTokenCache";
	private static final String ACCESS_TOKEN_CACHE = "accessTokenCache";

	public static final String TOKEN_REGISTRY = "token-registry";

	private TokenRegistry() {
		reinitialize();
	}
	
	public void reinitialize() {
		if(isRunning) {
			return;
		}
		Config cfg = new Config(TOKEN_REGISTRY);
		hazelcast = Hazelcast.newHazelcastInstance(cfg);
		isRunning = true;
	}

	public RequestToken serviceRequestToken(String token) {
		checkRunningStatus();
		Map<String, RequestToken> map = hazelcast.getMap(REQUEST_TOKEN_CACHE);
		RequestToken requestToken = map.get(token);
		if (requestToken == null)
			return null;
		map.remove(token); // It is for one time use
		if (!requestToken.isValid())
			return null;
		long timeOfIssue = requestToken.getTimeOfIssue();
		int timeToLive = requestToken.getExpiry().getTimeToLive();
		TimeUnit timeToLiveUnit = requestToken.getExpiry().getTimeUnit();
		boolean isValid = isValidToken(timeOfIssue, timeToLive, timeToLiveUnit);
		requestToken.setValid(isValid);
		if (isValid) {
			requestToken.setValid(false); // It is for one time use
			return requestToken;
		}
		return null;
	}

	public void addRequestToken(RequestToken token) {
		checkRunningStatus();
		Map<String, RequestToken> map = hazelcast.getMap(REQUEST_TOKEN_CACHE);
		map.put(token.getToken(), token);
	}

	public boolean isValidAccessToken(String token) {
		checkRunningStatus();
		Map<String, AccessToken> map = hazelcast.getMap(ACCESS_TOKEN_CACHE);
		AccessToken accessToken = map.get(token);
		if (accessToken == null)
			return false;
		if (!accessToken.isValid())
			return false;
		long timeOfIssue = accessToken.getTimeOfIssue();
		int timeToLive = accessToken.getExpiry().getTimeToLive();
		TimeUnit timeToLiveUnit = accessToken.getExpiry().getTimeUnit();
		boolean isValid = isValidToken(timeOfIssue, timeToLive, timeToLiveUnit);
		accessToken.setValid(isValid);
		return isValid;
	}

	public boolean isValidRefreshToken(String refreshToken, String userAgent,
			String userId, String deviceId) {
		checkRunningStatus();
		PersistedUserTokenRepositoryService service = ServiceRegistry.INSTANCE
				.findService(PersistedUserTokenRepositoryService.class);
		PersistedUserToken token = service.findByRefreshToken(refreshToken);
		if (token == null)
			return false;
		return token.getUserId().equals(userId)
				&& token.getUserIp().equals(deviceId);
	}

	public void addAccessToken(AccessToken token, String userId, String deviceId) {
		checkRunningStatus();
		Map<String, AccessToken> map = hazelcast.getMap(ACCESS_TOKEN_CACHE);
		map.put(token.getToken(), token);
		String refreshToken = token.getRefreshToken();
		if (refreshToken != null) {
			PersistedUserToken pToken = new PersistedUserToken();
			pToken.setRefreshToken(refreshToken);
			pToken.setTimeOfIssue(new DateTime());
			pToken.setUserId(userId);
			pToken.setUserIp(deviceId);
			PersistedUserTokenRepositoryService service = ServiceRegistry.INSTANCE
					.findService(PersistedUserTokenRepositoryService.class);
			service.add(pToken);
		}
	}

	public boolean invalidateAccessToken(String accessToken, String username) {
		checkRunningStatus();
		Map<String, AccessToken> map = hazelcast.getMap(ACCESS_TOKEN_CACHE);
		AccessToken token = map.get(accessToken);
		if (token == null)
			return false;
		String refreshToken = token.getRefreshToken();
		token.setValid(false);
		map.remove(accessToken);
		if (refreshToken != null) {
			PersistedUserTokenRepositoryService service = ServiceRegistry.INSTANCE
					.findService(PersistedUserTokenRepositoryService.class);
			PersistedUserToken token2 = service
					.findByRefreshToken(refreshToken);
			if (token2 != null) {
				service.remove(token2);
			}
		}
		return true;
	}

	public void stop() {
		if(!isRunning) {
			return;
		}
		isRunning = false;
		hazelcast.shutdown();
	}

	private boolean isValidToken(long timeOfIssue, int timeToLive,
			TimeUnit timeToLiveUnit) {
		checkRunningStatus();
		long milliseconds = 0;
		switch (timeToLiveUnit) {
		case MILLISECONDS:
			milliseconds = timeToLive;
			break;
		case SECONDS:
			milliseconds = 1000 * timeToLive;
			break;
		case MINUTES:
			milliseconds = 1000 * 60 * timeToLive;
			break;
		case HOURS:
			milliseconds = 1000 * 60 * 60 * timeToLive;
			break;
		case DAYS:
			milliseconds = 1000 * 24 * 60 * 60 * timeToLive;
			break;
		}
		milliseconds = milliseconds + timeOfIssue;
		long currentTime = System.currentTimeMillis();
		return (currentTime - milliseconds) < 100;
	}
	
	private void checkRunningStatus() {
		if(!isRunning) {
			throw new RuntimeException("Token-Registry is stopped. Please re-initialize again.");
		}
	}
}