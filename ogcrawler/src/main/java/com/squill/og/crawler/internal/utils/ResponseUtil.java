package com.squill.og.crawler.internal.utils;

import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.squill.og.crawler.IContentFilter;
import com.squill.og.crawler.internal.HtmlPage;
import com.squill.og.crawler.internal.proxy.ProxyDocument;

/**
 *
 * @author Saurav
 * @since 13-Mar-2015
 *
 */
public class ResponseUtil {

	public static String getResponseBodyContent(HttpResponse response) throws ParseException, IOException {
		return EntityUtils.toString(response.getEntity());
	}
	
	public static HtmlPage getParseableHtml(String hostURLPrefix, String html, String pageLink, IContentFilter filter) throws ClientProtocolException, IOException {
		Document document = Jsoup.parse(html);
		document.head().getElementsByTag(CoreConstants.STYLE_TAG).remove();
		document.head().getElementsByTag(CoreConstants.LINK_TAG).remove();
		document.body().getElementsByTag(CoreConstants.STYLE_TAG).remove();
		document.body().getElementsByTag(CoreConstants.LINK_TAG).remove();
		Invocable jsEngine = null;
		if(!filter.ignoreJSLinks()) {
			downloadScriptsInline(hostURLPrefix, document);
			jsEngine = initializeJSEngine(document);
		}
		document.head().getElementsByTag(CoreConstants.SCRIPT_TAG).remove();
		document.body().getElementsByTag(CoreConstants.SCRIPT_TAG).remove();
		String htmlContent = document.body().html();
		String link = "";
		if(pageLink != null) {
			link = pageLink.substring(0, pageLink.length() - 2);
			int index = link.lastIndexOf("/");
			if(index == -1) {
				index = link.length();
			}
			link = link.substring(0, index);
		}
		return new HtmlPage(htmlContent, jsEngine, link);
	}
	
	public static HtmlPage getParseableHtml(String html, String pageLink) throws ClientProtocolException, IOException {
		Document document = Jsoup.parse(html);
		document.head().getElementsByTag(CoreConstants.STYLE_TAG).remove();
		Elements linkElements = document.head().getElementsByTag(CoreConstants.LINK_TAG);
		if(linkElements != null && !linkElements.isEmpty()) {
			for(int i=0; i<linkElements.size(); i++) {
				Element linkElement = linkElements.get(i);
				String linkType = linkElement.attr("type");
				if(CoreConstants.TEXT_CSS_LINK_TYPE.equalsIgnoreCase(linkType)) {
					linkElement.remove();
				}
			}
		}
		document.body().getElementsByTag(CoreConstants.STYLE_TAG).remove();
		linkElements = document.body().getElementsByTag(CoreConstants.LINK_TAG);
		if(linkElements != null && !linkElements.isEmpty()) {
			for(int i=0; i<linkElements.size(); i++) {
				Element linkElement = linkElements.get(i);
				String linkType = linkElement.attr("type");
				if(CoreConstants.TEXT_CSS_LINK_TYPE.equalsIgnoreCase(linkType)) {
					linkElement.remove();
				}
			}
		}
		document.head().getElementsByTag(CoreConstants.SCRIPT_TAG).remove();
		document.body().getElementsByTag(CoreConstants.SCRIPT_TAG).remove();
		String htmlContent = document.html();
		String link = "";
		if(pageLink != null) {
			link = pageLink.substring(0, pageLink.length() - 2);
			int index = link.lastIndexOf("/");
			if(index == -1) {
				index = link.length();
			}
			link = link.substring(0, index);
		}
		return new HtmlPage(htmlContent, null, link);
	}
	
	private static Invocable initializeJSEngine(Document document) {
		Elements scripts = document.head().getElementsByTag(CoreConstants.SCRIPT_TAG);
		StringBuilder jsScript = new StringBuilder();
		if(scripts != null && !scripts.isEmpty()) {
			for(Element script : scripts) {
				jsScript.append(script.html());
				jsScript.append("\n");
			}
		}
		jsScript.append("\n");
		scripts = document.body().getElementsByTag(CoreConstants.SCRIPT_TAG);
		if(scripts != null && !scripts.isEmpty()) {
			for(Element script : scripts) {
				jsScript.append(script.html());
				jsScript.append("\n");
			}
		}
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		engine.put("document", new ProxyDocument(document));
		System.out.println("JS Script: " + jsScript.toString());
		try {
			engine.eval(jsScript.toString());
		} catch (ScriptException e) {
			//TODO -- LOG error here for JS Parsing.
			System.out.println("Failed to Parse JS... Syntax error.. ");
			System.out.println(e.getMessage());
			return null;
		}
		return (Invocable) engine;
	}
	
	private static void downloadScriptsInline(String hostURLPrefix, Document document) throws ClientProtocolException, IOException {
		Elements scripts = document.head().getElementsByTag(CoreConstants.SCRIPT_TAG);
		downloadScriptsInline(hostURLPrefix, scripts);
		scripts = document.body().getElementsByTag(CoreConstants.SCRIPT_TAG);
		downloadScriptsInline(hostURLPrefix, scripts);
	}
	
	private static void downloadScriptsInline(String hostURLPrefix, Elements scripts) throws ClientProtocolException, IOException {
		if(scripts != null && !scripts.isEmpty()) {
			for(Element script : scripts) {
				String scriptType = script.attr(CoreConstants.SCRIPT_TYPE);
				if(scriptType != null && !scriptType.equalsIgnoreCase(CoreConstants.JAVA_SCRIPT_TYPE)) {
					String language = script.attr("language");
					if(language == null || language.trim().equals(""))
						continue;
				}
				String link = script.attr(CoreConstants.SCRIPT_SRC);
				if(link != null && !link.trim().equals("")) {
					if(link.startsWith("/")) {
						String s = hostURLPrefix;
						if(s.endsWith("/")) {
							s = s.substring(0, s.lastIndexOf("/"));
						}
						link = s + link;
					}
					else {
						String s = hostURLPrefix;
						if(!s.endsWith("/")) {
							s = s + "/";
						}
						link = s + link;
					}
					downloadScriptInline(script, link);
				}
			}
		}
	}
	
	private static void downloadScriptInline(Element script, String link) throws ClientProtocolException, IOException {
		System.out.println("Downloading: " + link);
		HttpGet get = new HttpGet(link);
		HttpResponse response = new DefaultHttpClient().execute(get);
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode >= 200 && responseCode < 300) { // OK
			String scriptBody = getResponseBodyContent(response);
			script.appendText(scriptBody);
			script.removeAttr(CoreConstants.SCRIPT_SRC);
		}
	}
}