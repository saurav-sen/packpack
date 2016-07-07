package com.squill.og.crawler.internal;

import javax.script.Invocable;

/**
 *
 * @author Saurav
 * @since 20-Mar-2015
 *
 */
public class HtmlPage {

	private String htmlContent;
	
	private Invocable jsEngine;
	
	private String currentLinkContext;
	
	public HtmlPage(String htmlContent, Invocable jsEngine, String currentLinkContext) {
		this.htmlContent = htmlContent;
		this.jsEngine = jsEngine;
		this.currentLinkContext = currentLinkContext;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public Invocable getJsEngine() {
		return jsEngine;
	}

	public String getCurrentLinkContext() {
		return currentLinkContext;
	}

	public void setCurrentLinkContext(String currentLinkContext) {
		this.currentLinkContext = currentLinkContext;
	}
}