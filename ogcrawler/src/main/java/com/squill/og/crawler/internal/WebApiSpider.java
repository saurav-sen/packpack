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
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;

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
		if(session.isThresholdReached())
			return;
		IFeedUploader feedUploader = session.getFeedUploader();
		try {
			session.begin(webApi);
			feedUploader.beginEach(session, webApi);
			IApiRequestExecutor apiExecutor = webApi.getApiExecutor();
			Map<String, List<JRssFeed>> feedsMap = apiExecutor.execute(webApi.getUniqueId());
			new AllInOneAITaskExecutor(session).executeTasks(feedsMap, webApi);
			//AllInOneAITaskExecutor allInOneAITaskExecutor = new AllInOneAITaskExecutor(session);
			//feedsMap = allInOneAITaskExecutor.executeTasks(feedsMap, webApi);
			//JRssFeeds rssFeeds = uniteAll(feedsMap);
			//session.addAttr(webApi, ISpiderSession.RSS_FEEDS_KEY, rssFeeds);
			feedUploader.endEach(session, webApi);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		} finally {
			session.end(webApi);
		}
	}
	
	/*private JRssFeeds uniteAll(Map<String, List<JRssFeed>> feedsMap) {
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
	}*/
}
