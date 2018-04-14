package com.squill.og.crawler;

import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.ISpiderSession;

public abstract class SpiderSession implements ISpiderSession {
	
	private ThreadLocal<IWebCrawlable> threadLocal = new ThreadLocal<IWebCrawlable>();

	private IFeedUploader feedUploader;
	
	protected SpiderSession(IFeedUploader feedUploader) {
		this.feedUploader = feedUploader;
	}
	
	public void setCurrentWebCrawlable(IWebCrawlable currentWebSite) {
		threadLocal.set(currentWebSite);
	}
	
	@Override
	public IWebCrawlable getCurrentWebCrawlable() {
		return threadLocal.get();
	}
	
	public IFeedUploader getFeedUploader() {
		return feedUploader;
	}
}
