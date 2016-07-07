package com.squill.og.crawler;

import org.apache.http.client.CookieStore;

/**
 *
 * @author Saurav
 * @since 13-Mar-2015
 *
 */
public class LoginStatus {

	private String htmlContent;
	
	private CookieStore cookieStore;
	
	private int httpStatusCode;
	
	private String hostURLPrefix;
	
	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public CookieStore getCookieStore() {
		return cookieStore;
	}

	public void setCookieStore(CookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	public String getHostURLPrefix() {
		return hostURLPrefix;
	}

	public void setHostURLPrefix(String hostURLPrefix) {
		this.hostURLPrefix = hostURLPrefix;
	}
}