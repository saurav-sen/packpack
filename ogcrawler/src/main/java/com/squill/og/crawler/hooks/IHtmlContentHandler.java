package com.squill.og.crawler.hooks;

import com.squill.og.crawler.ILink;

/**
 * 
 * @author Saurav
 *
 */
public interface IHtmlContentHandler {
	
	public void preProcess(ILink link);

	public void postProcess(String htmlContent, ILink link);
	
	public void postComplete();
	
	public void flush();
	
	public int getFlushFrequency();
	
	public int getThresholdFrequency();
	
	public void setFlushFrequency(int flushFrequency);
	
	public void setThresholdFrequency(int thresholdFrequency);
	
	public void setFeedUploader(IFeedUploader feedUploader);
	
	public boolean needToClassifyFeeds();
}