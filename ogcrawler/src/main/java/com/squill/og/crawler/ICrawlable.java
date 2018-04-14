package com.squill.og.crawler;

import com.squill.og.crawler.hooks.IWebLinkTrackerService;

public interface ICrawlable {

	public String getUniqueId();

	public ICrawlSchedule getSchedule();

	public IWebLinkTrackerService getTrackerService();
}
