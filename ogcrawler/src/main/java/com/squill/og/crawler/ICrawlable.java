package com.squill.og.crawler;

import com.squill.og.crawler.hooks.IFeedHandler;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;

public interface ICrawlable {

	public String getUniqueId();
	
	public boolean isUploadIndependently();
	
	public ICrawlSchedule getSchedule();

	public IWebLinkTrackerService getTrackerService();
	
	public IFeedHandler getFeedHandler();
}
