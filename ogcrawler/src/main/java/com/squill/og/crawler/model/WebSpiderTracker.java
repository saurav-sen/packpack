package com.squill.og.crawler.model;

/**
 * 
 * @author Saurav
 *
 */
public class WebSpiderTracker {

	private long lastCrawled;
	
	private String link;

	public long getLastCrawled() {
		return lastCrawled;
	}

	public void setLastCrawled(long lastCrawled) {
		this.lastCrawled = lastCrawled;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}