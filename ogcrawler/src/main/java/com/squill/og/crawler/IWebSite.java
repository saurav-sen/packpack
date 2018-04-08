package com.squill.og.crawler;

import com.squill.og.crawler.hooks.IGeoLocationResolver;
import com.squill.og.crawler.hooks.IHtmlContentHandler;
import com.squill.og.crawler.hooks.ITaxonomyResolver;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;

/**
 * 
 * @author Saurav
 *
 */
public interface IWebSite {

	public String getUniqueId();
	
	public String getDomainUrl();

	public IRobotScope getRobotScope();

	public IHtmlContentHandler getContentHandler();

	// public WebSpiderTracker createNewTracker(String link);

	public ICrawlSchedule getSchedule();

	public boolean shouldCheckRobotRules();

	public IWebLinkTrackerService getTrackerService();

	public IGeoLocationResolver getTargetLocationResolver();
	
	public ITaxonomyResolver getTaxonomyResolver();
}