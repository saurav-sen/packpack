package com.pack.pack.client.internal;

import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;
import static com.pack.pack.client.api.APIConstants.BASE_URL;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1AuthorizationFlow;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JUser;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public class UserManagementApi extends AbstractAPI {

	private static final String OAUTH_REQUEST_TOKEN_PATH = "oauth/request_token"; //$NON-NLS-1$
	private static final String OAUTH_ACCESS_TOKEN_PATH = "oauth/access_token"; //$NON-NLS-1$
	private static final String OAUTH_AUTHORIZATION_PATH = "oauth/authorize"; //$NON-NLS-1$

	@Override
	protected ApiInvoker getInvoker() {
		return new Invoker();
	}
	
	private class Invoker implements ApiInvoker {
		
		private COMMAND action;
		
		private Map<String, Object> params;
		
		private String oAuthToken;
		
		@Override
		public void setConfiguration(Configuration configuration) {
			action = configuration.getAction();
			params = configuration.getApiParams();
			oAuthToken = configuration.getOAuthToken();
		}
		
		@Override
		public Object invoke() throws Exception {
			Object result = null;
			if (action == COMMAND.SIGN_IN) {
				result = signIn(params);
			} else if (action == COMMAND.SIGN_UP) {
				result = signUp(params);
			} else if (action == COMMAND.GET_USER_BY_ID) {
				result = getUserById(params, oAuthToken);
			} else if (action == COMMAND.GET_USER_BY_USERNAME) {
				result = getUserByUsername(params, oAuthToken);
			} else if (action == COMMAND.SEARCH_USER_BY_NAME) {
				result = searchUserByName(
						(String) params.get(APIConstants.User.NAME_SEARCH_PATTERN),
						oAuthToken);
			} else {
				throw new UnsupportedOperationException(action.name()
						+ " is not supported. Probably a different API "
						+ "type needs to be choosen for this.");
			}
			/*if(result != null) {
				if (result.getClass() == clazz) {
					return (T) result;
				}
				return JSONUtil.deserialize(JSONUtil.serialize(result), clazz);
			}*/
			return result;
		}
	}

	private JUser searchUserByName(String namePattern, String accessToken)
			throws ClientProtocolException, IOException, ParseException,
			PackPackException {
		String url = BASE_URL + "user/name/" + namePattern;
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, accessToken);
		CloseableHttpResponse response = client.execute(GET);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JUser.class);
	}

	private JUser getUserByUsername(Map<String, Object> params,
			String accessToken) throws ClientProtocolException, IOException,
			ParseException, PackPackException {
		String username = (String) params.get(APIConstants.User.USERNAME);
		String url = BASE_URL + "user/username/" + username;
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, accessToken);
		CloseableHttpResponse response = client.execute(GET);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JUser.class);
	}

	private JUser getUserById(Map<String, Object> params, String accessToken)
			throws ClientProtocolException, IOException, PackPackException {
		String id = (String) params.get(APIConstants.User.ID);
		String url = BASE_URL + "user/id/" + id;
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, accessToken);
		HttpResponse response = client.execute(GET);
		String json = EntityUtils.toString(response.getEntity());
		return JSONUtil.deserialize(json, JUser.class);
	}
	
	private JStatus signUp(Map<String, Object> params)
			throws ClientProtocolException, IOException, ParseException,
			PackPackException {
		String url = BASE_URL + "user";
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpPost POST = new HttpPost(url);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		Iterator<String> itr = params.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			if (APIConstants.User.Register.PROFILE_PICTURE.equals(key)) {
				File file = (File) params.get(key);
				builder.addBinaryBody(key, file,
						ContentType.APPLICATION_OCTET_STREAM, file.getName());
			} else {
				String text = (String) params.get(key);
				builder.addTextBody(key, text);
			}
		}
		HttpEntity entity = builder.build();
		POST.setEntity(entity);
		POST.addHeader("Content-Type",
				ContentType.MULTIPART_FORM_DATA.getMimeType());
		CloseableHttpResponse response = client.execute(POST);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JStatus.class);
	}

	private AccessToken signIn(Map<String, Object> params)
			throws ClientProtocolException, IOException {
		String clientKey = (String) params.get(APIConstants.Login.CLIENT_KEY);
		String clientSecret = (String) params
				.get(APIConstants.Login.CLIENT_SECRET);
		String username = (String) params.get(APIConstants.Login.USERNAME);
		String password = (String) params.get(APIConstants.Login.PASSWORD);
		return doSignIn(clientKey, clientSecret, username, password);
	}

	private AccessToken doSignIn(String clientKey, String clientSecret,
			String username, String password) throws ClientProtocolException,
			IOException {
		ConsumerCredentials consumerCredentials = new ConsumerCredentials(
				clientKey, clientSecret);
		OAuth1AuthorizationFlow authFlow = OAuth1ClientSupport
				.builder(consumerCredentials)
				.authorizationFlow(BASE_URL + OAUTH_REQUEST_TOKEN_PATH,
						BASE_URL + OAUTH_ACCESS_TOKEN_PATH,
						BASE_URL + OAUTH_AUTHORIZATION_PATH).build();
		String authorizationUri = authFlow.start();

		String query = URI.create(authorizationUri).getQuery();
		int index = query.indexOf("oauth_token=");
		String requestToken = query.substring(index + "oauth_token=".length());

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost POST = new HttpPost(
				"http://192.168.35.12:8080/packpack/oauth/authorize");
		POST.addHeader("Authorization", requestToken);
		POST.addHeader("Content-Type", "application/json");
		String json = "{\"username\": \"" + username + "\", \"password\": \""
				+ password + "\"}";
		HttpEntity body = new StringEntity(json, ContentType.APPLICATION_JSON);
		POST.setEntity(body);
		HttpResponse response = client.execute(POST);
		String verifier = EntityUtils.toString(response.getEntity());

		AccessToken accessToken = authFlow.finish(verifier);
		return accessToken;
	}
}