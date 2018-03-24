package com.squill.news.reader;

public class NewsSource {

	private String id;
	
	private String name;
	
	private String description;
	
	private String orgHomePage;
	
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

	public String getOrgHomePage() {
		return orgHomePage;
	}

	public void setOrgHomePage(String orgHomePage) {
		this.orgHomePage = orgHomePage;
	}
	
	public String getFeedType() {
		return feedType;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}
}
