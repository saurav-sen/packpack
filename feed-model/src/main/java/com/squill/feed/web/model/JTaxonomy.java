package com.squill.feed.web.model;

public class JTaxonomy {

	private String id;
	
	private String refUri;
	
	private String name;
	
	private String parentRefUrl;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRefUri() {
		return refUri;
	}

	public void setRefUri(String refUri) {
		this.refUri = refUri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentRefUrl() {
		return parentRefUrl;
	}

	public void setParentRefUrl(String parentRefUrl) {
		this.parentRefUrl = parentRefUrl;
	}
}
