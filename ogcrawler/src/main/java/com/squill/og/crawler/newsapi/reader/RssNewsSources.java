package com.squill.og.crawler.newsapi.reader;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class RssNewsSources {

	private List<RssNewsSource> sources;

	public List<RssNewsSource> getSources() {
		if(sources == null) {
			sources = new LinkedList<RssNewsSource>();
		}
		return sources;
	}
}
