package com.pack.pack.model.web;

import java.util.LinkedList;
import java.util.List;


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
	
	private String videoUrl;
	
	private List<JRssSubFeed> siblings;
	
	private String feedType;
	
	private String createdBy;
	
	private long uploadTime;
	
	private String shareableUrl;
	
	private String articleSummaryText;
	
	private String fullArticleText;
	
	private List<JGeoTag> geoTags;
	
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
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof JRssFeed)) {
			return false;
		}
		return ogImage.equals(((JRssFeed)obj).ogImage);
	}
	
	@Override
	public int hashCode() {
		return ogImage.hashCode();
	}

	public String getFeedType() {
		return feedType;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public long getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(long uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getShareableUrl() {
		return shareableUrl;
	}

	public void setShareableUrl(String shareableUrl) {
		this.shareableUrl = shareableUrl;
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
}