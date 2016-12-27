package com.squill.og.crawler;

import com.squill.og.crawler.hooks.IHtmlContentHandler;


/**
 * 
 * @author Saurav
 *
 */
public interface IWebSite {

	public String getDomainUrl();
	
	public IRobotScope getRobotScope();
	
	public IHtmlContentHandler getContentHandler();
	
	//public WebSpiderTracker createNewTracker(String link);
	
	public ICrawlSchedule getSchedule();
	
	public boolean shouldCheckRobotRules();
}