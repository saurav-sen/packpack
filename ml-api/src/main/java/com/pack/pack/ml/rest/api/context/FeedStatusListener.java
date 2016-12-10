package com.pack.pack.ml.rest.api.context;

import com.pack.pack.model.web.JRssFeeds;

/**
 * 
 * @author Saurav
 *
 */
public interface FeedStatusListener {

	public void completed(JRssFeeds feeds);
	
	public void failed(JRssFeeds feeds);
}