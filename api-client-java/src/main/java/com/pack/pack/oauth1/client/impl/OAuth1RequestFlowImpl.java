package com.pack.pack.oauth1.client.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.oauth1.client.AccessToken;
import com.pack.pack.oauth1.client.OAuth1ClientCredentials;
import com.pack.pack.oauth1.client.OAuth1RequestFlow;
import com.pack.pack.oauth1.client.internal.InternalConstants;
import com.pack.pack.oauth1.client.internal.OAuth1Secrets;

public class OAuth1RequestFlowImpl implements OAuth1RequestFlow {

	private String authorizeUrl;

	private OAuth1Request request;

	private String accessTokenUrl;

	private OAuth1ClientCredentials credentials;

	/*private static final CloseableHttpClient HTTP_CLIENT = HttpClientBuilder
			.create().build();*/
	
	private Map<String, String> tokensMap = new HashMap<String, String>(2);

	OAuth1RequestFlowImpl(OAuth1Request request) {
		this.request = request;
	}

	@Override
	public String start() throws ClientProtocolException, IOException {
		String responseFields = executeOAuth1Request(request, null);
		String[] split = responseFields.split("&");
		String requestTokenField = split[0];
		String requestToken = requestTokenField.split("=")[1];
		String requestTokenSecret = split[1].split("=")[1]; 
		tokensMap.put(requestToken, requestTokenSecret);
		return new StringBuilder(authorizeUrl).append("?").append(requestTokenField)
				.toString();
	}

	private String executeOAuth1Request(OAuth1Request request, String tokenSecret)
			throws ClientProtocolException, IOException {
		OAuth1Secrets secrets = new OAuth1Secrets();
		secrets.setConsumerSecret(request.getCredentials().getConsumerSecret());
		secrets.setTokenSecret(tokenSecret);
		String oauthSignature = new OAuth1Signature().generate(request, request
				.getParameters(), secrets);
		StringBuilder authorizationHeader = new StringBuilder("OAuth ");
		OAuth1Parameters parameters = request.getParameters();
		Set<String> keySet = parameters.keySet();
		Iterator<String> itr = keySet.iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			String value = parameters.get(key);
			authorizationHeader.append(key);
			authorizationHeader.append("=");
			authorizationHeader.append("\"");
			authorizationHeader.append(value);
			authorizationHeader.append("\"");
			authorizationHeader.append(", ");
		}
		authorizationHeader.append(InternalConstants.OAUTH_SIGNATURE);
		authorizationHeader.append("=");
		authorizationHeader.append("\"");
		authorizationHeader.append(oauthSignature);
		authorizationHeader.append("\"");
		HttpPost POST = new HttpPost(request.getUrl().toString());
		POST.addHeader(InternalConstants.AUTHORIZATION,
				authorizationHeader.toString());
		POST.addHeader("user-agent", "PackPack-OAuth1-Client");
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(POST);
		String responseFields = EntityUtils.toString(response.getEntity());
		return responseFields;
	}

	@Override
	public String authorizeUser(String requestToken, String username,
			String password) throws ClientProtocolException, IOException {
		HttpPost POST = new HttpPost(authorizeUrl);
		POST.addHeader("Authorization", requestToken);
		POST.addHeader("Content-Type", APIConstants.APPLICATION_JSON);
		String json = "{\"username\": \"" + username + "\", \"password\": \""
				+ password + "\"}";
		HttpEntity body = new StringEntity(json);
		POST.setEntity(body);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(POST);
		String verifier = EntityUtils.toString(response.getEntity());
		return verifier;
	}
	
	@Override
	public String authorizeToken(String requestToken, String token,
			String secret) throws ClientProtocolException, IOException {
		HttpPost POST = new HttpPost(authorizeUrl);
		POST.addHeader("Authorization", requestToken);
		POST.addHeader("Content-Type", APIConstants.APPLICATION_JSON);
		String json = "{\"token\": \"" + token + "\", \"secret\": \""
				+ secret + "\"}";
		HttpEntity body = new StringEntity(json);
		POST.setEntity(body);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(POST);
		String verifier = EntityUtils.toString(response.getEntity());
		return verifier;
	}
	
	public static void main(String[] args) {
		String str = "relogin:EBFG";
		str = str.substring(str.indexOf("relogin:") + 8);
		System.out.println(str);
	}

	@Override
	public AccessToken finish(String requestToken, String verifierCode)
			throws ClientProtocolException, IOException {
		OAuth1Request oAuth1Request = OAuth1Request.init("POST",
				accessTokenUrl, credentials);
		OAuth1Parameters parameters = oAuth1Request.getParameters();
		parameters.put(InternalConstants.OAUTH_VERIFIER, verifierCode);
		parameters.put(InternalConstants.OAUTH_TOKEN, requestToken);
		String tokenSecret = tokensMap.get(requestToken);
		String accessTokenResponse = executeOAuth1Request(oAuth1Request, tokenSecret);
		String[] split = accessTokenResponse.split("&");
		String oauth_token = split[0].split("=")[1];
		String oauth_token_secret = split[1].split("=")[1];
		AccessTokenImpl accessToken = new AccessTokenImpl();
		accessToken.setToken(oauth_token);
		accessToken.setTokenSecret(oauth_token_secret);
		return accessToken;
	}
	
	private class AccessTokenImpl implements AccessToken {

		private String token;
		
		private String tokenSecret;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getTokenSecret() {
			return tokenSecret;
		}

		public void setTokenSecret(String tokenSecret) {
			this.tokenSecret = tokenSecret;
		}
	}

	void setAuthorizeUrl(String authorizeUrl) {
		this.authorizeUrl = authorizeUrl;
	}

	void setAccessTokenUrl(String accessTokenUrl) {
		this.accessTokenUrl = accessTokenUrl;
	}

	void setCredentials(OAuth1ClientCredentials credentials) {
		this.credentials = credentials;
	}
}