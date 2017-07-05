package com.pack.pack.client.api.test;

import java.util.HashMap;
import java.util.Map;

public class TestSession {
	
	private Map<String, Object> map = new HashMap<String, Object>();
	
	private String oauthToken;
	
	private String userId;
	
	private int seqNo;
	
	private String baseUrl;
	
	private String baseUrl2;
	
	public TestSession(int seqNo, String baseUrl, String baseUrl2) {
		this.seqNo = seqNo;
		this.baseUrl = baseUrl;
		this.baseUrl2 = baseUrl2;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}
	
	public String getBaseUrl2() {
		return baseUrl2;
	}
	
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

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}
}
