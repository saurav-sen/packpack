package com.squill.og.crawler.hooks;

import java.util.List;

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
	
	public void upsertCrawledInfo(String link, WebSpiderTracker value,
			long ttlSeconds, boolean updateLastModifiedTime);
	
	public WebSpiderTracker getTrackedInfo(String link);
	
	public List<WebSpiderTracker> getAllTrackedInfo();
	
	public void clearAll();
}