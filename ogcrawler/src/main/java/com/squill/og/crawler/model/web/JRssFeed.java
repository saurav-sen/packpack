package com.squill.og.crawler.model.web;

/**
 * 
 * @author Saurav
 *
 */
public class JRssFeed {

	private String ogTitle;
	
	private String ogDescription;
	
	private String ogType;
	
	private String ogImage;
	
	private String ogUrl;
	
	private String hrefSource;
	
	private String id;

	public String getOgTitle() {
		return ogTitle;
	}

	public void setOgTitle(String ogTitle) {
		this.ogTitle = ogTitle;
	}

	public String getOgDescription() {
		return ogDescription;
	}

	public void setOgDescription(String ogDescription) {
		this.ogDescription = ogDescription;
	}

	public String getOgType() {
		return ogType;
	}

	public void setOgType(String ogType) {
		this.ogType = ogType;
	}

	public String getOgImage() {
		return ogImage;
	}

	public void setOgImage(String ogImage) {
		this.ogImage = ogImage;
	}

	public String getOgUrl() {
		return ogUrl;
	}

	public void setOgUrl(String ogUrl) {
		this.ogUrl = ogUrl;
	}

	public String getHrefSource() {
		return hrefSource;
	}

	public void setHrefSource(String hrefSource) {
		this.hrefSource = hrefSource;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}