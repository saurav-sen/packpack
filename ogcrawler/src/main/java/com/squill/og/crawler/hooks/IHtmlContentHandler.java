package com.squill.og.crawler.hooks;

import com.squill.og.crawler.ILink;

/**
 * 
 * @author Saurav
 *
 */
public interface IHtmlContentHandler {

	public void preProcess(ILink link, GenSession session);

	public void postProcess(String htmlContent, ILink link, GenSession session);

	public void postComplete(GenSession session);

	public void flush(GenSession session);

	public int getFlushFrequency();

	public int getThresholdFrequency();

	public void setFlushFrequency(int flushFrequency);

	public void setThresholdFrequency(int thresholdFrequency);

	public void setFeedUploader(IFeedUploader feedUploader);

	public void addMetaInfo(String key, Object value);
}