package com.pack.pack.model;

/**
 * 
 * @author Saurav
 *
 */
public class AdvirtiseFeed {

	private String promoterBrand;
	
	private long id;
	
	private String title;
	
	private String message;
	
	private String advImageUrl;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPromoterBrand() {
		return promoterBrand;
	}

	public void setPromoterBrand(String promoterBrand) {
		this.promoterBrand = promoterBrand;
	}

	public String getAdvImageUrl() {
		return advImageUrl;
	}

	public void setAdvImageUrl(String advImageUrl) {
		this.advImageUrl = advImageUrl;
	}
}