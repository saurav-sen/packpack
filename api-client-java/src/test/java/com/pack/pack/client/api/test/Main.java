package com.pack.pack.client.api.test;

import static com.pack.pack.client.api.APIConstants.BASE_URL;

import java.net.URI;

import com.pack.pack.oauth1.client.AccessToken;
import com.pack.pack.oauth1.client.OAuth1ClientCredentials;
import com.pack.pack.oauth1.client.OAuth1RequestFlow;
import com.pack.pack.oauth1.client.OAuth1Support;


public class Main {
	
	
	private static final String ANDROID_APP_CLIENT_KEY = "53e8a1f2-7568-4ac8-ab26-45738ca02599";
	private static final String ANDROID_APP_CLIENT_SECRET = "b1f6d761-dcb7-482b-a695-ab17e4a29b25";
	
	public static final String USERNAME = "sourabhnits@gmail.com";
	public static final String PASSWORD = "P@ckp@K#123";

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*SignUpUserTest signUpUserTest = new SignUpUserTest();
		signUpUserTest.signUp();*/
		
		AddTopicTest addTopicTest = new AddTopicTest();
		addTopicTest.beforeTest();
		//addTopicTest.createNewTopic();
		
		/*OAuth1ClientCredentials consumerCredentials = new OAuth1ClientCredentials(
				ANDROID_APP_CLIENT_KEY, ANDROID_APP_CLIENT_SECRET);
		OAuth1RequestFlow authFlow = OAuth1Support.builder(consumerCredentials,
				BASE_URL).build();
		String authorizationUri = authFlow.start();
		System.out.println(authorizationUri);

		String query = URI.create(authorizationUri).getQuery();
		int index = query.indexOf("oauth_token=");
		String requestToken = query.substring(index + "oauth_token=".length());
		System.out.println("Request Token: " + requestToken);
		
		String verifier = authFlow.authorize(requestToken, USERNAME, PASSWORD);
		System.out.println("Verifier: " + verifier);
		
		AccessToken accessToken = authFlow.finish(requestToken, verifier);
		System.out.println("Access Token: " + accessToken.getToken());*/
	}

}
