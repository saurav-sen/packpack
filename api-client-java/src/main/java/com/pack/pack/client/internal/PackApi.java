package com.pack.pack.client.internal;

import static com.pack.pack.client.api.APIConstants.APPLICATION_JSON;
import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;
import static com.pack.pack.client.api.APIConstants.BASE_URL;
import static com.pack.pack.client.api.APIConstants.CONTENT_TYPE_HEADER;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JComment;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPacks;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.dto.CommentDTO;
import com.pack.pack.model.web.dto.ForwardDTO;
import com.pack.pack.model.web.dto.LikeDTO;
import com.pack.pack.model.web.dto.PackReceipent;
import com.pack.pack.model.web.dto.PackReceipentType;

/**
 * 
 * @author Saurav
 *
 */
public class PackApi extends AbstractAPI {

	private Invoker invoker = new Invoker();

	@Override
	protected ApiInvoker getInvoker() {
		return invoker;
	}

	private JPack getPackById(String packId, String oAuthToken)
			throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + "pack/" + packId;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JPack.class);
	}

	@SuppressWarnings("unchecked")
	private Pagination<JPack> getAllLatestPackInDefaultTopics(String userId,
			String pageLink, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + "pack/" + "usr/" + userId + "/page/" + pageLink;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				Pagination.class);
	}

	@SuppressWarnings("unchecked")
	private Pagination<JPack> getAllPacksInTopic(String userId, String topicId,
			String pageLink, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + "pack/" + "usr/" + userId + "/topic/" + topicId
				+ "/page/" + pageLink;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		Pagination<JPack> page = JSONUtil.deserialize(
				EntityUtils.toString(response.getEntity()), Pagination.class);
		List<JPack> result = page.getResult();
		String json = "{\"packs\": " + JSONUtil.serialize(result, false) + "}";
		JPacks packs = JSONUtil.deserialize(json, JPacks.class);
		result = packs.getPacks();
		page.setResult(result);
		return page;
	}

	@SuppressWarnings("unchecked")
	private Pagination<JPack> getAllPackAttachments(String userId,
			String topicId, String packId, String pageLink, String oAuthToken)
			throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + "pack/items/" + "usr/" + userId + "/topic/"
				+ topicId + "/pack/" + packId + "/page/" + pageLink;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		Pagination<JPack> page = JSONUtil.deserialize(
				EntityUtils.toString(response.getEntity()), Pagination.class);
		List<JPack> result = page.getResult();
		String json = "{\"packs\": " + JSONUtil.serialize(result, false) + "}";
		JPacks packs = JSONUtil.deserialize(json, JPacks.class);
		result = packs.getPacks();
		page.setResult(result);
		return page;
	}

	private JStatus forwardPack(String packId, String fromUserId,
			String toUserId, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + "pack/" + packId;
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		PUT.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		ForwardDTO dto = new ForwardDTO();
		dto.setFromUserId(fromUserId);
		PackReceipent receipent = new PackReceipent();
		receipent.setToUserId(toUserId);
		receipent.setType(PackReceipentType.USER);
		dto.getReceipents().add(receipent);
		String json = JSONUtil.serialize(dto);
		HttpEntity jsonBody = new StringEntity(json);
		PUT.setEntity(jsonBody);
		HttpResponse response = client.execute(PUT);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JStatus.class);
	}

	private JStatus forwardPackOverEMail(String packId, String fromUserId,
			String toUserEmail, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + "pack/" + packId + "/email/" + fromUserId + "/"
				+ toUserEmail;
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(PUT);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JStatus.class);
	}

	private JComment addComment(CommentDTO commentDTO, String oAuthToken)
			throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + "pack/comment";
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		PUT.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		String json = JSONUtil.serialize(commentDTO);
		HttpEntity jsonBody = new StringEntity(json);
		PUT.setEntity(jsonBody);
		HttpResponse response = client.execute(PUT);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JComment.class);
	}

	private JStatus addLikeToPack(LikeDTO dto, String oAuthToken)
			throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + "pack/favourite";
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		PUT.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		String json = JSONUtil.serialize(dto);
		HttpEntity jsonBody = new StringEntity(json);
		PUT.setEntity(jsonBody);
		HttpResponse response = client.execute(PUT);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JStatus.class);
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
			if (COMMAND.GET_PACK_BY_ID.equals(action)) {
				String packId = (String) params.get(APIConstants.Pack.ID);
				result = getPackById(packId, oAuthToken);
			} else if (COMMAND.GET_ALL_PACKS_IN_DEFAULT_TOPICS.equals(action)) {
				String userId = (String) params.get(APIConstants.User.ID);
				String pageLink = (String) params
						.get(APIConstants.PageInfo.PAGE_LINK);
				if (pageLink == null || pageLink.trim().equals("")) {
					pageLink = "FIRST_PAGE";
				}
				result = getAllLatestPackInDefaultTopics(userId, pageLink,
						oAuthToken);
			} else if (COMMAND.GET_ALL_PACKS_IN_TOPIC.equals(action)) {
				String userId = (String) params.get(APIConstants.User.ID);
				String topicId = (String) params.get(APIConstants.Topic.ID);
				String pageLink = (String) params
						.get(APIConstants.PageInfo.PAGE_LINK);
				if (pageLink == null || pageLink.trim().equals("")) {
					pageLink = "FIRST_PAGE";
				}
				result = getAllPacksInTopic(userId, topicId, pageLink,
						oAuthToken);
			} else if (COMMAND.GET_ALL_ATTACHMENTS_IN_PACK.equals(action)) {
				String userId = (String) params.get(APIConstants.User.ID);
				String topicId = (String) params.get(APIConstants.Topic.ID);
				String packId = (String) params.get(APIConstants.Pack.ID);
				String pageLink = (String) params
						.get(APIConstants.PageInfo.PAGE_LINK);
				if (pageLink == null || pageLink.trim().equals("")) {
					pageLink = "FIRST_PAGE";
				}
				result = getAllPackAttachments(userId, topicId, packId,
						pageLink, oAuthToken);
			} else if (COMMAND.FORWARD_PACK.equals(action)) {
				String packId = (String) params.get(APIConstants.Pack.ID);
				String fromUserId = (String) params
						.get(APIConstants.ForwardPack.FROM_USER_ID);
				String toUserId = (String) params
						.get(APIConstants.ForwardPack.TO_USER_ID);
				result = forwardPack(packId, fromUserId, toUserId, oAuthToken);
			} else if (COMMAND.FORWARD_PACK_OVER_EMAIL.equals(action)) {
				String packId = (String) params.get(APIConstants.Pack.ID);
				String fromUserId = (String) params
						.get(APIConstants.ForwardPack.FROM_USER_ID);
				String toUserEmail = (String) params
						.get(APIConstants.ForwardPack.TO_USER_EMAIL);
				result = forwardPackOverEMail(packId, fromUserId, toUserEmail,
						oAuthToken);
			} else if (COMMAND.ADD_COMMENT_TO_PACK.equals(action)) {
				String packId = (String) params.get(APIConstants.Pack.ID);
				String fromUserId = (String) params
						.get(APIConstants.Comment.FROM_USER_ID);
				String comment = (String) params
						.get(APIConstants.Comment.COMMENT);
				CommentDTO commentDTO = new CommentDTO();
				commentDTO.setComment(comment);
				commentDTO.setEntityId(packId);
				commentDTO.setFromUserId(fromUserId);
				commentDTO.setEntityType("PACK");
				result = addComment(commentDTO, oAuthToken);
			} else if (COMMAND.ADD_COMMENT_TO_PACK.equals(action)) {
				String packAttachmentId = (String) params
						.get(APIConstants.PackAttachment.ID);
				String fromUserId = (String) params
						.get(APIConstants.Comment.FROM_USER_ID);
				String comment = (String) params
						.get(APIConstants.Comment.COMMENT);
				CommentDTO commentDTO = new CommentDTO();
				commentDTO.setComment(comment);
				commentDTO.setEntityId(packAttachmentId);
				commentDTO.setFromUserId(fromUserId);
				commentDTO.setEntityType("PACK_ATTACHMENT");
				result = addComment(commentDTO, oAuthToken);
			} else if (COMMAND.ADD_LIKE_TO_PACK.equals(action)) {
				String packId = (String) params.get(APIConstants.Pack.ID);
				String userId = (String) params.get(APIConstants.User.ID);
				LikeDTO dto = new LikeDTO();
				dto.setEntityId(packId);
				dto.setUserId(userId);
				dto.setEntityType("PACK");
				result = addLikeToPack(dto, oAuthToken);
			} else if (COMMAND.ADD_LIKE_TO_PACK.equals(action)) {
				String packAttachmentId = (String) params
						.get(APIConstants.PackAttachment.ID);
				String userId = (String) params.get(APIConstants.User.ID);
				LikeDTO dto = new LikeDTO();
				dto.setEntityId(packAttachmentId);
				dto.setUserId(userId);
				dto.setEntityType("PACK_ATTACHMENT");
				result = addLikeToPack(dto, oAuthToken);
			}
			return result;
		}
	}
}