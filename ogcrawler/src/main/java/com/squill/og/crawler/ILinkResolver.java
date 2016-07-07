package com.squill.og.crawler;

import java.util.Iterator;
import java.util.List;

import javax.script.Invocable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.squill.og.crawler.internal.HtmlPage;

/**
 * 
 * @author Saurav
 * @since 24-Mar-2015
 * 
 */
public interface ILinkResolver {

	public String resolveLink(String jsFunctionName, Invocable jsEngine,
			Element hrefElement, Document dom);

	public String resolveAjaxLink(Element hrefElement, Document dom);
	
	public List<String> computeNonHrefLinks(Document dom);
	
	public Iterator<Element> resolveCrawlableElements(Document dom, HtmlPage htmlPage);
}