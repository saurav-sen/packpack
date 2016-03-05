package com.pack.pack.oauth.registry;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.pack.pack.model.PersistedUserToken;
import com.pack.pack.oauth.token.AccessToken;
import com.pack.pack.oauth.token.Token;
import com.pack.pack.services.couchdb.PersistedUserTokenRepositoryService;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
public class TokenRegistry {

	public static final TokenRegistry INSTANCE = new TokenRegistry();

	private boolean isRunning = false;

	private HazelcastInstance hazelcast;

	private static final String REQUEST_TOKEN_CACHE = "requestTokenCache"; //$NON-NLS-1$
	private static final String ACCESS_TOKEN_CACHE = "accessTokenCache"; //$NON-NLS-1$
	
	private static final String PRINCIPAL_VS_ACCESS_TOKEN_CACHE = "principalVsAccessTokenCache"; //$NON-NLS-1$
	private static final String REQUEST_TOKEN_VS_VERIFIER_CACHE = "requestTokenVsVerifierCache"; //$NON-NLS-1$

	public static final String TOKEN_REGISTRY = "token-registry"; //$NON-NLS-1$
	
	private static final String HAZELCAST_XML_CONFIG = "META-INF/hazelcast-config.xml"; //$NON-NLS-1$

	private TokenRegistry() {
	}
	
	public void start() {
		if(isRunning) {
			return;
		}
		Config cfg = new ClasspathXmlConfig(HAZELCAST_XML_CONFIG);
		cfg.setInstanceName(TOKEN_REGISTRY);
		hazelcast = Hazelcast.newHazelcastInstance(cfg);
		isRunning = true;
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				TokenRegistry.this.stop();
			}
		});
	}

	public Token serviceRequestToken(String token) {
		checkRunningStatus();
		Map<String, Token> map = hazelcast.getMap(REQUEST_TOKEN_CACHE);
		Token requestToken = map.get(token);
		if (requestToken == null)
			return null;
		/*if (!requestToken.isValid())
			return null;*/
		long timeOfIssue = requestToken.getTimeOfIssue();
		int timeToLive = requestToken.getExpiry().getTimeToLive();
		TimeUnit timeToLiveUnit = requestToken.getExpiry().getTimeUnit();
		boolean isValid = isValidToken(timeOfIssue, timeToLive, timeToLiveUnit);
		//requestToken.setValid(isValid);
		if (isValid) {
			//requestToken.setValid(false); // It is for one time use
			return requestToken;
		}
		return null;
	}

	public void addRequestToken(Token token) {
		checkRunningStatus();
		Map<String, Token> map = hazelcast.getMap(REQUEST_TOKEN_CACHE);
		map.put(token.getToken(), token);
	}
	
	public Token invalidateRequestToken(String token) {
		checkRunningStatus();
		Map<String, Token> map = hazelcast.getMap(REQUEST_TOKEN_CACHE);
		return map.remove(token);
	}

	public boolean isValidAccessToken(String token) {
		checkRunningStatus();
		Map<String, Token> map = hazelcast.getMap(ACCESS_TOKEN_CACHE);
		Token accessToken = map.get(token);
		if (accessToken == null)
			return false;
		/*if (!accessToken.isValid())
			return false;*/
		long timeOfIssue = accessToken.getTimeOfIssue();
		int timeToLive = accessToken.getExpiry().getTimeToLive();
		TimeUnit timeToLiveUnit = accessToken.getExpiry().getTimeUnit();
		boolean isValid = isValidToken(timeOfIssue, timeToLive, timeToLiveUnit);
		//accessToken.setValid(isValid);
		return isValid;
	}

	public boolean isValidRefreshToken(String refreshToken, String userAgent,
			String username, String deviceId) {
		checkRunningStatus();
		PersistedUserTokenRepositoryService service = ServiceRegistry.INSTANCE
				.findService(PersistedUserTokenRepositoryService.class);
		PersistedUserToken token = service.findByRefreshToken(refreshToken);
		if (token == null)
			return false;
		return token.getUserId().equals(username)
				&& token.getUserIp().equals(deviceId);
	}
	
	public void addAccessToken(AccessToken token) {
		checkRunningStatus();
		Map<String, Token> map = hazelcast.getMap(ACCESS_TOKEN_CACHE);
		map.put(token.getToken(), token);
		IMap<String, List<Token>> map2 = hazelcast.getMap(PRINCIPAL_VS_ACCESS_TOKEN_CACHE);
		Principal principal = token.getPrincipal();
		List<Token> list = map2.get(principal.getName());
		if(list == null) {
			list = new ArrayList<Token>();
		}
		list.add(token);
		String username = principal.getName();
		map2.put(username, list);
		String refreshToken = token.getRefreshToken();
		if (refreshToken != null) {
			PersistedUserToken pToken = new PersistedUserToken();
			pToken.setRefreshToken(refreshToken);
			pToken.setTimeOfIssue(new DateTime());
			pToken.setUserId(username);
			//pToken.setUserIp(deviceId);
			PersistedUserTokenRepositoryService service = ServiceRegistry.INSTANCE
					.findService(PersistedUserTokenRepositoryService.class);
			service.add(pToken);
		}
	}
	
	public List<Token> getAllAccessTokens(String principalName) {
		checkRunningStatus();
		IMap<String, List<Token>> map = hazelcast.getMap(PRINCIPAL_VS_ACCESS_TOKEN_CACHE);
		return map.get(principalName);
	}
	
	public Token invalidateAccessToken(String accessToken) {
		checkRunningStatus();
		Map<String, Token> map = hazelcast.getMap(ACCESS_TOKEN_CACHE);
		AccessToken token = (AccessToken)map.remove(accessToken);
		IMap<Principal, List<Token>> map2 = hazelcast.getMap(PRINCIPAL_VS_ACCESS_TOKEN_CACHE);
		Principal principal = token.getPrincipal();
		List<Token> list = map2.get(principal.getName());
		if(list != null) {
			list.remove(token);
		}
		String refreshToken = token.getRefreshToken();
		if (refreshToken != null) {
			PersistedUserTokenRepositoryService service = ServiceRegistry.INSTANCE
					.findService(PersistedUserTokenRepositoryService.class);
			PersistedUserToken token2 = service
					.findByRefreshToken(refreshToken);
			if (token2 != null) {
				service.remove(token2);
			}
		}
		return token;
	}
	
	public void addVerifier(String requestToken, String verifier) {
		checkRunningStatus();
		Map<String, String> map = hazelcast.getMap(REQUEST_TOKEN_VS_VERIFIER_CACHE);
		map.put(requestToken, verifier);
	}
	
	public String removeVerifier(String requestToken) {
		checkRunningStatus();
		Map<String, String> map = hazelcast.getMap(REQUEST_TOKEN_VS_VERIFIER_CACHE);
		return map.remove(requestToken);
	}
	
	public Token getAccessToken(String accessToken) {
		checkRunningStatus();
		Map<String, Token> map = hazelcast.getMap(ACCESS_TOKEN_CACHE);
		return map.get(accessToken);
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