package com.pack.pack.oauth.token;

/**
 * 
 * @author Saurav
 *
 */
public class AccessTokenInfo extends TokenInfo {

	private String refreshToken;

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}