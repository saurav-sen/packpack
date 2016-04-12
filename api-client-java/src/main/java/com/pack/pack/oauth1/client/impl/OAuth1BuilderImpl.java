package com.pack.pack.oauth1.client.impl;

import java.net.MalformedURLException;

import com.pack.pack.oauth1.client.OAuth1Builder;
import com.pack.pack.oauth1.client.OAuth1ClientCredentials;
import com.pack.pack.oauth1.client.OAuth1RequestFlow;

public class OAuth1BuilderImpl implements OAuth1Builder {

	private OAuth1ClientCredentials credentials;
	
	private String authorizeUrl;
	
	private String requestTokenUrl;
	
	private String accessTokenUrl;

	public OAuth1BuilderImpl(OAuth1ClientCredentials credentials, String requestTokenUrl, String authorizeUrl, String accessTokenUrl) {
		this.credentials = credentials;
		this.authorizeUrl = authorizeUrl;
		this.requestTokenUrl = requestTokenUrl;
		this.accessTokenUrl = accessTokenUrl;
	}

	@Override
	public OAuth1RequestFlow build() throws MalformedURLException {
		OAuth1Request request = OAuth1Request.init("POST", requestTokenUrl, credentials);
		OAuth1RequestFlowImpl requestFlow = new OAuth1RequestFlowImpl(request);
		requestFlow.setAuthorizeUrl(authorizeUrl);
		requestFlow.setAccessTokenUrl(accessTokenUrl);
		requestFlow.setCredentials(credentials);
		return requestFlow;
	}
}