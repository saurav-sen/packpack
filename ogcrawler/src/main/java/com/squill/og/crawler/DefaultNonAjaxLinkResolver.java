package com.squill.og.crawler;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.squill.og.crawler.internal.HtmlPage;
import com.squill.og.crawler.internal.utils.CoreConstants2;


/**
 *
 * @author Saurav
 * @since 24-Mar-2015
 *
 */
public class DefaultNonAjaxLinkResolver implements ILinkResolver {

	@Override
	public String resolveLink(String jsFunctionName, Invocable jsEngine,
			Element hrefElement, Document dom) {
		try {
			return resolveLink(jsFunctionName, jsEngine);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String resolveLink(String functionLink, Invocable jsEngine) throws NoSuchMethodException, ScriptException {
		if(jsEngine == null)
			return null;
		int index = functionLink.indexOf("(");
		String functionName = functionLink.substring(0,index);
		int index2 = functionLink.indexOf(")");
		String str = functionLink.substring(index + 1, index2);
		String[] arguments = str.split(",");
		if(arguments != null) {
			Object[] args = new Object[arguments.length];
			for(int i=0; i<arguments.length; i++) {
				String s = arguments[i];
				if(isInteger(s)) {
					args[i] = Integer.parseInt(s);
				}
				else if(isLong(s)) {
					args[i] = Long.parseLong(s);
				}
				else if(isFloat(s)) {
					args[i] = Float.parseFloat(s);
				}
				else if(isDouble(s)) {
					args[i] = Double.parseDouble(s);
				}
				else {
					args[i] = s;
				}
			}
			Object result = jsEngine.invokeFunction(functionName, args);
			return result.toString();
		}
		Object result = jsEngine.invokeFunction(functionName);
		return result.toString();
	}
	
	private boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private boolean isLong(String str) {
		try {
			Long.parseLong(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private boolean isFloat(String str) {
		try {
			Float.parseFloat(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public String resolveAjaxLink(Element hrefElement, Document dom) {
		throw new UnsupportedOperationException("AJAX Crawling is not supported.");
	}

	@Override
	public List<String> computeNonHrefLinks(Document dom) {
		return Collections.emptyList();
	}

	@Override
	public Iterator<Element> resolveCrawlableElements(Document dom, HtmlPage htmlPage) {
		Elements elementsByTag = dom.body().getElementsByTag(
				CoreConstants2.HYPERLINK_ELEMENT_TAG_NAME);
		return elementsByTag.iterator();
	}
}