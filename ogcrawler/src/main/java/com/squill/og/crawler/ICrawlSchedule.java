package com.squill.og.crawler;

import java.util.concurrent.TimeUnit;

/**
 * 
 * @author CipherCloud
 *
 */
public interface ICrawlSchedule {

	public long getInitialDelay();
	
	public long getPeriodicDelay();
	
	public TimeUnit getTimeUnit();
}