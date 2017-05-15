package com.pack.pack.model.web.dto;

/**
 * 
 * @author Saurav
 *
 */
public class UserSettings {
	
	public static final String DISPLAY_NAME = "display_name";
    public static final String USER_ADDRESS = "user_address";
    public static final String USER_NAME = "user_name";

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