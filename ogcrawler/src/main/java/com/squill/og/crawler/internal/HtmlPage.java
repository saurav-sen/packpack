package com.squill.og.crawler.internal;

import javax.script.Invocable;

import org.jsoup.nodes.Document;

/**
 *
 * @author Saurav
 * @since 20-Mar-2015
 *
 */
public class HtmlPage {
	
	private String htmlContent;

	private Document htmlDocument;
	
	private Invocable jsEngine;
	
	private String currentLinkContext;
	
	public HtmlPage(Document htmlDocument, Invocable jsEngine, String currentLinkContext) {
		this(htmlDocument, null, jsEngine, currentLinkContext);
	}
	
	public HtmlPage(Document htmlDocument, String htmlContent, Invocable jsEngine, String currentLinkContext) {
		this.htmlDocument = htmlDocument;
		this.htmlContent = htmlContent;
		this.jsEngine = jsEngine;
		this.currentLinkContext = currentLinkContext;
	}

	public String getHtmlContent() {
		if(htmlContent == null) {
			htmlContent = htmlDocument.body().html();
		}
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

	public Document getHtmlDocument() {
		return htmlDocument;
	}
}