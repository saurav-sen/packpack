package com.squill.og.crawler;

import com.squill.og.crawler.hooks.IHtmlContentHandler;

/**
 * 
 * @author Saurav
 *
 */
public interface IWebSite extends IWebCrawlable {

	public IRobotScope getRobotScope();

	public IHtmlContentHandler getContentHandler();

	public boolean shouldCheckRobotRules();
}