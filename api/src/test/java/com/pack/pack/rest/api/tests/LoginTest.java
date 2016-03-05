package com.pack.pack.rest.api.tests;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1AuthorizationFlow;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

import com.pack.pack.oauth.OAuthConstants;

/**
 * 
 * @author Saurav
 *
 */
public class LoginTest {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
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

		String query = URI.create(authorizationUri).getQuery();
		int index = query.indexOf("oauth_token=");
		String requestToken = query.substring(index + "oauth_token=".length());
		System.out.println("Request Token: " + requestToken);

		HttpClient client = new DefaultHttpClient();
		HttpPost POST = new HttpPost(
				"http://192.168.35.12:8080/packpack/oauth/authorize");
		POST.addHeader("Authorization", requestToken);
		POST.addHeader("Content-Type", "application/json");
		String json = "{\"username\": \"sourabhnits@gmail.com\", \"password\": \"P@ckp@K#123\"}";
		HttpEntity body = new StringEntity(json, ContentType.APPLICATION_JSON);
		POST.setEntity(body);
		HttpResponse response = client.execute(POST);
		String verifier = EntityUtils.toString(response.getEntity());
		System.out.println("Verifier: " + verifier);

		AccessToken accessToken = authFlow.finish(verifier);
		System.out.println("Access token: " + accessToken.getToken());
	}
}