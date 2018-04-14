package com.squill.og.crawler.hooks;

import com.squill.og.crawler.IWebCrawlable;


/**
 * 
 * @author Saurav
 *
 */
public interface IFeedUploader {

	public void preProcess(ISpiderSession session);
	
	public void addConfig(String key, String value);
	
	public void flush(ISpiderSession session);
	
	public void postComplete(ISpiderSession session, IWebCrawlable webSite);
	
	public void postCompleteAll(ISpiderSession session);
}