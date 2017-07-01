package com.pack.pack.client.internal;

import static com.pack.pack.client.api.APIConstants.APPLICATION_JSON;
import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;
import static com.pack.pack.client.api.APIConstants.CONTENT_TYPE_HEADER;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.ByteBody;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.client.internal.response.cache.CachingHttpClient;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.JTopics;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.PromoteStatus;
import com.pack.pack.model.web.dto.EntityPromoteDTO;
import com.pack.pack.model.web.dto.TopicEditDto;
import com.pack.pack.model.web.dto.TopicFollowDTO;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
class TopicApi extends BaseAPI {

	TopicApi(String baseUrl) {
		super(baseUrl);
	}

	private Invoker invoker = new Invoker();

	@Override
	protected ApiInvoker getInvoker() {
		return invoker;
	}

	@SuppressWarnings("unchecked")
	private Pagination<JTopic> getUserTopicList(String pageLink,
			String oAuthToken, String userId, String category) throws Exception {
		String url = getBaseUrl() + "topic/" + pageLink + "/category/"
				+ category + "/user/" + userId;
		HttpClient client = new DefaultHttpClient();
		if (getCacheStorage() != null) {
			CachingHttpClient httpClient = new CachingHttpClient(client,
					getCacheStorage(), getCacheConfig());
			client = httpClient;
		}
		HttpGet GET = new HttpGet(url);
		GET.setHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		Pagination<JTopic> page = JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), Pagination.class);
		List<JTopic> result = page.getResult();
		String json = "{\"topics\": " + JSONUtil.serialize(result, false) + "}";
		JTopics topics = JSONUtil.deserialize(json, JTopics.class);
		result = topics.getTopics();
		page.setResult(result);
		return page;
	}

	private JStatus followTopic(String userId, String topicId, String oAuthToken)
			throws Exception {
		String url = getBaseUrl() + "activity/topic";
		TopicFollowDTO dto = new TopicFollowDTO();
		dto.setTopicId(topicId);
		dto.setUserId(userId);
		String json = JSONUtil.serialize(dto);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost POST = new HttpPost(url);
		POST.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		POST.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		HttpEntity jsonBody = new StringEntity(json);
		POST.setEntity(GZipUtil.compress(jsonBody));
		POST.addHeader(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);
		HttpResponse response = client.execute(POST);
		return JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JStatus.class);
	}

	private JStatus neglectTopic(String userId, String topicId,
			String oAuthToken) throws Exception {
		String url = getBaseUrl() + "activity/topic/" + topicId + "/user/"
				+ userId;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpDelete DELETE = new HttpDelete(url);
		DELETE.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(DELETE);
		return JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JStatus.class);
	}

	private JTopic getTopicById(String topicId, String oAuthToken)
			throws Exception {
		String url = getBaseUrl() + "topic/" + topicId;
		HttpClient client = new DefaultHttpClient();
		if (getCacheStorage() != null) {
			CachingHttpClient httpClient = new CachingHttpClient(client,
					getCacheStorage(), getCacheConfig());
			client = httpClient;
		}
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		return JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JTopic.class);
	}
	
	private PromoteStatus promoteTopic(String topicId, String userId,
			String oAuthToken) throws Exception {
		String url = getBaseUrl() + "promote/usr/" + userId;
		HttpClient client = new DefaultHttpClient();
		if (getCacheStorage() != null) {
			CachingHttpClient httpClient = new CachingHttpClient(client,
					getCacheStorage(), getCacheConfig());
			client = httpClient;
		}
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		PUT.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		EntityPromoteDTO dto = new EntityPromoteDTO();
		dto.setId(topicId);
		dto.setType(JTopic.class.getName());
		String json = JSONUtil.serialize(dto);
		HttpEntity jsonBody = new StringEntity(json);
		PUT.setEntity(jsonBody);
		HttpResponse response = client.execute(PUT);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				PromoteStatus.class);
	}
	
	private List<JTopic> getUserOwnedTopics(String userId, String oAuthToken)
			throws Exception {
		String url = getBaseUrl() + "topic/owner/" + userId;
		HttpClient client = new DefaultHttpClient();
		if (getCacheStorage() != null) {
			CachingHttpClient httpClient = new CachingHttpClient(client,
					getCacheStorage(), getCacheConfig());
			client = httpClient;
		}
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		JTopics topics = JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JTopics.class);
		if (topics == null) {
			return Collections.emptyList();
		}
		return topics.getTopics();
	}
	
	private JTopic editTopic(Map<String, Object> params, String oAuthToken,
			MultipartRequestProgressListener listener) throws Exception {
		if(listener != null) {
			listener.countTransferProgress(10, 100);
		}
		String userId = (String) params.get(APIConstants.Topic.OWNER_ID);
		String topicId = (String) params.get(APIConstants.Topic.ID);
		String topicName = (String) params.get(APIConstants.Topic.NAME);
		String topicDescription = (String) params
				.get(APIConstants.Topic.DESCRIPTION);
		TopicEditDto dto = new TopicEditDto();
		dto.setName(topicName);
		dto.setDescription(topicDescription);
		String url = getBaseUrl() + "topic/" + topicId + "/owner/" + userId;
		HttpClient client = new DefaultHttpClient();
		if (getCacheStorage() != null) {
			CachingHttpClient httpClient = new CachingHttpClient(client,
					getCacheStorage(), getCacheConfig());
			client = httpClient;
		}
		HttpPut PUT = new HttpPut(url);
		String json = JSONUtil.serialize(dto);
		HttpEntity jsonBody = new StringEntity(json);
		PUT.setEntity(jsonBody);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		PUT.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		HttpResponse response = client.execute(PUT);
		JTopic result = JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JTopic.class);
		if(listener != null) {
			listener.countTransferProgress(100, 100);
		}
		return result;
	}

	private JTopic createTopic(Map<String, Object> params, String oAuthToken)
			throws Exception {
		/*
		 * int followers = 0; JTopic topic = new JTopic();
		 * topic.setDescription(description); topic.setName(name);
		 * topic.setFollowers(followers); topic.setOwnerName(ownerName);
		 * topic.setOwnerId(ownerId); topic.setCategory(category); String json =
		 * JSONUtil.serialize(topic, false);
		 */
		String url = getBaseUrl() + "topic/";
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost POST = new HttpPost(url);
		POST.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		// POST.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		/*
		 * HttpEntity jsonBody = new StringEntity(json, APPLICATION_JSON);
		 * POST.setEntity(jsonBody);
		 */

		MultipartEntity multipartEntity = new MultipartEntity();
		// MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		Iterator<String> itr = params.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			if (APIConstants.Topic.WALLPAPER.equals(key)) {
				Object obj = params.get(key);
				if (obj instanceof File) {
					File file = (File) obj;
					FileBody fileBody = new FileBody(file, file.getName(),
							HTTP.OCTET_STREAM_TYPE, null);
					multipartEntity.addPart(key, fileBody);
				} else if(obj instanceof ByteBody) {
					ByteBody byteBody = (ByteBody)obj;
					byte[] bytes = byteBody.getBytes();
					ByteArrayBody byteArrayBody = new ByteArrayBody(bytes, 
							UUID.randomUUID().toString());
					multipartEntity.addPart(key, byteArrayBody);
				} else {
					throw new PackPackException(ErrorCodes.PACK_ERR_94,
							"Unsupported Media Type, selected wallpaper");
				}
				// builder.addBinaryBody(key, file, HTTP.OCTET_STREAM_TYPE,
				// file.getName());
			} else {
				String text = (String) params.get(key);
				// builder.addTextBody(key, text);
				StringBody contentBody = new StringBody(text, TEXT_PLAIN, Charset.forName(UTF_8));
				multipartEntity.addPart(key, contentBody);
			}
		}
		// HttpEntity entity = builder.build();
		// POST.setEntity(entity);
		POST.setEntity(GZipUtil.compress(multipartEntity));
		POST.addHeader(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);

		HttpResponse response = client.execute(POST);
		return JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JTopic.class);
	}
	
	private JTopic editTopicSettings(String topicId, String userId, String key,
			String value, String oAuthToken) throws Exception {
		String url = getBaseUrl() + "topic/" + topicId + "/settings/" + key
				+ "/" + value + "/usr/" + userId;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(PUT);
		return JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JTopic.class);
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
			return invoke(null);
		}

		@Override
		public Object invoke(MultipartRequestProgressListener listener)
				throws Exception {
			Object result = null;
			if (action == COMMAND.GET_USER_FOLLOWED_TOPIC_LIST) {
				String pageLink = (String) params
						.get(APIConstants.PageInfo.PAGE_LINK);
				if (pageLink == null || pageLink.trim().equals("")) {
					pageLink = "FIRST_PAGE";
				}
				String userId = (String) params.get(APIConstants.User.ID);
				String category = (String) params
						.get(APIConstants.Topic.CATEGORY);
				result = getUserTopicList(pageLink, oAuthToken, userId,
						category);
			} else if (action == COMMAND.FOLLOW_TOPIC) {
				String userId = (String) params.get(APIConstants.User.ID);
				String topicId = (String) params.get(APIConstants.Topic.ID);
				result = followTopic(userId, topicId, oAuthToken);
			} else if (action == COMMAND.NEGLECT_TOPIC) {
				String topicId = (String) params.get(APIConstants.Topic.ID);
				String userId = (String) params.get(APIConstants.User.ID);
				result = neglectTopic(userId, topicId, oAuthToken);
			} else if (action == COMMAND.GET_TOPIC_BY_ID) {
				String topicId = (String) params.get(APIConstants.Topic.ID);
				result = getTopicById(topicId, oAuthToken);
			} else if (action == COMMAND.CREATE_NEW_TOPIC) {
				/*
				 * String ownerId = (String) params
				 * .get(APIConstants.Topic.OWNER_ID); String ownerName =
				 * (String) params .get(APIConstants.Topic.OWNER_NAME); String
				 * name = (String) params.get(APIConstants.Topic.NAME); String
				 * description = (String) params
				 * .get(APIConstants.Topic.DESCRIPTION); String category =
				 * (String) params .get(APIConstants.Topic.CATEGORY);
				 */
				result = createTopic(params, oAuthToken);
			} else if(action == COMMAND.EDIT_EXISTING_TOPIC) {
				result = editTopic(params, oAuthToken, listener);
			} else if(action == COMMAND.GET_USER_OWNED_TOPICS) {
				String userId = (String) params.get(APIConstants.User.ID);
				result = getUserOwnedTopics(userId, oAuthToken);
			} else if(action == COMMAND.EDIT_TOPIC_SETTINGS) {
				String topicId = (String) params.get(APIConstants.Topic.ID);
				String userId = (String) params.get(APIConstants.User.ID);
				String key = (String) params
						.get(APIConstants.TopicSettings.KEY);
				String value = (String) params
						.get(APIConstants.TopicSettings.VALUE);
				result = editTopicSettings(topicId, userId, key, value,
						oAuthToken);
			} else if(action == COMMAND.PROMOTE_TOPIC) {
				String topicId = (String) params.get(APIConstants.Topic.ID);
				String userId = (String) params.get(APIConstants.User.ID);
				result = promoteTopic(topicId, userId, oAuthToken);
			}
			return result;
		}
	}
}