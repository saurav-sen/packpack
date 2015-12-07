package com.pack.pack.rest.api.oauth.token;


/**
 * 
 * @author Saurav
 *
 */
public class ResetToken extends Token {

	private String token;
	private String email;
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}