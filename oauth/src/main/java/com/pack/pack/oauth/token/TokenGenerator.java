package com.pack.pack.oauth.token;

import java.util.concurrent.TimeUnit;

/**
 * 
 * @author Saurav
 *
 */
public class TokenGenerator {
	
	public TokenGenerator() {
	}
	
	public AccessToken generateNewAccessToken() throws Exception {
		AccessToken token = new AccessToken();
		String tokenValue = new KeyGenerator().generateNewToken();
		token.setToken(tokenValue);
		String refreshToken = new KeyGenerator().generateNewToken();
		token.setRefreshToken(refreshToken);
		token.setExpiry(new TTL(2, TimeUnit.HOURS));
		token.setTimeOfIssue(System.currentTimeMillis());
		return token;
	}
	
	public RequestToken generateNewRequestToken() throws Exception {
		RequestToken token = new RequestToken();
		String tokenValue = new KeyGenerator().generateNewToken();
		token.setToken(tokenValue);
		token.setExpiry(new TTL(2, TimeUnit.HOURS));
		token.setTimeOfIssue(System.currentTimeMillis());
		return token;
	}
	public ResetToken generateNewResetToken(String userId) throws Exception {
		ResetToken token = new ResetToken();
		String tokenValue = new KeyGenerator().generateNewToken();
		token.setToken(tokenValue);
		token.setExpiry(new TTL(1, TimeUnit.HOURS));
		token.setTimeOfIssue(System.currentTimeMillis());
		token.setEmail(userId);
		return token;
	}
	
	public ResetToken generateNewResetToken(String uniqueKey, String userId) throws Exception {
		ResetToken token = new ResetToken();
		token.setToken(uniqueKey);
		token.setExpiry(new TTL(1, TimeUnit.HOURS));
		token.setTimeOfIssue(System.currentTimeMillis());
		token.setEmail(userId);
		return token;
	}
}