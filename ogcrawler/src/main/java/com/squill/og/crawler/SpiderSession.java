package com.squill.og.crawler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.ISpiderSession;

public abstract class SpiderSession implements ISpiderSession {
	
	private IFeedUploader feedUploader;
	
	private Map<String, Object> sessionAttributes = new HashMap<String, Object>();
	
	private int count = 0;
	
	private static final Logger LOG = LoggerFactory.getLogger(SpiderSession.class);
	
	protected SpiderSession(IFeedUploader feedUploader) {
		this.feedUploader = feedUploader;
	}
	
	public IFeedUploader getFeedUploader() {
		return feedUploader;
	}
	
	@Override
	public synchronized void begin(IWebCrawlable webCrawlable) {
		if(count == 0) {
			addSessionAttr(BATCH_ID_KEY, System.currentTimeMillis());
		}
		count++;
	}
	
	@Override
	public synchronized void end(IWebCrawlable webSite) {
		if(count > 0) {
			count--;
		}
		if(count == 0) {
			sessionAttributes.clear();
		}
	}
	
	@Override
	public void addSessionAttr(String key, Object value) {
		sessionAttributes.put(key, value);
	}

	@Override
	public Object getSessionAttr(String key) {
		return sessionAttributes.get(key);
	}
	
	@Override
	public long getBatchId() {
		Object attr = getSessionAttr(BATCH_ID_KEY);
		if(attr == null)
			return -1;
		try {
			return Long.parseLong(attr.toString());
		} catch (NumberFormatException e) {
			LOG.error(e.getMessage(), e);
			return -1;
		}
	}
}
