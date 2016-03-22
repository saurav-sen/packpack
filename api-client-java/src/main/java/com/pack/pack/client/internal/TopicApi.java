package com.pack.pack.client.internal;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.dto.TopicFollowDTO;

import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;
import static com.pack.pack.client.api.APIConstants.CONTENT_TYPE_HEADER;
import static com.pack.pack.client.api.APIConstants.APPLICATION_JSON;
import static com.pack.pack.client.api.APIConstants.BASE_URL;

/**
 * 
 * @author Saurav
 *
 */
public class TopicApi extends AbstractAPI {

	@Override
	protected ApiInvoker getInvoker() {
		return new Invoker();
	}

	@SuppressWarnings("unchecked")
	private Pagination<JTopic> getUserTopicList(String pageLink,
			String oAuthToken, String userId) throws Exception {
		String url = BASE_URL + "activity/topic/" + pageLink + "/user/"
				+ userId;
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet GET = new HttpGet(url);
		GET.setHeader(AUTHORIZATION_HEADER, oAuthToken);
		CloseableHttpResponse response = client.execute(GET);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				Pagination.class);
	}

	private JStatus followTopic(String userId, String topicId, String oAuthToken)
			throws Exception {
		String url = BASE_URL + "activity/topic";
		TopicFollowDTO dto = new TopicFollowDTO();
		dto.setTopicId(topicId);
		dto.setUserId(userId);
		String json = JSONUtil.serialize(dto);
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpPost POST = new HttpPost(url);
		POST.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		POST.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		HttpEntity jsonBody = new StringEntity(json);
		POST.setEntity(jsonBody);
		CloseableHttpResponse response = client.execute(POST);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JStatus.class);
	}

	private JStatus neglectTopic(String userId, String topicId,
			String oAuthToken) throws Exception {
		String url = BASE_URL + "activity/topic/" + topicId + "/user/" + userId;
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpDelete DELETE = new HttpDelete(url);
		DELETE.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		CloseableHttpResponse response = client.execute(DELETE);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JStatus.class);
	}

	private JTopic getTopicById(String topicId, String oAuthToken)
			throws Exception {
		String url = BASE_URL + "topic/" + topicId;
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		CloseableHttpResponse response = client.execute(GET);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JTopic.class);
	}

	private JTopic createTopic(String ownerId, String ownerName, String name,
			String description, String category, String oAuthToken) throws Exception {
		int followers = 0;
		JTopic topic = new JTopic();
		topic.setDescription(description);
		topic.setName(name);
		topic.setFollowers(followers);
		topic.setOwnerName(ownerName);
		topic.setOwnerId(ownerId);
		topic.setCategory(category);
		String json = JSONUtil.serialize(topic);
		String url = BASE_URL + "topic/";
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpPost POST = new HttpPost(url);
		POST.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		POST.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		HttpEntity jsonBody = new StringEntity(json);
		POST.setEntity(jsonBody);
		CloseableHttpResponse response = client.execute(POST);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JTopic.class);
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
			if (action == COMMAND.GET_USER_FOLLOWED_TOPIC_LIST) {
				String pageLink = (String) params
						.get(APIConstants.PageInfo.PAGE_LINK);
				if (pageLink == null || pageLink.trim().equals("")) {
					pageLink = "FIRST_PAGE";
				}
				String userId = (String) params.get(APIConstants.User.ID);
				result = getUserTopicList(pageLink, oAuthToken, userId);
			} else if (action == COMMAND.FOLLOW_TOPIC) {
				String userId = (String) params.get(APIConstants.User.ID);
				String topicId = (String) params
						.get(APIConstants.Topic.ID);
				result = followTopic(userId, topicId, oAuthToken);
			} else if (action == COMMAND.NEGLECT_TOPIC) {
				String topicId = (String) params
						.get(APIConstants.Topic.ID);
				String userId = (String) params.get(APIConstants.User.ID);
				result = neglectTopic(userId, topicId, oAuthToken);
			} else if (action == COMMAND.GET_TOPIC_BY_ID) {
				String topicId = (String) params
						.get(APIConstants.Topic.ID);
				result = getTopicById(topicId, oAuthToken);
			} else if (action == COMMAND.CREATE_NEW_TOPIC) {
				String ownerId = (String) params
						.get(APIConstants.Topic.OWNER_ID);
				String ownerName = (String) params
						.get(APIConstants.Topic.OWNER_NAME);
				String name = (String) params.get(APIConstants.Topic.NAME);
				String description = (String) params
						.get(APIConstants.Topic.DESCRIPTION);
				String category = (String) params
						.get(APIConstants.Topic.CATEGORY);
				result = createTopic(ownerId, ownerName, name, description,
						category, oAuthToken);
			}
			return result;
		}
	}
}