package com.pack.pack.ml.rest.api.context;

import com.squill.feed.web.model.JRssFeeds;

/**
 * 
 * @author Saurav
 *
 */
public interface FeedStatusListener {

	public void completed(JRssFeeds feeds);
	
	public void failed(JRssFeeds feeds);
}