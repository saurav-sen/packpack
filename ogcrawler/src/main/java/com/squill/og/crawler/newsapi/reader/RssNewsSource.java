package com.squill.og.crawler.newsapi.reader;

/**
 * 
 * @author Saurav
 *
 */
public class RssNewsSource extends NewsSource {

	private String rssFeedUrl;
	
	private boolean active;
	
	private String baseUrl;

	public String getRssFeedUrl() {
		return rssFeedUrl;
	}

	public void setRssFeedUrl(String rssFeedUrl) {
		this.rssFeedUrl = rssFeedUrl;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
}
