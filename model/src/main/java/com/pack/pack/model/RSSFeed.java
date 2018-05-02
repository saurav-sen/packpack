package com.pack.pack.model;

import java.util.LinkedList;
import java.util.List;


/**
 * 
 * @author Saurav
 *
 */
public class RSSFeed /*extends CouchDbDocument*/{

	/**
	 * 
	 */
	//private static final long serialVersionUID = 3320472739855917908L;

	private String ogTitle;
	
	private String ogDescription;
	
	private String ogType;
	
	private String ogImage;
	
	private String ogUrl;
	
	private String hrefSource;
	
	private double longitude;
	
	private double latitude;
	
	private String promoStartTimestamp;
	
	private String promoExpiryTimestamp;
	
	private String id;
	
	private String videoUrl;
	
	private List<RssSubFeed> siblings;
	
	private String feedType;
	
	private long uploadTime;
	
	private String shareableUrl;
	
	private String articleSummaryText;
	
	private String fullArticleText;
	
	private List<GeoTag> geoTags;
	
	private List<Concept> concepts;
	
	private List<Taxonomy> taxonomies;
	
	private String createdBy;
	
	private String createdByLogoUrl;
	
	private String createdByUserId;

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

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getPromoStartTimestamp() {
		return promoStartTimestamp;
	}

	public void setPromoStartTimestamp(String promoStartTimestamp) {
		this.promoStartTimestamp = promoStartTimestamp;
	}

	public String getPromoExpiryTimestamp() {
		return promoExpiryTimestamp;
	}

	public void setPromoExpiryTimestamp(String promoExpiryTimestamp) {
		this.promoExpiryTimestamp = promoExpiryTimestamp;
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

	public List<RssSubFeed> getSiblings() {
		if(siblings == null) {
			siblings = new LinkedList<RssSubFeed>();
		}
		return siblings;
	}

	public void setSiblings(List<RssSubFeed> siblings) {
		this.siblings = siblings;
	}

	public String getFeedType() {
		return feedType;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
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

	public List<GeoTag> getGeoTags() {
		if(geoTags == null) {
			geoTags = new LinkedList<GeoTag>();
		}
		return geoTags;
	}

	public void setGeoTags(List<GeoTag> geoTags) {
		this.geoTags = geoTags;
	}

	public List<Concept> getConcepts() {
		if(concepts == null) {
			concepts = new LinkedList<Concept>();
		}
		return concepts;
	}

	public void setConcepts(List<Concept> concepts) {
		this.concepts = concepts;
	}

	public List<Taxonomy> getTaxonomies() {
		if(taxonomies == null) {
			taxonomies = new LinkedList<Taxonomy>();
		}
		return taxonomies;
	}

	public void setTaxonomies(List<Taxonomy> taxonomies) {
		this.taxonomies = taxonomies;
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
}