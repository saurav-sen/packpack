package com.squill.og.crawler.internal;

public class LocalData {

	public long lastNotifiedTimestamp = -1;
	
	public static final LocalData INSTANCE = new LocalData();
	
	private LocalData() {
	}
	
	public void setLastNotifiedTimestamp(long lastNotifiedTimestamp) {
		this.lastNotifiedTimestamp = lastNotifiedTimestamp;
	}
	
	public long getLastNotifiedTimestamp() {
		return lastNotifiedTimestamp;
	}
}
