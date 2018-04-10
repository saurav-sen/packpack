package com.squill.og.crawler.hooks;

import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.model.web.JRssFeeds;

public interface ISpiderSession {
	
	public static final String RSS_FEEDS_KEY = "RSS_FEEDS_KEY";

	public void addAttr(String key, Object value);

	public Object getAttr(String key);

	public IWebSite getCurrentWebSite();
	
	public JRssFeeds getFeeds(IWebSite webSite);
	
	public void done(IWebSite webSite);
	
	public IWebSite[] getAllCompleted();
}
