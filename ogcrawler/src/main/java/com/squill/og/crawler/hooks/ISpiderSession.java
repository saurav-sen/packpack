package com.squill.og.crawler.hooks;

import com.squill.feed.web.model.JRssFeeds;
import com.squill.og.crawler.IWebCrawlable;

public interface ISpiderSession {
	
	public static final String RSS_FEEDS_KEY = "RSS_FEEDS_KEY";
	
	public static final String BATCH_ID_KEY = "BATCH_ID_KEY";

	public void addAttr(IWebCrawlable webCrawlable, String key, Object value);

	public Object getAttr(IWebCrawlable webCrawlable, String key);

	public JRssFeeds getFeeds(IWebCrawlable webCrawlable);
	
	public void begin(IWebCrawlable webCrawlable);
	
	public void end(IWebCrawlable webCrawlable);
	
	public void addSessionAttr(String key, Object value);
	
	public Object getSessionAttr(String key);
	
	public long getBatchId();
}
