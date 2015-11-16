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
	
	private String imageId;
	
	private String title;
	
	private String category;

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
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