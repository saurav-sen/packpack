package com.squill.og.crawler.hooks;

import com.squill.og.crawler.ILink;
import com.squill.og.crawler.model.web.JRssFeeds;

/**
 * 
 * @author Saurav
 *
 */
public interface IHtmlContentHandler {

	public void preProcess(ILink link, ISpiderSession session);

	public void postProcess(String htmlContent, ILink link, ISpiderSession session);

	public JRssFeeds postComplete(ISpiderSession session);

	public int getFlushFrequency();

	public int getThresholdFrequency();

	public void setFlushFrequency(int flushFrequency);

	public void setThresholdFrequency(int thresholdFrequency);

	public void addMetaInfo(String key, Object value);
}