package com.squill.og.crawler.model.web;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class JRssFeeds {

	private List<JRssFeed> feeds;

	public List<JRssFeed> getFeeds() {
		if(feeds == null) {
			feeds = new LinkedList<JRssFeed>();
		}
		return feeds;
	}

	public void setFeeds(List<JRssFeed> feeds) {
		this.feeds = feeds;
	}
}