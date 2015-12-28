package com.pack.pack.oauth.token;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @author Saurav
 *
 */
public class TokenRegistry {
	
	public static final TokenRegistry INSTANCE = new TokenRegistry();
	
	private ConcurrentMap<String, SoftReference<AccessToken>> refreshTokenCache 
						= new ConcurrentHashMap<String, SoftReference<AccessToken>>();
	
	private boolean flag = true;
	
	private List<ITokenStateChangeListener> accessTokenStateListeners = 
										new ArrayList<ITokenStateChangeListener>(3);
	private BlockingQueue<TokenInfo> accessTokenInspectionQ = new ArrayBlockingQueue<TokenInfo>(100);
	
	private HazelcastInstance hazelcast;
	
	private static final String REQUEST_TOKEN_CACHE = "requestTokenCache";
	private static final String ACCESS_TOKEN_CACHE = "accessTokenCache";
	private static final String RESET_TOKEN_CACHE = "resetTokenCache";
	
	public static final String TOKEN_REGISTRY = "token-registry";

	
	private TokenRegistry() {
		Config cfg = new Config(TOKEN_REGISTRY);
		hazelcast = Hazelcast.newHazelcastInstance(cfg);
		
		Thread requestTokenInvalidator = new Thread(new RequestTokenInvalidator());
		Thread accessTokenInvalidator = new Thread(new AccessTokenInvalidator());
		Thread listenerExecutor = new Thread(new AccessTokenListenerExecutor());
		Thread resetTokenInvalidator = new Thread(new ResetTokenInvalidator());
		requestTokenInvalidator.setDaemon(true);
		accessTokenInvalidator.setDaemon(true);
		listenerExecutor.setDaemon(true);
		requestTokenInvalidator.start();
		accessTokenInvalidator.start();
		listenerExecutor.start();
	}
	
	public RequestToken serviceRequestToken(String token) {
		Map<String, RequestToken> map = hazelcast.getMap(REQUEST_TOKEN_CACHE);
		RequestToken requestToken = map.get(token);
		if(requestToken == null)
			return null;
		map.remove(token); //It is for one time use
		if(!requestToken.isValid())
			return null;
		long timeOfIssue = requestToken.getTimeOfIssue();
		int timeToLive = requestToken.getExpiry().getTimeToLive();
		TimeUnit timeToLiveUnit = requestToken.getExpiry().getTimeUnit();
		boolean isValid = isValidToken(timeOfIssue, timeToLive, timeToLiveUnit);
		requestToken.setValid(isValid);
		if(isValid) {
			requestToken.setValid(false); //It is for one time use
			return requestToken;
		}
		return null;
	}
	
	public ResetToken isValidResetToken(String token, boolean invalidate) {
		Map<String, ResetToken> map = hazelcast.getMap(RESET_TOKEN_CACHE);
		ResetToken resetToken = map.get(token);
		if(resetToken == null)
			return null;
		if (invalidate) {
			map.remove(token); //It is for one time use
		}
		if(!resetToken.isValid())
			return null;
		long timeOfIssue = resetToken.getTimeOfIssue();
		int timeToLive = resetToken.getExpiry().getTimeToLive();
		TimeUnit timeToLiveUnit = resetToken.getExpiry().getTimeUnit();
		boolean isValid = isValidToken(timeOfIssue, timeToLive, timeToLiveUnit);
		resetToken.setValid(isValid);
		if(isValid) {
			if(invalidate)
			resetToken.setValid(false); //It is for one time use
			return 	resetToken;
		}
		return null;
	}
	public void addRequestToken(RequestToken token) {
		Map<String, RequestToken> map = hazelcast.getMap(REQUEST_TOKEN_CACHE);
		map.put(token.getToken(), token);
	}
	
	public void addResetToken(ResetToken token) {
		Map<String, ResetToken> map = hazelcast.getMap(RESET_TOKEN_CACHE);
		map.put(token.getToken(), token);
	}
	
	
	public boolean isValidAccessToken(String token) {
		Map<String, AccessToken> map = hazelcast.getMap(ACCESS_TOKEN_CACHE);
		AccessToken accessToken = map.get(token);
		if(accessToken == null)
			return false;
		if(!accessToken.isValid())
			return false;
		long timeOfIssue = accessToken.getTimeOfIssue();
		int timeToLive = accessToken.getExpiry().getTimeToLive();
		TimeUnit timeToLiveUnit = accessToken.getExpiry().getTimeUnit();
		boolean isValid = isValidToken(timeOfIssue, timeToLive, timeToLiveUnit);
		accessToken.setValid(isValid);
		return isValid;
	}
	
