package com.squill.og.crawler;

import com.squill.og.crawler.hooks.IHtmlContentHandler;

/**
 * 
 * @author Saurav
 *
 */
public interface IWebSite extends IWebCrawlable {
	
	public String getDomainUrl();

	public IRobotScope getRobotScope();

	public IHtmlContentHandler getContentHandler();

	public boolean shouldCheckRobotRules();
	
	public boolean isPageLinkExtractorEnabled();
	
	public void enablePageLinkExtractor();
	
	public void disablePageLinkExtractor();
}