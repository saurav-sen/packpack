package com.pack.pack.model;

import org.ektorp.support.CouchDbDocument;

/**
 * 
 * @author Saurav
 *
 */
public class EGift extends CouchDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3863397344858359963L;

	private String brandInfo;
	
	private String brandId;
	
	private String title;
	
	private String category;
	
	private String imageUrl;
	
	private String imageThumbnailUrl;

	public String getImageThumbnailUrl() {
		return imageThumbnailUrl;
	}

	public void setImageThumbnailUrl(String imageThumbnailUrl) {
		this.imageThumbnailUrl = imageThumbnailUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getBrandInfo() {
		return brandInfo;
	}

	public void setBrandInfo(String brandInfo) {
		this.brandInfo = brandInfo;
	}

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}
}