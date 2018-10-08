package com.squill.og.crawler.hooks;

import java.util.List;

import com.squill.feed.web.model.JRssFeedType;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.model.DocumentHeadersMemento;
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

	public void addValue(String keyPrefix, String key, String value,
			long ttlSeconds);

	public String getValue(String keyPrefix, String key);

	public void clearAll();

	public DocumentHeadersMemento getPreviousSessionMemento(JRssFeedType type);

	public void flushNewHeadersMemento(DocumentHeadersMemento headersMemento,
			JRssFeedType type);
}