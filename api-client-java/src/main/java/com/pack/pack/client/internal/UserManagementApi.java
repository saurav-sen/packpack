package com.pack.pack.client.internal;

import static com.pack.pack.client.api.APIConstants.APPLICATION_JSON;
import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;
import static com.pack.pack.client.api.APIConstants.CONTENT_TYPE_HEADER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.dto.SignupDTO;
import com.pack.pack.model.web.dto.SignupVerifierDTO;
import com.pack.pack.model.web.dto.UserSettings;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
class UserManagementApi extends BaseAPI {

	UserManagementApi(String baseUrl) {
		super(baseUrl);
	}

	private Invoker invoker = new Invoker();

	@Override
	protected ApiInvoker getInvoker() {
		return invoker;
	}
	
	private class Invoker implements ApiInvoker {
		
		private COMMAND action;
		
		private Map<String, Object> params;
		
		private String userName;
		
		@Override
		public void setConfiguration(Configuration configuration) {
			action = configuration.getAction();
			params = configuration.getApiParams();
			userName = configuration.getUserName();
		}
		
		@Override
		public Object invoke() throws Exception {
			return invoke(null);
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Object invoke(MultipartRequestProgressListener listener) throws Exception {
			Object result = null;
			if (action == COMMAND.SIGN_UP) {
				result = signUp(params);
			} else if (action == COMMAND.EDIT_USER_CATEGORIES) {
				String userId = (String) params.get(APIConstants.User.ID);
				List<String> followedCategories = (List<String>) params
						.get(APIConstants.TopicCategories.FOLLOWED_CATEGORIES);
				StringBuilder followedCategoriesStr = new StringBuilder();
				for (String followedCategory : followedCategories) {
					followedCategoriesStr.append(followedCategory);
					followedCategoriesStr
							.append(APIConstants.TopicCategories.SEPARATOR);
				}
				result = editUserFollowdCategories(userId,
						followedCategoriesStr.toString(), userName);
			} else if(action == COMMAND.GET_USER_CATEGORIES) {
				String userId = (String) params.get(APIConstants.User.ID);
				result = getUserFollowedCategories(userId, userName);
			} else if (action == COMMAND.GET_USER_BY_ID) {
				result = getUserById(params, userName);
			} else if (action == COMMAND.GET_USER_BY_USERNAME) {
				result = getUserByUsername(params, userName);
			} else if (action == COMMAND.SEARCH_USER_BY_NAME) {
				result = searchUserByName(
						(String) params.get(APIConstants.User.NAME_SEARCH_PATTERN),
						userName);
			} else if (action == COMMAND.UPLOAD_USER_PROFILE_PICTURE) {
				String userId = (String) params.get(APIConstants.User.ID);
				byte[] data = (byte[]) params
						.get(APIConstants.User.PROFILE_PICTURE);
				result = uploadUserProfilePicture(userId, data, userName);
			} else if(action == COMMAND.UPDATE_USER_SETTINGS) {
				String userId = (String) params.get(APIConstants.User.ID);
				String key = (String) params.get(APIConstants.User.Settings.KEY);
				String value = (String) params.get(APIConstants.User.Settings.VALUE);
				result = updateUserSettings(userId, key, value, userName);
			} else if (action == COMMAND.ISSUE_PASSWD_RESET_LINK) {
				String userName = (String) params
						.get(APIConstants.User.USERNAME);
				result = issuePasswordResetVerifier(userName);
			} else if(action == COMMAND.ISSUE_SIGNUP_VERIFIER) {
				String email = (String) params
						.get(APIConstants.User.Register.EMAIL);
				String name = (String) params
						.get(APIConstants.User.Register.NAME);
				result = issueSignupVerifier(email, name);
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
	
	private JUser updateUserSettings(String userId, String key, String value,
			String userName) throws ClientProtocolException, IOException,
			PackPackException {
		String url = getBaseUrl() + "user/id/" + userId + "/settings";
		HttpPut PUT = new HttpPut(url);
		DefaultHttpClient client = new DefaultHttpClient();
		PUT.addHeader(AUTHORIZATION_HEADER, userName);
		UserSettings settings = new UserSettings();
		settings.setKey(key);
		settings.setValue(value);
		String json = JSONUtil.serialize(settings, true);
		PUT.setEntity(GZipUtil.compress(new StringEntity(json)));
		PUT.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		PUT.addHeader(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);
		HttpResponse response = client.execute(PUT);
		return JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JUser.class);
	}

	private JUser searchUserByName(String namePattern, String userName)
			throws ClientProtocolException, IOException, ParseException,
			PackPackException {
		String url = getBaseUrl() + "user/name/" + namePattern;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, userName);
		HttpResponse response = client.execute(GET);
		return JSONUtil.deserialize(EntityUtils.toString(GZipUtil.decompress(response.getEntity())),
				JUser.class);
	}

	private JUser getUserByUsername(Map<String, Object> params,
			String userName) throws ClientProtocolException, IOException,
			ParseException, PackPackException {
		String username = (String) params.get(APIConstants.User.USERNAME);
		String url = getBaseUrl() + "user/username/" + username;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, userName);
		HttpResponse response = client.execute(GET);
		return JSONUtil.deserialize(EntityUtils.toString(GZipUtil.decompress(response.getEntity())),
				JUser.class);
	}

