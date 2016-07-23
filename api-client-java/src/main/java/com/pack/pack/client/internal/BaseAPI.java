package com.pack.pack.client.internal;


/**
 * 
 * @author Saurav
 *
 */
abstract class BaseAPI extends AbstractAPI {
	
	private String baseUrl;
	
	protected BaseAPI(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	protected String getBaseUrl() {
		return baseUrl;
	}
}
