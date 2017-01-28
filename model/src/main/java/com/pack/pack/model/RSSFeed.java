package com.pack.pack.model;


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
}