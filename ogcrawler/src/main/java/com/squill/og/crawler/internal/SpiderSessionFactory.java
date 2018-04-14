package com.squill.og.crawler.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.squill.og.crawler.IWebCrawlable;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.SpiderSession;
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.ISpiderSession;
import com.squill.og.crawler.model.web.JRssFeeds;

public class SpiderSessionFactory {
	
	public static final SpiderSessionFactory INSTANCE = new SpiderSessionFactory();
	
	private SpiderSessionFactory() {
	}

	public ISpiderSession createNewSession(IFeedUploader feedUploader) {
		return new SpiderSessionImpl(feedUploader);
	}
	
	private class SpiderSessionImpl extends SpiderSession {

		private Map<String, Object> attrMap = new HashMap<String, Object>();

		private List<IWebCrawlable> webSites = new ArrayList<IWebCrawlable>();
		
		private SpiderSessionImpl(IFeedUploader feedUploader) {
			super(feedUploader);
		}

		@Override
		public void addAttr(String key, Object value) {
			if (RSS_FEEDS_KEY.equals(key)) {
				attrMap.put(key + "_" + getCurrentWebCrawlable().getUniqueId(), value);
			} else {
				attrMap.put(key, value);
			}
		}

		@Override
		public Object getAttr(String key) {
			return attrMap.get(key);
		}

		@Override
		public JRssFeeds getFeeds(IWebCrawlable webSite) {
			return (JRssFeeds) getAttr(RSS_FEEDS_KEY + "_" + webSite.getUniqueId());
		}

		@Override
		public void done(IWebCrawlable webSite) {
			webSites.add(webSite);
		}
		
		@Override
		public IWebSite[] getAllCompleted() {
			return webSites.toArray(new IWebSite[webSites.size()]);
		}
	}
}
