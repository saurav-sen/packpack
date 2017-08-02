package com.squill.news.reader;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class NewsFeeds {
	
	private String status;
	
	private String source;
	
	private List<NewsFeed> articles;

	public List<NewsFeed> getArticles() {
		if(articles == null) {
			articles = new ArrayList<NewsFeed>();
		}
		return articles;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
