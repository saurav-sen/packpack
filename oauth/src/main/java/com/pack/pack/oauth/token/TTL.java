package com.pack.pack.oauth.token;

import java.util.concurrent.TimeUnit;

/**
 * 
 * @author Saurav
 *
 */
public class TTL {

	private int timeToLive;
	
	private TimeUnit timeUnit;
	
	public TTL(int timeToLive, TimeUnit timeUnit) {
		this.timeToLive = timeToLive;
		this.timeUnit = timeUnit;
	}

	public int getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}
}