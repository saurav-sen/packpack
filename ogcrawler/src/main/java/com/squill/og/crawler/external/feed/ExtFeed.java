package com.squill.og.crawler.external.feed;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class ExtFeed {

	private String title;

	private String link;

	private String description;

	private String language;

	private String copyright;

	private String pubDate;

	private List<ExtFeedEntry> entries;

	public ExtFeed(String title, String link, String description,
			String language, String copyright, String pubDate) {
		this.title = title;
		this.link = link;
		this.description = description;
		this.language = language;
		this.copyright = copyright;
		this.pubDate = pubDate;
	}

	public List<ExtFeedEntry> getEntries() {
		if (entries == null) {
			entries = new ArrayList<ExtFeedEntry>();
		}
		return entries;
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public String getDescription() {
		return description;
	}

	public String getLanguage() {
		return language;
	}

	public String getCopyright() {
		return copyright;
	}

	public String getPubDate() {
		return pubDate;
	}

	@Override
	public String toString() {
		StringBuilder toString = new StringBuilder();
		toString.append("{");
		toString.append("\"copyright\": \"");
		toString.append(copyright);
		toString.append("\"");
		toString.append(", \"description\": \"");
		toString.append(description);
		toString.append("\"");
		toString.append(", \"language\": \"");
		toString.append(language);
		toString.append("\"");
		toString.append(", \"link\": \"");
		toString.append(link);
		toString.append("\"");
		toString.append(", \"pubDate\": \"");
		toString.append(pubDate);
		toString.append("\"");
		toString.append(", \"title\": \"");
		toString.append(title);
		toString.append("\"");
		toString.append(", \"entries\": ");
		toString.append("[");
		Iterator<ExtFeedEntry> itr = getEntries().iterator();
		while(itr.hasNext()) {
			toString.append(itr.next().toString());
			if(itr.hasNext()) {
				toString.append(",");
			}
		}
		toString.append("]");
		toString.append("}");
		return toString.toString();
	}
}