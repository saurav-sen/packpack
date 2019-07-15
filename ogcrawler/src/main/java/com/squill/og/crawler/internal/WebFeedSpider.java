package com.squill.og.crawler.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.feed.web.model.JRssFeed;
import com.squill.og.crawler.IRssSite;
import com.squill.og.crawler.Spider;
import com.squill.og.crawler.SpiderSession;
import com.squill.og.crawler.external.feed.ExtFeed;
import com.squill.og.crawler.external.feed.ExternalRssFeedParser;
import com.squill.og.crawler.hooks.IFeedHandler;
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.utils.WebFeedSpiderUtil;

/**
 * 
 * @author Saurav
 *
 */
public class WebFeedSpider implements Spider {

	private IRssSite rssSite;

	private static Logger LOG = LoggerFactory.getLogger(WebFeedSpider.class);

	private SpiderSession session;

	private IFeedHandler feedHandler;

	public WebFeedSpider(IRssSite rssSite,
			long crawlSchedulePeriodicTimeInMillis,
			IWebLinkTrackerService tracker, IFeedHandler feedHandler,
			SpiderSession session) {
		this.rssSite = rssSite;
		this.feedHandler = feedHandler;
		this.session = session;
	}

	@Override
	public void run() {
		if (session.isThresholdReached())
			return;
		IFeedUploader feedUploader = session.getFeedUploader();
		try {
			if (feedHandler == null) {
				session.begin(rssSite);
				feedUploader.beginEach(session, rssSite);
			}
			ExtFeed extFeed = new ExternalRssFeedParser(rssSite.getRssFeedUrl())
					.parse();
			Map<String, List<JRssFeed>> feedsMap = new HashMap<String, List<JRssFeed>>();
			List<JRssFeed> feeds = WebFeedSpiderUtil.converAll(extFeed
					.getEntries(), null);
			feedsMap.put(rssSite.getUniqueId(), feeds);
			if (feedHandler == null) {
				new AllInOneAITaskExecutor(session).executeTasks(feedsMap,
						rssSite);
				feedUploader.endEach(session, rssSite);
			} else {
				if (feedHandler.isExecuteAItaks()) {
					new AllInOneAITaskExecutor(session).executeTasks(feedsMap,
							rssSite);
				}
				feedHandler.handleReceived(feedsMap, session, rssSite);
			}
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		} finally {
			session.end(rssSite);
		}
	}
}
