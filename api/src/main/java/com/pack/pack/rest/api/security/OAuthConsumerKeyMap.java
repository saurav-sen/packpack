package com.pack.pack.rest.api.security;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Saurav
 *
 */
public class OAuthConsumerKeyMap {

	public static Map<String, String> consumerKeyMap = new HashMap<String, String>(4);
	
	static {
		consumerKeyMap.put(OAuthConstants.DEFAULT_CLIENT_KEY, OAuthConstants.DEFAULT_CLIENT_SECRET);
	}
	
	public static final OAuthConsumerKeyMap INSTANCE = new OAuthConsumerKeyMap();
	
	private OAuthConsumerKeyMap() {
	}
	
	public String getConsumerSecret(String consumerKey) {
		return consumerKeyMap.get(consumerKey);
	}
	
	public void addCunsumerDetails(String consumerKey, String consumerSecret) {
		consumerKeyMap.put(consumerKey, consumerSecret);
	}
}