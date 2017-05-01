package com.pack.pack.client.internal;


/**
 * 
 * @author Saurav
 *
 */
abstract class BaseAPI extends AbstractAPI {
	
	protected static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
	protected static final String GZIP_CONTENT_ENCODING = "gzip";
	
	protected static final String UTF_8_CHARSET = "; charset=utf-8";
	protected static final String UTF_8 = "UTF-8";
	
	protected static final String TEXT_PLAIN = "text/plain";
	
	private String baseUrl;
	
	protected BaseAPI(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	protected String getBaseUrl() {
		return baseUrl;
	}
}
