package com.squill.og.crawler.hooks;

import com.squill.og.crawler.IWebCrawlable;



/**
 * 
 * @author Saurav
 *
 */
public interface IFeedUploader {

	public void beginEach(ISpiderSession session, IWebCrawlable webCrawlable);
	
	public void endEach(ISpiderSession session, IWebCrawlable webCrawlable);
}