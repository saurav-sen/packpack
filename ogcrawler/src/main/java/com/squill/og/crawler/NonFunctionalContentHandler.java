package com.squill.og.crawler;

import com.squill.og.crawler.hooks.ISpiderSession;
import com.squill.og.crawler.hooks.IHtmlContentHandler;
import com.squill.og.crawler.model.web.JRssFeeds;

/**
 * 
 * @author Saurav
 *
 */
public abstract class NonFunctionalContentHandler implements
		IHtmlContentHandler {

	@Override
	public void preProcess(ILink link, ISpiderSession session) {
	}

	@Override
	public void postProcess(String content, ILink link, ISpiderSession session) {
		handleContent(content, link);
	}

	@Override
	public JRssFeeds postComplete(ISpiderSession session) {
		return handleFlush();
	}

	@Override
	public int getFlushFrequency() {
		return 100;
	}

	@Override
	public int getThresholdFrequency() {
		return -1;
	}

	protected abstract JRssFeeds handleFlush();

	protected abstract void handleContent(String content, ILink link);
}