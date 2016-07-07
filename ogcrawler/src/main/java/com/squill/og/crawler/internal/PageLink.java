package com.squill.og.crawler.internal;

import java.util.Collections;
import java.util.List;

import com.squill.og.crawler.ILink;
import com.squill.og.crawler.internal.utils.CoreConstants;



/**
 * 
 * @author Saurav
 *
 */
public class PageLink implements ILink {

	private String link;
	private String text;
	
	private CrawlContext context;
	
	private HtmlPage parent;
	
	public PageLink() {
		this(null);
	}
	
	public PageLink(String link) {
		this(link, null);
	}
	
	public PageLink(String link, String text) {
		setLink(link);
		setText(text);
	}

	public String getLink() {
		String hyperlink = link.replaceAll(" ", "%20");
		if(hyperlink.startsWith(CoreConstants.HTTP) || hyperlink.startsWith(CoreConstants.HTTPS)) {
			return hyperlink;
		}
		if(parent == null)
			return hyperlink;
		String separator = "";
		String linkContext = parent.getCurrentLinkContext();
		String cLink = linkContext;
		int count = 0;
		while(hyperlink.startsWith("../")) {
			if(cLink.endsWith("/")) {
				cLink = cLink.substring(0, cLink.length()-2);
			}
			hyperlink = hyperlink.substring(3);
			if(count > 0) {
				cLink = cLink.substring(0, cLink.lastIndexOf("/"));
			}
			count++;
		}
		linkContext = cLink;
		if(!linkContext.endsWith("/") && !hyperlink.startsWith("/")) {
			separator = "/";
		}
		return linkContext + separator + hyperlink;
	}
	
	/*public static void main(String[] args) {
		HtmlPage parent = new HtmlPage("", null, "http://www.domain.com/mypage/mypage1/");
		PageLink p1 = new PageLink("../abc.html");
		p1.setParent(parent);
		System.out.println(p1.getUrl());
		p1 = new PageLink("../../abc.html");
		p1.setParent(parent);
		System.out.println(p1.getUrl());
	}*/

	public void setLink(String link) {
		this.link = replaceInvalidChar(link);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public CrawlContext getContext() {
		return context;
	}

	public void setContext(CrawlContext context) {
		this.context = context;
	}

	private String replaceInvalidChar(String link) {
		link = link.replaceAll("'", "");
		link = link.replaceAll("\"", "");
		return link;
	}
	
	@Override
	public String toString() {
		return new StringBuffer("Link: ").append(this.link).toString();
	}

	public HtmlPage getParent() {
		return parent;
	}

	public void setParent(HtmlPage parent) {
		this.parent = parent;
	}

	@Override
	public String getUrl() {
		return getLink();
		//return link;
	}

	@Override
	public List<String> getTags() {
		return Collections.emptyList();
	}
}