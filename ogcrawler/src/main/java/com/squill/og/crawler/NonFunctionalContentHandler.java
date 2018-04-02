package com.squill.og.crawler;

import com.squill.og.crawler.hooks.GenSession;
import com.squill.og.crawler.hooks.IGeoLocationResolver;
import com.squill.og.crawler.hooks.IHtmlContentHandler;

/**
 * 
 * @author Saurav
 *
 */
public abstract class NonFunctionalContentHandler implements
		IHtmlContentHandler {

	@Override
	public void preProcess(ILink link, IGeoLocationResolver locationResolver, GenSession session) {
	}

	@Override
	public void postProcess(String content, ILink link,
			IGeoLocationResolver locationResolver, GenSession session) {
		handleContent(content, link);
	}

	@Override
	public void postComplete(GenSession session) {
		handleFlush();
	}

	@Override
	public void flush(GenSession session) {
		handleFlush();
	}

	@Override
	public int getFlushFrequency() {
		return 100;
	}

	@Override
	public int getThresholdFrequency() {
		return -1;
	}

	protected abstract void handleFlush();

	protected abstract void handleContent(String content, ILink link);
}