package com.pack.pack.rest.api.security.oauth1;

import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1AuthorizationFlow;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

import com.pack.pack.rest.api.security.OAuthConstants;

public class Main {

	public static void main(String[] args) {
		ConsumerCredentials consumerCredentials = new ConsumerCredentials(
				OAuthConstants.DEFAULT_CLIENT_KEY,
				OAuthConstants.DEFAULT_CLIENT_SECRET);
		OAuth1AuthorizationFlow authFlow = OAuth1ClientSupport
				.builder(consumerCredentials)
				.authorizationFlow(
						"http://192.168.35.12:8080/packpack/"
								+ OAuthConstants.OAUTH_REQUEST_TOKEN_PATH,
						"http://192.168.35.12:8080/packpack/"
								+ OAuthConstants.OAUTH_ACCESS_TOKEN_PATH,
						"http://192.168.35.12:8080/packpack/oauth/authorize")
				.build();
		String authorizationUri = authFlow.start();
		System.out.println(authorizationUri);
	}
}