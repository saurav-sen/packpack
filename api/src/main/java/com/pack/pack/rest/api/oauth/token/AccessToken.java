package com.pack.pack.rest.api.oauth.token;


/**
 * 
 * @author Saurav
 *
 */
public class AccessToken extends Token {

	private String token;
	
	private String refreshToken;
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}