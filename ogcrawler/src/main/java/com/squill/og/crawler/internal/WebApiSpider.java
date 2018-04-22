package com.squill.og.crawler.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.IWebApi;
import com.squill.og.crawler.Spider;
import com.squill.og.crawler.SpiderSession;
import com.squill.og.crawler.hooks.IApiRequestExecutor;
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.ISpiderSession;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.model.web.JRssFeed;
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
		IFeedUploader feedUploader = session.getFeedUploader();
		try {
			feedUploader.beginEach(session, webApi);
			IApiRequestExecutor apiExecutor = webApi.getApiExecutor();
			JRssFeeds rssFeeds = apiExecutor.execute(webApi.getUniqueId());
			Map<String, List<JRssFeed>> feedsMap = new HashMap<String, List<JRssFeed>>();
			feedsMap.put(webApi.getUniqueId(), rssFeeds.getFeeds());
			AllInOneAITaskExecutor allInOneAITaskExecutor = new AllInOneAITaskExecutor(session);
			feedsMap = allInOneAITaskExecutor.executeTasks(feedsMap, webApi);
			rssFeeds = uniteAll(feedsMap);
			session.addAttr(webApi, ISpiderSession.RSS_FEEDS_KEY, rssFeeds);
			feedUploader.endEach(session, webApi);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		} finally {
			session.done(webApi);
		}
	}
	
	private JRssFeeds uniteAll(Map<String, List<JRssFeed>> feedsMap) {
		JRssFeeds rssFeeds = new JRssFeeds();
		Iterator<String> itr = feedsMap.keySet().iterator();
		while(itr.hasNext()) {
			String key = itr.next();
			List<JRssFeed> values = feedsMap.get(key);
			if(values == null || values.isEmpty())
				continue;
			rssFeeds.getFeeds().addAll(values);
		}
		return rssFeeds;
	}
}
