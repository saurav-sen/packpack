package com.squill.og.crawler;


/**
 * 
 * @author Saurav
 *
 */
public abstract class NonFunctionalContentHandler implements
		IHtmlContentHandler {

	@Override
	public void preProcess(ILink link) {
	}

	@Override
	public void postProcess(String content, ILink link) {
		handleContent(content, link);
	}

	@Override
	public void postComplete() {
		handleFlush();
	}

	@Override
	public void flush() {
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