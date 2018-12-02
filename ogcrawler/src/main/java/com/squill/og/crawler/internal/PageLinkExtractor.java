package com.squill.og.crawler.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.squill.og.crawler.DefaultNonAjaxLinkResolver;
import com.squill.og.crawler.ILinkResolver;
import com.squill.og.crawler.IRobotScope;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.internal.utils.CoreConstants2;

/**
 * 
 * @author Saurav
 * 
 */
public class PageLinkExtractor {

	private PageLinkFilter linkFilter;
	private ILinkResolver linkResolver;
	
	public PageLinkExtractor() {
		this(null, null);
	}

	public PageLinkExtractor(IRobotScope contentFilter, ILinkResolver linkResolver) {
		linkFilter = new PageLinkFilter(contentFilter);
		this.linkResolver = linkResolver;
	}

	/**
	 * Retrieves all page link for crawling from HTML content
	 * 
	 * @param html
	 *            html content for validation
	 * @param root
	 * @return List links and link text
	 * @throws ScriptException 
	 * @throws NoSuchMethodException 
	 */
	public List<PageLink> extractAllPageLinks(HtmlPage htmlPage, IWebSite root) throws NoSuchMethodException, ScriptException {
		List<PageLink> result = new ArrayList<PageLink>();
		String html = htmlPage.getHtmlContent();
		Invocable jsEngine = htmlPage.getJsEngine();
		Document document = Jsoup.parse(html);
		/*Elements linkElements = document.body().getElementsByTag(
				CoreConstants.HYPERLINK_ELEMENT_TAG_NAME);*/
		if(linkResolver == null) {
			linkResolver = new DefaultNonAjaxLinkResolver();
		}
		//Iterator<Element> itr = linkElements.iterator();
		Iterator<Element> itr = linkResolver.resolveCrawlableElements(document, htmlPage);
		while (itr.hasNext()) {
			Element linkElement = itr.next();
			String link = linkElement.attr(CoreConstants2.HREF);
			if (link == null)
				continue;
			if(jsEngine != null) {
				if (link.startsWith(CoreConstants2.JAVA_SCRIPT + CoreConstants2.COLON)) {
					int index = link.indexOf(CoreConstants2.JAVA_SCRIPT
							+ CoreConstants2.COLON);
					link = link.substring(index + 1);
					link = linkResolver.resolveLink(link, jsEngine, linkElement, document);
				} else if (link.startsWith(CoreConstants2.JAVA_SCRIPT
						+ CoreConstants2.SEMICOLON)) {
					int index = link.indexOf(CoreConstants2.JAVA_SCRIPT
							+ CoreConstants2.SEMICOLON);
					link = link.substring(index + 1);
					link = linkResolver.resolveLink(link, jsEngine, linkElement, document);
				}
				if(link == null)
					continue;
			}
			String text = linkElement.text();
			PageLink pageLink = new PageLink(link, text, root);
			//pageLink.setContext(ctx);
			pageLink.setParent(htmlPage);
			if (linkFilter.isCrawlable(pageLink)) {
				result.add(pageLink);
			}
		}
		List<String> computeNonHrefLinks = linkResolver.computeNonHrefLinks(document);
		if(computeNonHrefLinks != null && !computeNonHrefLinks.isEmpty()) {
			for(String computedNonHrefLink : computeNonHrefLinks) {
				PageLink pageLink = new PageLink(computedNonHrefLink, null);
				//pageLink.setContext(ctx);
				pageLink.setParent(htmlPage);
				if (linkFilter.isCrawlable(pageLink)) {
					result.add(pageLink);
				}
			}
		}
		return result;
	}
}