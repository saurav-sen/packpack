package com.pack.pack.rest.api.oauth.token;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 
 * @author Saurav
 *
 */
public class Token {

	@JsonIgnore
	private long timeOfIssue;
	
	@JsonIgnore
	private boolean isValid = true;
	
	private TTL expiry;

	@JsonIgnore
	public long getTimeOfIssue() {
		return timeOfIssue;
	}

	public void setTimeOfIssue(long timeOfIssue) {
		this.timeOfIssue = timeOfIssue;
	}

	@JsonIgnore
	public TTL getExpiry() {
		return expiry;
	}

	public void setExpiry(TTL expiry) {
		this.expiry = expiry;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
}