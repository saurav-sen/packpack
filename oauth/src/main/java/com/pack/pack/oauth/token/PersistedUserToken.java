package com.pack.pack.oauth.token;


/**
 * 
 * @author Saurav
 *
 */
public class PersistedUserToken {

	private String refreshToken;
	
	private long timeOfIssue;
	
	private String userId;
	
	private String userIp;
	
	private String userAgent;

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public long getTimeOfIssue() {
		return timeOfIssue;
	}

	public void setTimeOfIssue(long timeOfIssue) {
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