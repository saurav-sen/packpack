package com.pack.pack.oauth1.client.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.pack.pack.oauth1.client.internal.InternalConstants;

public class OAuth1Parameters {

	private Map<String, String> paramsMap = new HashMap<String, String>();
	
	OAuth1Parameters() {
	}
	
	Set<String> keySet() {
		return paramsMap.keySet();
	}
	
	String get(String paramName) {
		return paramsMap.get(paramName);
	}
	
	boolean containsKey(String key) {
		return paramsMap.containsKey(key);
	}
	
	void put(String key, String value) {
		paramsMap.put(key, value);
	}
	
	String getTimestamp() {
		return paramsMap.get(InternalConstants.OAUTH_TIMESTAMP);
	}

	String getNonce() {
		return paramsMap.get(InternalConstants.OAUTH_NONCE);
	}

	String getOauthCallback() {
		return paramsMap.get(InternalConstants.OAUTH_CALLBAK);
	}

	String getConsumerKey() {
		return paramsMap.get(InternalConstants.OAUTH_CONSUMER_KEY);
	}

	String getOauthSignatureMethod() {
		return paramsMap.get(InternalConstants.OAUTH_SIGNATURE_METHOD);
	}
	
	String getOAuthVersion() {
		return paramsMap.get(InternalConstants.OAUTH_VERSION);
	}
}