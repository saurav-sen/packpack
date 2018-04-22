package com.squill.og.crawler;

import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.ISpiderSession;

public abstract class SpiderSession implements ISpiderSession {
	
	private IFeedUploader feedUploader;
	
	protected SpiderSession(IFeedUploader feedUploader) {
		this.feedUploader = feedUploader;
	}
	
	public IFeedUploader getFeedUploader() {
		return feedUploader;
	}
	
	@Override
	public void done(IWebCrawlable webSite) {
	}
}
