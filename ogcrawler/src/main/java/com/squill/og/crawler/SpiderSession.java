package com.squill.og.crawler;

import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.ISpiderSession;

public abstract class SpiderSession implements ISpiderSession {
	
	private ThreadLocal<IWebSite> threadLocal = new ThreadLocal<IWebSite>();

	private IFeedUploader feedUploader;
	
	protected SpiderSession(IFeedUploader feedUploader) {
		this.feedUploader = feedUploader;
	}
	
	public void setCurrentWebSite(IWebSite currentWebSite) {
		threadLocal.set(currentWebSite);
	}
	
	@Override
	public IWebSite getCurrentWebSite() {
		return threadLocal.get();
	}
	
	public IFeedUploader getFeedUploader() {
		return feedUploader;
	}
}
