package com.squill.og.crawler.internal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.services.exception.PackPackException;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.og.crawler.IWebCrawlable;
import com.squill.og.crawler.SpiderSession;
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.ISpiderSession;

public class SpiderSessionFactory {
	
	public static final SpiderSessionFactory INSTANCE = new SpiderSessionFactory();
	
	private static final Logger LOG = LoggerFactory.getLogger(SpiderSessionFactory.class);
	
	private SpiderSessionFactory() {
	}

	public ISpiderSession createNewSession(IFeedUploader feedUploader) {
		return new SpiderSessionImpl(feedUploader);
	}
	
	private class SpiderSessionImpl extends SpiderSession {

		private Map<String, Map<String, Object>> attrMap = new HashMap<String, Map<String, Object>>();
		
		private Count crawledCount = new Count();
		
		private long startTime = 0;
		
		private Queue<String> notificationMessages = new LinkedList<String>();
		
		private SpiderSessionImpl(IFeedUploader feedUploader) {
			super(feedUploader);
		}
		
		@Override
		public synchronized void refresh() {
			crawledCount = new Count();
			Iterator<Map<String, Object>> itr = attrMap.values().iterator();
			while(itr.hasNext()) {
				Map<String, Object> next = itr.next();
				if(next != null) {
					next.clear();
				}
			}
			attrMap.clear();
			notificationMessages.clear();
		}
		
		@Override
		public void addNotificationMessage(String message) {
			notificationMessages.offer(message);
		}
		
		@Override
		public String getTopNotificationMessage() {
			return notificationMessages.poll();
		}
		
		@Override
		public boolean hashMoreNotificationMessages() {	
			return notificationMessages.peek() != null;
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
			Count count = crawledCount;
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
			Count count = crawledCount;
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
			
			private String countFilePath;
			
			private TmpJson tmpJson;
			
			private Count() {
				init0();
			}
			
			private void init0() {
				try {
					//countFilePath = System.getProperty(SystemPropertyKeys.WEB_CRAWLERS_BASE_DIR);
					//countFilePath = System.getProperty("user.home");
					countFilePath = System.getProperty("java.io.tmpdir");
					if(!countFilePath.endsWith(File.separator) && !countFilePath.endsWith("/") && !countFilePath.endsWith("\\")) {
						countFilePath = countFilePath + File.separator;
					}
					countFilePath = countFilePath + "tmpCount.json";
					File file = new File(countFilePath);
					if(file.exists()) {
						String content = new String(Files.readAllBytes(Paths.get(countFilePath)));
						tmpJson = JSONUtil.deserialize(content, TmpJson.class, true);
						if(System.currentTimeMillis() >= tmpJson.getExpiryTime()) {
							LOG.debug("Re-setting TmpJson Count to 0");
							tmpJson.setCount(0);
							tmpJson.setExpiryTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000); // Plus 24 hours
							save();
						}
						this.count = tmpJson.getCount();
					} else {
						LOG.debug("Creating new TmpJson Count file");
						tmpJson = new TmpJson();
						tmpJson.setCount(0);
						tmpJson.setExpiryTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000); // Plus 24 hours
						save();
					}
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				} catch (PackPackException e) {
					LOG.error(e.getMessage(), e);
				}
			}

			private int getCount() {
				return count;
			}

			private void setCount(int count) {
				this.count = count;
				if(tmpJson.getCount() != count) {
					LOG.debug("Setting TmpJson Count = " + count);
					tmpJson.setCount(count);
					save();
				}
			}
			
			private void save() {
				try {
					LOG.debug("Trying to save TmpJson file");
					if(tmpJson != null) {
						String json = JSONUtil.serialize(tmpJson);
						LOG.debug("Saving TmpJson = " + json);
						Files.write(Paths.get(countFilePath), json.getBytes());
					} else {
						LOG.debug("Failed saving TmpJson as it is NULL");
					}
				} catch (PackPackException e) {
					LOG.error(e.getMessage(), e);
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}
}
