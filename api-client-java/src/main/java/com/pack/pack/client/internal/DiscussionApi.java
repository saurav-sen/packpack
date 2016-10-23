package com.pack.pack.client.internal;

import static com.pack.pack.client.api.APIConstants.APPLICATION_JSON;
import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;
import static com.pack.pack.client.api.APIConstants.CONTENT_TYPE_HEADER;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.EntityType;
import com.pack.pack.model.web.JDiscussion;
import com.pack.pack.model.web.JDiscussions;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.dto.CommentDTO;
import com.pack.pack.model.web.dto.DiscussionDTO;
import com.pack.pack.model.web.dto.LikeDTO;

/**
 * 
 * @author Saurav
 *
 */
class DiscussionApi extends BaseAPI {

	DiscussionApi(String baseUrl) {
		super(baseUrl);
	}

	private Invoker invoker = new Invoker();

	@Override
	protected ApiInvoker getInvoker() {
		return invoker;
	}

	@SuppressWarnings("unchecked")
	private Pagination<JDiscussion> getAllDiscussionsForTopic(String topicId,
			String userId, String pageLink, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "discussion/topic/" + topicId + "/usr/"
				+ userId + "/page/" + pageLink;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		Pagination<JDiscussion> page = JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), Pagination.class);
		if (page == null)
			return page;
		List<JDiscussion> result = page.getResult();
		String json = "{\"discussions\": " + JSONUtil.serialize(result, false)
				+ "}";
		JDiscussions discussions = JSONUtil.deserialize(json,
				JDiscussions.class);
		result = discussions.getDiscussions();
		page.setResult(result);
		return page;
	}

	@SuppressWarnings("unchecked")
	private Pagination<JDiscussion> getAllDiscussionsForPack(String packId,
			String userId, String pageLink, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "discussion/pack/" + packId + "/usr/"
				+ userId + "/page/" + pageLink;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		Pagination<JDiscussion> page = JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), Pagination.class);
		if (page == null)
			return page;
		List<JDiscussion> result = page.getResult();
		String json = "{\"discussions\": " + JSONUtil.serialize(result, false)
				+ "}";
		JDiscussions discussions = JSONUtil.deserialize(json,
				JDiscussions.class);
		result = discussions.getDiscussions();
		page.setResult(result);
		return page;
	}
	
	@SuppressWarnings("unchecked")
	private Pagination<JDiscussion> getAllRepliesForDiscussion(String discussionId,
			String userId, String pageLink, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "discussion/discussion/" + discussionId + "/usr/"
				+ userId + "/page/" + pageLink;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		Pagination<JDiscussion> page = JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), Pagination.class);
		if (page == null)
			return page;
		List<JDiscussion> result = page.getResult();
		String json = "{\"discussions\": " + JSONUtil.serialize(result, false)
				+ "}";
		JDiscussions discussions = JSONUtil.deserialize(json,
				JDiscussions.class);
		result = discussions.getDiscussions();
		page.setResult(result);
		return page;
	}

	private JDiscussion startDiscussionOnTopic(String topicId, String userId,
			String title, String content, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		DiscussionDTO dto = new DiscussionDTO();
		dto.setTitle(title);
		dto.setContent(content);
		String url = getBaseUrl() + "discussion/topic/" + topicId + "/usr/"
				+ userId;
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		PUT.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		String json = JSONUtil.serialize(dto);
		HttpEntity jsonBody = new StringEntity(json);
		PUT.setEntity(GZipUtil.compress(jsonBody));
		PUT.addHeader(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);
		HttpResponse response = client.execute(PUT);
		return JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JDiscussion.class);
	}

	private JDiscussion startDiscussionOnPack(String packId, String userId,
			String title, String content, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		DiscussionDTO dto = new DiscussionDTO();
		dto.setTitle(title);
		dto.setContent(content);
		String url = getBaseUrl() + "discussion/pack/" + packId + "/usr/"
				+ userId;
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		PUT.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		String json = JSONUtil.serialize(dto);
		HttpEntity jsonBody = new StringEntity(json);
		PUT.setEntity(GZipUtil.compress(jsonBody));
		PUT.addHeader(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);
		HttpResponse response = client.execute(PUT);
		return JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JDiscussion.class);
	}

	private JDiscussion addReplyToDiscussion(String discussionId,
			String userId, String content, EntityType type, String oAuthToken)
			throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		CommentDTO dto = new CommentDTO();
		dto.setComment(content);
		dto.setEntityId(discussionId);
		dto.setFromUserId(userId);
		String url = getBaseUrl() + "discussion/" + discussionId + "/usr/" + userId;
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		PUT.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		String json = JSONUtil.serialize(dto);
		HttpEntity jsonBody = new StringEntity(json);
		PUT.setEntity(GZipUtil.compress(jsonBody));
		PUT.addHeader(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);
		HttpResponse response = client.execute(PUT);
		return JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JDiscussion.class);
	}

	private JDiscussion getDiscussionById(String discussionId, String userId,
			String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "discussion/" + discussionId;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		return JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JDiscussion.class);
	}

	private JStatus addLikeToDiscussion(String discussionId, String userId,
			EntityType type, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "discussion/favourite/" + discussionId;
		HttpPost POST = new HttpPost(url);
		POST.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		LikeDTO dto = new LikeDTO();
		dto.setEntityId(discussionId);
		dto.setUserId(userId);
		String json = JSONUtil.serialize(dto);
		HttpEntity jsonBody = new StringEntity(json);
		POST.setEntity(GZipUtil.compress(jsonBody));
		POST.addHeader(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);
		HttpResponse response = client.execute(POST);
		return JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JStatus.class);
	}

	private JStatus addLikeToReply(String discussionId, String userId,
			EntityType type, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "discussion/favourite/reply/"
				+ discussionId;
		HttpPost POST = new HttpPost(url);
		POST.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		LikeDTO dto = new LikeDTO();
		dto.setEntityId(discussionId);
		dto.setUserId(userId);
		String json = JSONUtil.serialize(dto);
		HttpEntity jsonBody = new StringEntity(json);
		POST.setEntity(GZipUtil.compress(jsonBody));
		POST.addHeader(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);
		HttpResponse response = client.execute(POST);
		return JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JStatus.class);
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
			if (COMMAND.GET_ALL_DISCUSSIONS_FOR_TOPIC.equals(action)) {
				String topicId = (String) params.get(APIConstants.Topic.ID);
				String userId = (String) params.get(APIConstants.User.ID);
				String pageLink = (String) params
						.get(APIConstants.PageInfo.PAGE_LINK);
				if (pageLink == null || pageLink.trim().equals("")) {
					pageLink = "FIRST_PAGE";
				}
				return getAllDiscussionsForTopic(topicId, userId, pageLink,
						oAuthToken);
			} else if (COMMAND.GET_ALL_DISCUSSIONS_FOR_PACK.equals(action)) {
				String packId = (String) params.get(APIConstants.Pack.ID);
				String userId = (String) params.get(APIConstants.User.ID);
				String pageLink = (String) params
						.get(APIConstants.PageInfo.PAGE_LINK);
				if (pageLink == null || pageLink.trim().equals("")) {
					pageLink = "FIRST_PAGE";
				}
				return getAllDiscussionsForPack(packId, userId, pageLink,
						oAuthToken);
			} else if (COMMAND.GET_ALL_REPLIES_FOR_DISCUSSION.equals(action)) {
				String discussionId = (String) params.get(APIConstants.Discussion.ID);
				String userId = (String) params.get(APIConstants.User.ID);
				String pageLink = (String) params
						.get(APIConstants.PageInfo.PAGE_LINK);
				if (pageLink == null || pageLink.trim().equals("")) {
					pageLink = "FIRST_PAGE";
				}
				return getAllRepliesForDiscussion(discussionId, userId, pageLink,
						oAuthToken);
			} else if (COMMAND.START_DISCUSSION_ON_TOPIC.equals(action)) {
				String topicId = (String) params.get(APIConstants.Topic.ID);
				String userId = (String) params.get(APIConstants.User.ID);
				String title = (String) params
						.get(APIConstants.Discussion.TITLE);
				String content = (String) params
						.get(APIConstants.Discussion.CONTENT);
				return startDiscussionOnTopic(topicId, userId, title, content,
						oAuthToken);
			} else if (COMMAND.START_DISCUSSION_ON_PACK.equals(action)) {
				String packId = (String) params.get(APIConstants.Pack.ID);
				String userId = (String) params.get(APIConstants.User.ID);
				String title = (String) params
						.get(APIConstants.Discussion.TITLE);
				String content = (String) params
						.get(APIConstants.Discussion.CONTENT);
				return startDiscussionOnPack(packId, userId, title, content,
						oAuthToken);
			} else if (COMMAND.ADD_REPLY_TO_DISCUSSION.equals(action)) {
				String discussionId = (String) params
						.get(APIConstants.Discussion.ID);
				String userId = (String) params.get(APIConstants.User.ID);
				String type = (String) params.get(APIConstants.Discussion.TYPE);
				String content = (String) params
						.get(APIConstants.Discussion.CONTENT);
				if (EntityType.DISCUSSION.name().equalsIgnoreCase(type)) {
					return addReplyToDiscussion(discussionId, userId, content,
							EntityType.DISCUSSION, oAuthToken);
				}
			} else if (COMMAND.GET_DISCUSSION_BY_ID.equals(action)) {
				String discussionId = (String) params
						.get(APIConstants.Discussion.ID);
				String userId = (String) params.get(APIConstants.User.ID);
				return getDiscussionById(discussionId, userId, oAuthToken);
			} else if (COMMAND.ADD_LIKE_TO_DISCUSSION.equals(action)) {
				String discussionId = (String) params
						.get(APIConstants.Discussion.ID);
				String userId = (String) params.get(APIConstants.User.ID);
				String type = (String) params.get(APIConstants.Discussion.TYPE);
				if (EntityType.DISCUSSION.name().equalsIgnoreCase(type)) {
					return addLikeToDiscussion(discussionId, userId,
							EntityType.DISCUSSION, oAuthToken);
				} else if (EntityType.REPLY.name().equalsIgnoreCase(type)) {
					return addLikeToReply(discussionId, userId,
							EntityType.REPLY, oAuthToken);
				}
			}
			return null;
		}

		@Override
		public Object invoke(MultipartRequestProgressListener listener)
				throws Exception {
			throw new UnsupportedOperationException(
					"Progress track is not supported by Discussion API");
		}
	}
}