package com.squill.og.crawler.hooks;

import java.util.List;
import java.util.Map;

import com.squill.feed.web.model.JRssFeed;
import com.squill.og.crawler.IWebCrawlable;

/**
 * 
 * @author Saurav
 *
 */
public interface IFeedHandler {

	public default boolean isExecuteAItaks() {
		return false;
	}

	public void handleReceived(Map<String, List<JRssFeed>> feedsMap,
			ISpiderSession session, IWebCrawlable webCrawlable)
			throws Exception;
}
