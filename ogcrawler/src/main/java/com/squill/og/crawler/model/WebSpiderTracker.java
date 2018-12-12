package com.squill.og.crawler.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.squill.feed.web.model.JGeoTag;
import com.squill.feed.web.model.JRssFeed;
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
	
	private String htmlSnippet;
	
	private List<JGeoTag> geoTags;
	
	private List<JTaxonomy> taxonomies;
	
	private boolean geoTagsResolved = false;
	
	private JRssFeed feedToUpload;
	
	private String webCrawlerId;
	
	private boolean articleExtractionDone = false;
	
	private String title;
	
	public WebSpiderTracker() {
		
	}

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

	public JRssFeed getFeedToUpload() {
		return feedToUpload;
	}

	public void setFeedToUpload(JRssFeed feedToUpload) {
		this.feedToUpload = feedToUpload;
	}

	public String getWebCrawlerId() {
		return webCrawlerId;
	}

	public void setWebCrawlerId(String webCrawlerId) {
		this.webCrawlerId = webCrawlerId;
	}

	public boolean isArticleExtractionDone() {
		return articleExtractionDone;
	}

	public void setArticleExtractionDone(boolean articleExtractionDone) {
		this.articleExtractionDone = articleExtractionDone;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHtmlSnippet() {
		return htmlSnippet;
	}

	public void setHtmlSnippet(String htmlSnippet) {
		this.htmlSnippet = htmlSnippet;
	}
}