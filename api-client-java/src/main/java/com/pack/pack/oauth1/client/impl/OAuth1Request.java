package com.pack.pack.oauth1.client.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.pack.pack.oauth1.client.OAuth1ClientCredentials;
import com.pack.pack.oauth1.client.internal.HmacSha1Method;
import com.pack.pack.oauth1.client.internal.InternalConstants;

public class OAuth1Request {

	private String requestMethod;
	private URL url;
	
	private OAuth1Parameters parameters;
	
	private OAuth1ClientCredentials credentials;
	
	private OAuth1Request(String requestMethod, String requestUrl) throws MalformedURLException {
		this.requestMethod = requestMethod;
		url = new URL(requestUrl);
	}
	
	public static OAuth1Request init(String requestMethod, String requestUrl,
			OAuth1ClientCredentials credentials) throws MalformedURLException {
		OAuth1Parameters params = new OAuth1Parameters();
		params.put(InternalConstants.OAUTH_TIMESTAMP,
				Long.toString(System.currentTimeMillis() / 1000));
		params.put(InternalConstants.OAUTH_NONCE, UUID.randomUUID().toString());
		params.put(InternalConstants.OAUTH_CALLBAK, "oob");
		params.put(InternalConstants.OAUTH_VERSION, "1.0");
		params.put(InternalConstants.OAUTH_CONSUMER_KEY,
				credentials.getConsumerKey());
		params.put(InternalConstants.OAUTH_SIGNATURE_METHOD,
				HmacSha1Method.NAME);
		OAuth1Request request = new OAuth1Request(requestMethod, requestUrl);
		request.setParameters(params);
		request.setCredentials(credentials);
		return request;
	}
	
	String getRequestMethod() {
		return requestMethod;
	}

	URL getUrl() {
		return url;
	}

	OAuth1Parameters getParameters() {
		return parameters;
	}

	void setParameters(OAuth1Parameters parameters) {
		this.parameters = parameters;
	}
	
	Set<String> getParameterNames() {
		return Collections.emptySet();
	}
	
	List<String> getParameterValues(String paramName) {
		return Collections.emptyList();
	}

	OAuth1ClientCredentials getCredentials() {
		return credentials;
	}

	void setCredentials(OAuth1ClientCredentials credentials) {
		this.credentials = credentials;
	}
}