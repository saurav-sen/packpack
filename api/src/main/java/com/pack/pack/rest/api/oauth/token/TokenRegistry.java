package com.pack.pack.rest.api.oauth.token;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author Saurav
 *
 */
public class TokenRegistry {
	
	public static final TokenRegistry INSTANCE = new TokenRegistry();
	
	private ConcurrentMap<String, SoftReference<RequestToken>> requestTokenCache 
						= new ConcurrentHashMap<String, SoftReference<RequestToken>>();
	
	private ConcurrentMap<String, SoftReference<AccessToken>> accessTokenCache 
						= new ConcurrentHashMap<String, SoftReference<AccessToken>>();
	
	private ConcurrentMap<String, SoftReference<AccessToken>> refreshTokenCache 
						= new ConcurrentHashMap<String, SoftReference<AccessToken>>();
	
	private ConcurrentMap<String, SoftReference<ResetToken>> resetTokenCache 
	= new ConcurrentHashMap<String, SoftReference<ResetToken>>();
	
	private boolean flag = true;
	
	private List<ITokenStateChangeListener> accessTokenStateListeners = 
										new ArrayList<ITokenStateChangeListener>(3);
	private BlockingQueue<TokenInfo> accessTokenInspectionQ = new ArrayBlockingQueue<TokenInfo>(100);
	

	
	private TokenRegistry() {
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
		SoftReference<RequestToken> ref = requestTokenCache.get(token);
		if(ref == null)
			return null;
		RequestToken requestToken = ref.get();
		if(requestToken == null)
			return null;
		ref.clear(); //It is for one time use
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
		SoftReference<ResetToken> ref = resetTokenCache.get(token);
		if(ref == null)
			return null;
		ResetToken resetToken = ref.get();
		if(resetToken == null)
			return null;
		if (invalidate)
		ref.clear(); //It is for one time use
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
		requestTokenCache.put(token.getToken(), new SoftReference<RequestToken>(token));
	}
	
	public void addResetToken(ResetToken token) {
		resetTokenCache.put(token.getToken(), new SoftReference<ResetToken>(token));
	}
	
	
	public boolean isValidAccessToken(String token) {
		SoftReference<AccessToken> ref = accessTokenCache.get(token);
		if(ref == null)
			return false;
		AccessToken accessToken = ref.get();
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
		SoftReference<AccessToken> ref = new SoftReference<AccessToken>(token);
		accessTokenCache.put(token.getToken(), ref);
		//refreshTokenCache.put(token.getRefreshToken(), ref);
		accessTokenInspectionQ.offer(new TokenInfo(token, TokenState.CREATED));
	}
	
	public boolean invalidateAccessToken(String accessToken, String username) {
		SoftReference<AccessToken> ref = accessTokenCache.get(accessToken);
		if(ref == null)
			return false;
		AccessToken token = ref.get();
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
				Iterator<String> itr = requestTokenCache.keySet().iterator();
				while(itr.hasNext()) {
					String key = itr.next();
					SoftReference<RequestToken> ref = requestTokenCache.get(key);
					if(ref == null)
						continue;
					RequestToken token = ref.get();
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
				Iterator<String> itr = resetTokenCache.keySet().iterator();
				while(itr.hasNext()) {
					String key = itr.next();
					SoftReference<ResetToken> ref = resetTokenCache.get(key);
					if(ref == null)
						continue;
					ResetToken token = ref.get();
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
				Iterator<String> itr = accessTokenCache.keySet().iterator();
				while(itr.hasNext()) {
					try {
						String key = itr.next();
						SoftReference<AccessToken> ref = accessTokenCache.get(key);
						if(ref == null)
							continue;
						AccessToken token = ref.get();
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