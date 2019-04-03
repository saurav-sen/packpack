package com.squill.og.crawler.internal;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.feed.web.model.JRssFeed;
import com.squill.og.crawler.IWebApi;
import com.squill.og.crawler.Spider;
import com.squill.og.crawler.SpiderSession;
import com.squill.og.crawler.hooks.IApiRequestExecutor;
import com.squill.og.crawler.hooks.IFeedHandler;
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;

public class WebApiSpider implements Spider {

	private IWebApi webApi;
	
	private static Logger LOG = LoggerFactory.getLogger(WebApiSpider.class);
	
	private SpiderSession session;
	
	private IFeedHandler feedHandler;
	
	public WebApiSpider(IWebApi webApi, long crawlSchedulePeriodicTimeInMillis, IWebLinkTrackerService tracker, IFeedHandler feedHandler, SpiderSession session) {
		this.webApi = webApi;
		this.feedHandler = feedHandler;
		this.session = session;
	}

	@Override
	public void run() {
		if(session.isThresholdReached())
			return;
		IFeedUploader feedUploader = session.getFeedUploader();
		try {
			if (feedHandler == null) {
				session.begin(webApi);
				feedUploader.beginEach(session, webApi);
			}
			IApiRequestExecutor apiExecutor = webApi.getApiExecutor();
			Map<String, List<JRssFeed>> feedsMap = apiExecutor.execute(webApi.getUniqueId());
			if (feedHandler == null) {
				new AllInOneAITaskExecutor(session).executeTasks(feedsMap,
						webApi);
				feedUploader.endEach(session, webApi);
			} else {
				if(feedHandler.isExecuteAItaks()) {
					new AllInOneAITaskExecutor(session).executeTasks(feedsMap,
							webApi);
				}
				feedHandler.handleReceived(feedsMap, session, webApi);
			}
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		} finally {
			session.end(webApi);
		}
	}
}
