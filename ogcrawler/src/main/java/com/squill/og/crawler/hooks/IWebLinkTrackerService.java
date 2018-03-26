package com.squill.og.crawler.hooks;

import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.model.WebSpiderTracker;

/**
 * 
 * @author Saurav
 *
 */
public interface IWebLinkTrackerService {
	
	public void init(IWebSite webSite);

	public void dispose();
	
	public void addCrawledInfo(String link, WebSpiderTracker value,
			long ttlSeconds);
	
	public WebSpiderTracker getTrackedInfo(String link);
	
	public void clearAll();
}