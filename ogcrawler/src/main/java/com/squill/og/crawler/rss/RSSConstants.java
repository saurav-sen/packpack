package com.squill.og.crawler.rss;

/**
 * 
 * @author Saurav
 *
 */
public interface RSSConstants {

	public static final String OG_TITLE = "og:title"; //$NON-NLS-1$
	public static final String OG_DESCRIPTION = "og:description"; //$NON-NLS-1$
	public static final String OG_IMAGE = "og:image"; //$NON-NLS-1$
	public static final String OG_TYPE = "og:type"; //$NON-NLS-1$
	public static final String OG_URL = "og:url"; //$NON-NLS-1$
	
	public static final long DEFAULT_TTL_WEB_TRACKING_INFO = 3 * 24 * 60 * 60; // 3 Days in Seconds
	
	public static final long DEFAULT_TTL_HEADERS_MEMENTO = 6 * 60 * 60; // 6 Hours in Seconds
}