package com.pack.pack.model;

import org.ektorp.support.CouchDbDocument;
import org.joda.time.DateTime;

/**
 * 
 * @author Saurav
 *
 */
public class PersistedUserToken extends CouchDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8356532879377269960L;

	private String refreshToken;
	
	private DateTime timeOfIssue;
	
	private String userId;
	
	private String userIp;
	
	private String userAgent;

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public DateTime getTimeOfIssue() {
		return timeOfIssue;
	}

	public void setTimeOfIssue(DateTime timeOfIssue) {
		this.timeOfIssue = timeOfIssue;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
}