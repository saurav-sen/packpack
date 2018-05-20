package com.squill.og.crawler.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.squill.feed.web.model.JGeoTag;
import com.squill.feed.web.model.JTaxonomy;

/**
 * 
 * @author Saurav
 *
 */
public class WebSpiderTracker {

	private long lastCrawled;
	
	private String link;
	
	private String lastModifiedSince;
	
	private boolean uploadCompleted = false;
	
	private String articleSummaryText;
	
	private String fullArticleText;
	
	private List<JGeoTag> geoTags;
	
	private List<JTaxonomy> taxonomies;
	
	private boolean geoTagsResolved = false;

	public long getLastCrawled() {
		return lastCrawled;
	}

	public void setLastCrawled(long lastCrawled) {
		this.lastCrawled = lastCrawled;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getLastModifiedSince() {
		return lastModifiedSince;
	}

	public void setLastModifiedSince(String lastModifiedSince) {
		this.lastModifiedSince = lastModifiedSince;
	}

	public boolean isUploadCompleted() {
		return uploadCompleted;
	}

	public void setUploadCompleted(boolean uploadCompleted) {
		this.uploadCompleted = uploadCompleted;
	}

	public String getArticleSummaryText() {
		return articleSummaryText;
	}

	public void setArticleSummaryText(String articleSummaryText) {
		this.articleSummaryText = articleSummaryText;
	}

	public String getFullArticleText() {
		return fullArticleText;
	}

	public void setFullArticleText(String fullArticleText) {
		this.fullArticleText = fullArticleText;
	}
	
	public List<JGeoTag> getGeoTags() {
		if(geoTags == null) {
			geoTags = new LinkedList<JGeoTag>();
		}
		return geoTags;
	}

	public void setGeoTags(List<JGeoTag> geoTags) {
		this.geoTags = geoTags;
	}
	
	public List<JTaxonomy> getTaxonomies() {
		if(taxonomies == null) {
			taxonomies = new ArrayList<JTaxonomy>();
		}
		return taxonomies;
	}

	public void setTaxonomies(List<JTaxonomy> taxonomies) {
		this.taxonomies = taxonomies;
	}

	public boolean isGeoTagsResolved() {
		if(!getGeoTags().isEmpty()) {
			geoTagsResolved = true;
		}
		return geoTagsResolved;
	}

	public void setGeoTagsResolved(boolean geoTagsResolved) {
		this.geoTagsResolved = geoTagsResolved;
	}
}