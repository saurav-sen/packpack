package com.squill.og.crawler.newsapi.reader;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class NewsSource {

	private String id;
	
	private String name;
	
	private String description;
	
	private List<String> orgHomePages;
	
	private String feedType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getOrgHomePages() {
		if(orgHomePages == null) {
			orgHomePages = new ArrayList<String>();
		}
		return orgHomePages;
	}

	public void setOrgHomePages(List<String> orgHomePages) {
		this.orgHomePages = orgHomePages;
	}
	
	public String getFeedType() {
		return feedType;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}
}
