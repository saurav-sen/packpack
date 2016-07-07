package com.squill.og.crawler;

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
}