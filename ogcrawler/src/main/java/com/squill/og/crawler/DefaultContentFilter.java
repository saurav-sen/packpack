package com.squill.og.crawler;

import com.squill.og.crawler.internal.HtmlPage;

/**
 * 
 * @author Saurav
 *
 */
public abstract class DefaultContentFilter implements IContentFilter {

	@Override
	public boolean ignoreJSLinks() {
		return false;
	}

	@Override
	public String getDownloadableContent(HtmlPage htmlPage) {
		return htmlPage.getHtmlContent();
	}
}