package com.pack.pack.client.internal;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

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

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.API.Login;
import com.pack.pack.common.util.JSONUtil;

public class LoginApi implements API {
	
	private static final String BASE_URL = "http://192.168.35.12:8080/packpack/";
	private static final String OAUTH_REQUEST_TOKEN_PATH = "oauth/request_token";
	private static final String OAUTH_ACCESS_TOKEN_PATH = "oauth/access_token";
	private static final String OAUTH_AUTHORIZATION_PATH = "oauth/authorize";
	
	@Override
	public <T> T execute(Class<T> clazz, COMMAND action, Map<String, Object> params) throws Exception {
		if(action == COMMAND.SIGN_IN) {
			String clientKey = (String)params.get(API.Login.CLIENT_KEY);
			String clientSecret = (String)params.get(API.Login.CLIENT_SECRET);
			String username = (String)params.get(API.Login.USERNAME);
			String password = (String)params.get(API.Login.PASSWORD);
			AccessToken token = login(clientKey, clientSecret, username, password);
			if(token.getClass() == clazz) {
				return (T)token;
			}
			return JSONUtil.deserialize(JSONUtil.serialize(token), clazz);
		}
		throw new UnsupportedOperationException(action.name()
				+ " is not supported. Probably a different API "
				+ "type needs to be choosen for this.");
	}

	private AccessToken login(String clientKey, String clientSecret,
			String username, String password) throws ClientProtocolException, IOException {
		ConsumerCredentials consumerCredentials = new ConsumerCredentials(
				clientKey, clientSecret);
		OAuth1AuthorizationFlow authFlow = OAuth1ClientSupport
				.builder(consumerCredentials)
				.authorizationFlow(
						BASE_URL + OAUTH_REQUEST_TOKEN_PATH,
						BASE_URL + OAUTH_ACCESS_TOKEN_PATH,
						BASE_URL + OAUTH_AUTHORIZATION_PATH)
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
		String json = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
		HttpEntity body = new StringEntity(json, ContentType.APPLICATION_JSON);
		POST.setEntity(body);
		HttpResponse response = client.execute(POST);
		String verifier = EntityUtils.toString(response.getEntity());
		//System.out.println("Verifier: " + verifier);

		AccessToken accessToken = authFlow.finish(verifier);
		//System.out.println("Access token: " + accessToken.getToken());
		return accessToken;
	}	
}