package com.pack.pack.client.api.test;

import java.util.HashMap;
import java.util.Map;

public class TestSession {
	
	private Map<String, Object> map = new HashMap<String, Object>();
	
	private String oauthToken;
	
	private String userId;
	
	public void storeValue(String key, Object value) {
		map.put(key, value);
	}
	
	public Object getValue(String key) {
		return map.get(key);
	}

	public String getOauthToken() {
		return oauthToken;
	}

	public void setOauthToken(String oauthToken) {
		this.oauthToken = oauthToken;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public boolean isInitialized() {
		return oauthToken != null && userId != null;
	}
}
