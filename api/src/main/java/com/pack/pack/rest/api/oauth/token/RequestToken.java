package com.pack.pack.rest.api.oauth.token;


/**
 * 
 * @author Saurav
 *
 */
public class RequestToken extends Token {

	private String token;
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}