package com.squill.feed.web.model;

import java.util.concurrent.TimeUnit;

/**
 * 
 * @author Saurav
 *
 */
public class TTL {

	private short time;
	
	private TimeUnit unit;

	public short getTime() {
		return time;
	}

	public void setTime(short time) {
		this.time = time;
	}

	public TimeUnit getUnit() {
		return unit;
	}

	public void setUnit(TimeUnit unit) {
		this.unit = unit;
	}
}