package com.pack.pack.client.internal;


/**
 * 
 * @author Saurav
 *
 */
abstract class BaseAPI extends AbstractAPI {
	
	protected static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
	protected static final String GZIP_CONTENT_ENCODING = "gzip";
	
	private String baseUrl;
	
	protected BaseAPI(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	protected String getBaseUrl() {
		return baseUrl;
	}
}
