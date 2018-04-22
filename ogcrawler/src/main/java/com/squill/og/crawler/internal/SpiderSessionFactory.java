package com.squill.og.crawler.internal;

import java.util.HashMap;
import java.util.Map;

import com.squill.og.crawler.IWebCrawlable;
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

		private Map<String, Map<String, Object>> attrMap = new HashMap<String, Map<String, Object>>();

		private SpiderSessionImpl(IFeedUploader feedUploader) {
			super(feedUploader);
		}

		@Override
		public void addAttr(IWebCrawlable webCrawlable, String key, Object value) {
			Map<String, Object> map = attrMap.get(webCrawlable.getUniqueId());
			if(map == null) {
				map = new HashMap<String, Object>();
				attrMap.put(webCrawlable.getUniqueId(), map);
			}
			map.put(key, value);
		}

		@Override
		public Object getAttr(IWebCrawlable webCrawlable, String key) {
			Map<String, Object> map = attrMap.get(webCrawlable.getUniqueId());
			if(map == null)
				return null;
			return map.get(key);
		}

		@Override
		public JRssFeeds getFeeds(IWebCrawlable webCrawlable) {
			return (JRssFeeds) getAttr(webCrawlable, RSS_FEEDS_KEY);
		}

		@Override
		public void done(IWebCrawlable webCrawlable) {
			if(webCrawlable == null)
				return;
			attrMap.remove(webCrawlable.getUniqueId());
			super.done(webCrawlable);
		}
	}
}
