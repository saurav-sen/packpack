package com.squill.og.crawler.internal.utils;

import crawlercommons.fetcher.http.UserAgent;

/**
 *
 * @author Saurav
 * @since 13-Mar-2015
 *
 */
public interface CoreConstants {

	public static final String USER_AGENT = "Mozilla/5.0"; //$NON-NLS-1$
	public static final String LOCATION_HTTP_HEADER = "location"; //$NON-NLS-1$
	public static final String SET_COOKIE = "set-cookie"; //$NON-NLS-1$
	public static final String HTTP = "http://"; //$NON-NLS-1$
	public static final String HTTPS = "https://"; //$NON-NLS-1$
	
	public static final int BLOCK_SIZE = 1024;
	
	public static final String PDF_FILE_EXT = ".pdf"; //$NON-NLS-1$
	public static final String HTML_FILE_EXT = ".html"; //$NON-NLS-1$
	
	public static final String CONTENT_DISPOSITION = "Content-Disposition"; //$NON-NLS-1$
	public static final String CONTENT_TYPE = "content-type"; //$NON-NLS-1$
	public static final String CONTENT_DISPOSITION_ATTACHMENT = "attachment"; //$NON-NLS-1$
	public static final String ATTCAHMENT_FILE_NAME = "filename"; //$NON-NLS-1$
	
	public static final String EQUAL = "="; //$NON-NLS-1$
	public static final String SEMICOLON = ";"; //$NON-NLS-1$
	public static final String COLON = ":"; //$NON-NLS-1$
	
	public static final String MIME_TYPE_APPLICATION_PDF = "application/pdf"; //$NON-NLS-1$
	public static final String MIME_TYPE_TEXT_HTML = "text/html"; //$NON-NLS-1$
	
	public static final String SCRIPT_TAG = "script"; //$NON-NLS-1$
	public static final String STYLE_TAG = "style"; //$NON-NLS-1$
	public static final String LINK_TAG = "link"; //$NON-NLS-1$
	
	public static final String TEXT_CSS_LINK_TYPE = "text/css";
	
	public static final String JAVA_SCRIPT_TYPE = "text/javascript"; //$NON-NLS-1$
	public static final String SCRIPT_TYPE = "type"; //$NON-NLS-1$
	public static final String SCRIPT_SRC = "src"; //$NON-NLS-1$
	
	public static final String HREF = "href"; //$NON-NLS-1$
	public static final String HYPERLINK_ELEMENT_TAG_NAME = "a"; //$NON-NLS-1$
	
	public static final String JAVA_SCRIPT = "javascript"; //$NON-NLS-1$
	
	public static final UserAgent TROVE_ROBOT = new UserAgent("TroveBots", //$NON-NLS-1$
			"inbox@mymedicalfiles.in", "http://www.mymedicalfiles.in"); //$NON-NLS-1$ //$NON-NLS-2$
	
	public static final String TROVE_ROBOT_USER_AGENT_STRING = "TroveBots; +http://www.mymedicalfiles.in; email: inbox@mymedicalfiles.in"; //$NON-NLS-1$
}