package com.squill.feed.web.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	private long batchId;
	
	@JsonIgnore
	private String feedType;
	
	private String articleSummaryText;
	
	private String fullArticleText;
	
	private List<JGeoTag> geoTags;
	
	private List<JConcept> concepts;
	
	private List<JTaxonomy> taxonomies;
	
	private long uploadTime;
	
	private String createdBy;
	
	private String createdByLogoUrl;
	
	private String createdByUserId;
	
	private String shareableUrl;
	
	private String videoUrl;
	
	private List<JRssSubFeed> siblings;
	
	private String squillUrl;
	
	private List<String> keywords;
	
	public String getOgTitle() {
		return ogTitle;
	}

	public void setOgTitle(String ogTitle) {
		this.ogTitle = ogTitle;
	}

	public String getOgDescription() {
		if(ogDescription == null) {
			ogDescription = "";
		}
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

	public String getFeedType() {
		if(feedType == null) {
			return ogType;
		}
		return feedType;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
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

	public List<JConcept> getConcepts() {
		if(concepts == null) {
			concepts = new ArrayList<JConcept>();
		}
		return concepts;
	}

	public void setConcepts(List<JConcept> concepts) {
		this.concepts = concepts;
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

	public long getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(long uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedByLogoUrl() {
		return createdByLogoUrl;
	}

	public void setCreatedByLogoUrl(String createdByLogoUrl) {
		this.createdByLogoUrl = createdByLogoUrl;
	}

	public String getCreatedByUserId() {
		return createdByUserId;
	}

	public void setCreatedByUserId(String createdByUserId) {
		this.createdByUserId = createdByUserId;
	}

	public String getShareableUrl() {
		return shareableUrl;
	}

	public void setShareableUrl(String shareableUrl) {
		this.shareableUrl = shareableUrl;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public List<JRssSubFeed> getSiblings() {
		if(siblings == null) {
			siblings = new LinkedList<JRssSubFeed>();
		}
		return siblings;
	}

	public void setSiblings(List<JRssSubFeed> siblings) {
		this.siblings = siblings;
	}

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}
	
	@Override
	public int hashCode() {
		return (getClass().getName() + "_" + ogUrl).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof JRssFeed)) {
			return false;
		}
		return this.ogUrl.equals(((JRssFeed) obj).getOgUrl());
	}

	public String getSquillUrl() {
		return squillUrl;
	}

	public void setSquillUrl(String squillUrl) {
		this.squillUrl = squillUrl;
	}

	public List<String> getKeywords() {
		if(keywords == null) {
			keywords = new LinkedList<String>();
		}
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
}