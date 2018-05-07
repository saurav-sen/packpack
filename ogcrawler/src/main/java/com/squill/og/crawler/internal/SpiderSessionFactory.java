package com.squill.og.crawler.internal;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import com.squill.feed.web.model.JRssFeeds;
import com.squill.og.crawler.IWebCrawlable;
import com.squill.og.crawler.SpiderSession;
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.ISpiderSession;

public class SpiderSessionFactory {
	
	public static final SpiderSessionFactory INSTANCE = new SpiderSessionFactory();
	
	private SpiderSessionFactory() {
	}

	public ISpiderSession createNewSession(IFeedUploader feedUploader) {
		return new SpiderSessionImpl(feedUploader);
	}
	
	private class SpiderSessionImpl extends SpiderSession {

		private Map<String, Map<String, Object>> attrMap = new HashMap<String, Map<String, Object>>();
		
		private SoftReference<Count> crawledCount = new SoftReference<Count>(new Count(0));
		
		private long startTime = 0;
		
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
		public void end(IWebCrawlable webCrawlable) {
			if(webCrawlable == null)
				return;
			attrMap.remove(webCrawlable.getUniqueId());
			super.end(webCrawlable);
		}

		@Override
		public synchronized boolean isThresholdReached() {
			Count count = crawledCount.get();
			if(count.getCount() < 950) { // To be safe lets consider 950 instead of 1000 per day.
				return false;
			} else {
				if(startTime == 0) {
					startTime = System.currentTimeMillis();
					count.setCount(0);
					return false;
				}
				long currentTime = System.currentTimeMillis();
				long oneDay = 25 * 60 * 60 * 1000; // To be safe lets consider 25 hours.
				long diff = currentTime - startTime;
				if(diff > oneDay) {
					startTime = currentTime;
					count.setCount(0);
					return false;
				}
				return true;
			}
		}

		@Override
		public synchronized void incrementCrawledCount(int incCountBy) {
			Count count = crawledCount.get();
			int c = count.getCount();
			if(c <= 0) {
				c = 0;
			}
			if(startTime == 0) {
				startTime = System.currentTimeMillis();
			} else {
				long currentTime = System.currentTimeMillis();
				long oneDay = 25 * 60 * 60 * 1000; // To be safe lets consider 25 hours.
				long diff = currentTime - startTime;
				if(diff > oneDay) {
					startTime = System.currentTimeMillis();
					c = 0;
				}
			}
			c = c + incCountBy;
			count.setCount(c);
		}
		
		private class Count {
			
			private int count;
			
			private Count(int count) {
				this.count = count;
			}

			private int getCount() {
				return count;
			}

			private void setCount(int count) {
				this.count = count;
			}
		}
	}
}