	public void addAccessToken(AccessToken token) {
		Map<String, AccessToken> map = hazelcast.getMap(ACCESS_TOKEN_CACHE);
		map.put(token.getToken(), token);
		accessTokenInspectionQ.offer(new TokenInfo(token, TokenState.CREATED));
	}
	
	public boolean invalidateAccessToken(String accessToken, String username) {
		Map<String, AccessToken> map = hazelcast.getMap(ACCESS_TOKEN_CACHE);
		AccessToken token = map.get(accessToken);
		if(token ==  null)
			return false;
		token.setValid(false);
		return true;
	}
	
	public void stop() {
		flag = false;
	}
	
	private boolean isValidToken(long timeOfIssue, int timeToLive, TimeUnit timeToLiveUnit) {
		long milliseconds = 0;
		switch (timeToLiveUnit) {
		case MILLISECONDS:
			milliseconds = timeToLive;
			break;
		case SECONDS:
			milliseconds = 1000*timeToLive;
			break;
		case MINUTES:
			milliseconds = 1000*60*timeToLive;
			break;
		case HOURS:
			milliseconds = 1000*60*60*timeToLive;
			break;
		case DAYS:
			milliseconds = 1000*24*60*60*timeToLive;
			break;
		}
		milliseconds = milliseconds + timeOfIssue;
		long currentTime = System.currentTimeMillis();
		return (currentTime - milliseconds) < 100;
	}
	
	public void addAccessTokenStateChangeListener(ITokenStateChangeListener listener) {
		accessTokenStateListeners.add(listener);
	}
	
	private class RequestTokenInvalidator implements Runnable {
		
		@Override
		public void run() {
			while(flag) {
				Map<String, RequestToken> map = hazelcast.getMap(REQUEST_TOKEN_CACHE);
				Iterator<String> itr = map.keySet().iterator();
				while(itr.hasNext()) {
					String key = itr.next();
					RequestToken token = map.get(key);
					if(token == null || !token.isValid()) {
						itr.remove();
						continue;
					}
					long timeOfIssue = token.getTimeOfIssue();
					int timeToLive = token.getExpiry().getTimeToLive();
					if (!isValidToken(timeOfIssue, timeToLive, token
							.getExpiry().getTimeUnit())) {
						itr.remove();
					}
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private class ResetTokenInvalidator implements Runnable {
		
		@Override
		public void run() {
			while(flag) {
				Map<String, ResetToken> map = hazelcast.getMap(RESET_TOKEN_CACHE);
				Iterator<String> itr = map.keySet().iterator();
				while(itr.hasNext()) {
					String key = itr.next();
					ResetToken token = map.get(key);
					if(token == null || !token.isValid()) {
						itr.remove();
						continue;
					}
					long timeOfIssue = token.getTimeOfIssue();
					int timeToLive = token.getExpiry().getTimeToLive();
					if (!isValidToken(timeOfIssue, timeToLive, token
							.getExpiry().getTimeUnit())) {
						itr.remove();
					}
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	private class AccessTokenInvalidator implements Runnable {
		
		@Override
		public void run() {
			while(flag) {
				Map<String, AccessToken> map = hazelcast.getMap(ACCESS_TOKEN_CACHE);
				Iterator<String> itr = map.keySet().iterator();
				while(itr.hasNext()) {
					try {
						String key = itr.next();
						AccessToken token = map.get(key);
						if(token == null || !token.isValid()) {
							itr.remove();
							accessTokenInspectionQ.offer(new TokenInfo(token, TokenState.DESTROYED), 1000, TimeUnit.MILLISECONDS);
							continue;
						}
						long timeOfIssue = token.getTimeOfIssue();
						int timeToLive = token.getExpiry().getTimeToLive();
						if (!isValidToken(timeOfIssue, timeToLive, token
								.getExpiry().getTimeUnit())) {
							accessTokenInspectionQ.offer(new TokenInfo(token, TokenState.DESTROYED), 1000, TimeUnit.MILLISECONDS);
							itr.remove();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class AccessTokenListenerExecutor implements Runnable {
		@Override
		public void run() {
			while(flag) {
				try {
					TokenInfo tokenInfo = accessTokenInspectionQ.poll(1000, TimeUnit.MILLISECONDS);
					if(tokenInfo == null)
						continue;
					for(ITokenStateChangeListener listener : accessTokenStateListeners) {
						Token token = tokenInfo.getToken();
						TokenState state = tokenInfo.getState();
						listener.stateChanged(token, state);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	
	
	private class TokenInfo {
		
		private Token token;
		
		private TokenState state;
		
		public TokenInfo(Token token, TokenState state) {
			this.token = token;
			this.state = state;
		}

		public Token getToken() {
			return token;
		}

		public void setToken(Token token) {
			this.token = token;
		}

		public TokenState getState() {
			return state;
		}

		public void setState(TokenState state) {
			this.state = state;
		}
	}
}