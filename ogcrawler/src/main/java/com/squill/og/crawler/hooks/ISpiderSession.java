package com.squill.og.crawler.hooks;

import com.squill.og.crawler.IWebCrawlable;
import com.squill.og.crawler.model.web.JRssFeeds;

public interface ISpiderSession {
	
	public static final String RSS_FEEDS_KEY = "RSS_FEEDS_KEY";

	public void addAttr(IWebCrawlable webCrawlable, String key, Object value);

	public Object getAttr(IWebCrawlable webCrawlable, String key);

	public JRssFeeds getFeeds(IWebCrawlable webSite);
	
	public void done(IWebCrawlable webSite);
}
