package com.pack.pack.model;

/**
 * 
 * @author Saurav
 *
 */
public class UserInfo {
	
	public static final String FOLLOWED_CATEGORIES = "followedCategories";

	private String key;
	
	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}