package com.squill.og.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.hooks.IApiRequestExecutor;
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.ISpiderSession;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.model.web.JRssFeeds;

public class WebApiSpider implements Spider {

	private IWebApi webApi;
	
	private static Logger LOG = LoggerFactory.getLogger(WebApiSpider.class);
	
	private SpiderSession session;
	
	public WebApiSpider(IWebApi webApi, long crawlSchedulePeriodicTimeInMillis, IWebLinkTrackerService tracker, SpiderSession session) {
		this.webApi = webApi;
		this.session = session;
	}

	@Override
	public void run() {
		session.setCurrentWebCrawlable(webApi);
		IFeedUploader feedUploader = session.getFeedUploader();
		try {
			feedUploader.preProcess(session);
			IApiRequestExecutor apiExecutor = webApi.getApiExecutor();
			JRssFeeds rssFeeds = apiExecutor.execute(webApi.getUniqueId());
			session.addAttr(ISpiderSession.RSS_FEEDS_KEY, rssFeeds);
			feedUploader.postComplete(session, session.getCurrentWebCrawlable());
			session.done(webApi);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
