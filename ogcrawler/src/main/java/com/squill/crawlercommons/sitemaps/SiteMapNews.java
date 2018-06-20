package com.squill.crawlercommons.sitemaps;


/**
 * 
 * @author Saurav
 *
 */
public class SiteMapNews {

	private String publication_date;
	
	private String title;
	
	private String publication_name;
	
	public SiteMapNews(String publication_date, String title, String publication_name) {
		this.publication_date = publication_date;
		this.title = title;
		this.publication_name = publication_name;
	}

	public String getPublication_date() {
		return publication_date;
	}

	public String getTitle() {
		return title;
	}

	public String getPublication_name() {
		return publication_name;
	}
}
