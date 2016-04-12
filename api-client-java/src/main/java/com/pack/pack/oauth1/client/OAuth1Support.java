package com.pack.pack.oauth1.client;

import com.pack.pack.oauth1.client.impl.OAuth1BuilderImpl;

public class OAuth1Support {
	
	private static final String OAUTH_REQUEST_TOKEN_PATH = "oauth/request_token"; //$NON-NLS-1$
	private static final String OAUTH_AUTHORIZATION_PATH = "oauth/authorize"; //$NON-NLS-1$
	private static final String OAUTH_ACCESS_TOKEN_PATH = "oauth/access_token"; //$NON-NLS-1$
	
	public static OAuth1Builder builder(
			OAuth1ClientCredentials consumerCredential, String baseUrl) {
		String requestTokenUrl = baseUrl;
		{
			if(!requestTokenUrl.endsWith("/")) {
				requestTokenUrl = requestTokenUrl + "/";
			}
			requestTokenUrl = requestTokenUrl + OAUTH_REQUEST_TOKEN_PATH;
		}
		
		String authorizeUrl = baseUrl;
		{
			if(!authorizeUrl.endsWith("/")) {
				authorizeUrl = authorizeUrl + "/";
			}
			authorizeUrl = authorizeUrl + OAUTH_AUTHORIZATION_PATH;
		}
		
		String accessTokenUrl = baseUrl;
		{
			if(!accessTokenUrl.endsWith("/")) {
				accessTokenUrl = accessTokenUrl + "/";
			}
			accessTokenUrl = accessTokenUrl + OAUTH_ACCESS_TOKEN_PATH;
		}
		return new OAuth1BuilderImpl(consumerCredential, requestTokenUrl,
				authorizeUrl, accessTokenUrl);
	}
}
