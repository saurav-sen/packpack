package com.squill.og.crawler;

import org.jsoup.nodes.Document;

import com.squill.og.crawler.internal.HtmlPage;

/**
 *
 * @author Saurav
 * @since 19-Mar-2015
 *
 */
public interface IContentFilter {

	public boolean isDownloadable(Document htmlPageContent);
	
	public String[] getSupportedDomainNames();
	
	public boolean isCrawlable(String link);
	
	public boolean ignoreJSLinks();
	
	public String getDownloadableContent(HtmlPage htmlPage);
}