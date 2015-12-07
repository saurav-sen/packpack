package com.pack.pack.rest.api.oauth.token;

/**
 * 
 * @author Saurav
 *
 */
public class LoginStatus {
	
	public static final String VALID = "valid";
	public static final String INVALID = "invalid";

	public String valid;

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}
}