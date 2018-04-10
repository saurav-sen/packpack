package com.squill.og.crawler.hooks;

import com.squill.og.crawler.IWebSite;


/**
 * 
 * @author Saurav
 *
 */
public interface IFeedUploader {

	public void preProcess(ISpiderSession session);
	
	public void addConfig(String key, String value);
	
	public void flush(ISpiderSession session);
	
	public void postComplete(ISpiderSession session, IWebSite webSite);
	
	public void postCompleteAll(ISpiderSession session);
}