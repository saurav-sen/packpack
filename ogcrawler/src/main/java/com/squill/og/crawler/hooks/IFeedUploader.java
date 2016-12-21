package com.squill.og.crawler.hooks;

import com.pack.pack.model.web.JRssFeeds;

/**
 * 
 * @author Saurav
 *
 */
public interface IFeedUploader {

	public void addConfig(String key, String value);
	
	public void uploadBulk(JRssFeeds rssFeeds) throws Exception ;
}