	private JUser getUserById(Map<String, Object> params, String userName)
			throws ClientProtocolException, IOException, PackPackException {
		String id = (String) params.get(APIConstants.User.ID);
		String url = getBaseUrl() + "user/id/" + id;
		HttpClient client = new DefaultHttpClient();
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, userName);
		HttpResponse response = client.execute(GET);
		String json = EntityUtils.toString(GZipUtil.decompress(response.getEntity()));
		return JSONUtil.deserialize(json, JUser.class);
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getUserFollowedCategories(String userId,
			String userName) throws ClientProtocolException, IOException,
			PackPackException {
		String url = getBaseUrl() + "user/id/" + userId + "/follow/category";
		HttpClient client = new DefaultHttpClient();
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, userName);
		HttpResponse response = client.execute(GET);
		String json = EntityUtils.toString(GZipUtil.decompress(response
				.getEntity()));
		return JSONUtil.deserialize(json, ArrayList.class);
	}
	
	private JUser editUserFollowdCategories(String userId,
			String followedCategories, String userName)
			throws ClientProtocolException, IOException, PackPackException {
		String url = getBaseUrl() + "user/id/" + userId + "/follow/category";
		HttpClient client = new DefaultHttpClient();
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(AUTHORIZATION_HEADER, userName);
		HttpEntity entity = new StringEntity(followedCategories);
		PUT.setEntity(GZipUtil.compress(entity));
		PUT.addHeader(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);
		HttpResponse response = client.execute(PUT);
		String json = EntityUtils.toString(GZipUtil.decompress(response
				.getEntity()));
		return JSONUtil.deserialize(json, JUser.class);
	}
	
	private JUser uploadUserProfilePicture(String userId, byte[] data,
			String userName) throws ClientProtocolException, IOException,
			PackPackException {
		String url = getBaseUrl() + "user/id/" + userId;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(AUTHORIZATION_HEADER, userName);
		MultipartEntity multipartEntity = new MultipartEntity();
		multipartEntity.addPart(APIConstants.User.PROFILE_PICTURE,
				new ByteArrayBody(data, "image/jpeg", userId
						+ "_profilePicture.jpg"));
		PUT.setEntity(GZipUtil.compress(multipartEntity));
		PUT.addHeader(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);
		HttpResponse response = client.execute(PUT);
		return JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JUser.class);
	}
	
	private JUser signUp(Map<String, Object> params)
			throws ClientProtocolException, IOException, ParseException,
			PackPackException {
		String url = getBaseUrl() + "user";
		DefaultHttpClient client = new DefaultHttpClient();
		Object attachment = params.get(APIConstants.User.PROFILE_PICTURE);
		if(attachment != null) {
			url = url + "/register";
		}
		HttpPost POST = new HttpPost(url);
		if(attachment != null) {
			MultipartEntity multipartEntity = new MultipartEntity();
			Iterator<String> itr = params.keySet().iterator();
			while (itr.hasNext()) {
				String key = itr.next();
				String text = (String) params.get(key);
				StringBody contentBody = new StringBody(text);
				multipartEntity.addPart(key, contentBody);
			}
			POST.setEntity(GZipUtil.compress(multipartEntity));
			POST.addHeader(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);
		} else {
			POST.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
			SignupDTO dto = new SignupDTO();
			String name = (String) params.get(APIConstants.User.Register.NAME);
			String email = (String) params.get(APIConstants.User.Register.EMAIL);
			String verificationCode = (String) params.get(APIConstants.User.Register.VERIFIER);
			double longitude = (double) params.get(APIConstants.User.Register.LONGITUDE);
			double latitude = (double) params.get(APIConstants.User.Register.LATITUDE);
			dto.setLongitude(longitude);
			dto.setLatitude(latitude);
			dto.setEmail(email);
			dto.setName(name);
			//dto.setPassword(password);
			dto.setVerificationCode(verificationCode);
			String json = JSONUtil.serialize(dto);
			HttpEntity jsonBody = new StringEntity(json, UTF_8);
			POST.setEntity(GZipUtil.compress(jsonBody));
			POST.addHeader(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);
		}
		
		/*POST.addHeader("Content-Type",
				ContentType.MULTIPART_FORM_DATA.getMimeType());*/
		HttpResponse response = client.execute(POST);
		int statusCode = response.getStatusLine().getStatusCode();
		if(statusCode != 200) {
			throw new RuntimeException("Failed status code = " + statusCode);
		}
		return JSONUtil.deserialize(EntityUtils.toString(GZipUtil.decompress(response.getEntity())),
				JUser.class);
	}
	
	private JStatus issueSignupVerifier(String email, String name)
			throws Exception {
		SignupVerifierDTO dto = new SignupVerifierDTO();
		dto.setEmail(email);
		dto.setNameOfUser(name);
		String json = JSONUtil.serialize(dto);
		String url = getBaseUrl() + "user/signup/code";
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		PUT.setEntity(new StringEntity(json, "UTF-8"));
		HttpResponse response = client.execute(PUT);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JStatus.class);
	}
	
	private JStatus issuePasswordResetVerifier(String userName)
			throws Exception {
		String url = getBaseUrl() + "user/passwd/reset/usr/" + userName;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet GET = new HttpGet(url);
		HttpResponse response = client.execute(GET);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JStatus.class);
	}
}