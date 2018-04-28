package com.squill.og.crawler;

import java.util.List;
import java.util.Map;

import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.og.crawler.hooks.IHtmlContentHandler;
import com.squill.og.crawler.hooks.ISpiderSession;

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
	public Map<String, List<JRssFeed>> getCollectiveFeeds(ISpiderSession session) {
		// TODO Auto-generated method stub
		return null;
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