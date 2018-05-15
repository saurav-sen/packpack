package com.squill.og.crawler.hooks;

/**
 * 
 * @author Saurav
 *
 */
public interface ILinkFilter {

	public boolean isScoped(String linkUrl);
	
	public boolean isScopedSitemapUrl(String sitemapUrl);
}
