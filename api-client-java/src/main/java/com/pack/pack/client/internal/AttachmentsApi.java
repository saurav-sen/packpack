package com.pack.pack.client.internal;

import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;
import static com.pack.pack.client.api.APIConstants.BASE_URL;
import static com.pack.pack.client.api.APIConstants.CONTENT_TYPE_HEADER;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public class AttachmentsApi extends AbstractAPI {

	private static final String ATTACHMENT = "attachment/";

	@Override
	protected ApiInvoker getInvoker() {
		return new Invoker();
	}

	private HttpResponse getProfilePicture(String userId, String fileName,
			String oAuthToken) throws Exception {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		String url = BASE_URL + ATTACHMENT + "profile/image/" + userId + "/"
				+ fileName;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		CloseableHttpResponse response = client.execute(GET);
		return response;
	}

	private HttpResponse getThumnailImage(String topicId, String packId,
			String fileName, String oAuthToken) throws Exception {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		String url = BASE_URL + ATTACHMENT + "image/" + topicId + "/" + packId
				+ "/thumbnail/" + fileName;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		CloseableHttpResponse response = client.execute(GET);
		return response;
	}

	private HttpResponse getOriginalImage(String topicId, String packId,
			String fileName, String oAuthToken) throws Exception {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		String url = BASE_URL + ATTACHMENT + "image/" + topicId + "/" + packId
				+ "/" + fileName;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		CloseableHttpResponse response = client.execute(GET);
		return response;
	}

	private HttpResponse getThumnailVideo(String topicId, String packId,
			String fileName, String oAuthToken) throws Exception {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		String url = BASE_URL + ATTACHMENT + "video/" + topicId + "/" + packId
				+ "/thumbnail/" + fileName;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		CloseableHttpResponse response = client.execute(GET);
		return response;
	}

	private HttpResponse getOriginalVideo(String topicId, String packId,
			String fileName, String oAuthToken) throws Exception {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		String url = BASE_URL + ATTACHMENT + "video/" + topicId + "/" + packId
				+ "/" + fileName;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		CloseableHttpResponse response = client.execute(GET);
		return response;
	}

	private JStatus uploadImagePack(Map<String, Object> params,
			String oAuthToken) throws ClientProtocolException, IOException,
			ParseException, PackPackException {
		String topicId = (String) params.get(APIConstants.Topic.ID);
		String userId = (String) params.get(APIConstants.User.ID);
		String url = BASE_URL + ATTACHMENT + "image/topic/" + topicId + "/usr/"
				+ userId;
		return uploadPack(params, url, oAuthToken);
	}

	private JStatus uploadVideoPack(Map<String, Object> params,
			String oAuthToken) throws ClientProtocolException, IOException,
			ParseException, PackPackException {
		String topicId = (String) params.get(APIConstants.Topic.ID);
		String userId = (String) params.get(APIConstants.User.ID);
		String url = BASE_URL + ATTACHMENT + "video/topic/" + topicId + "/usr/"
				+ userId;
		return uploadPack(params, url, oAuthToken);
	}

	private JStatus uploadPack(Map<String, Object> params, String url,
			String oAuthToken) throws ClientProtocolException, IOException,
			ParseException, PackPackException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpPost POST = new HttpPost(url);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		Iterator<String> itr = params.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			if (APIConstants.Topic.ID.equals(key)
					|| APIConstants.User.ID.equals(key)) {
				continue;
			} else if (APIConstants.Attachment.FILE_ATTACHMENT.equals(key)) {
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
		POST.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		POST.addHeader(CONTENT_TYPE_HEADER,
				ContentType.MULTIPART_FORM_DATA.getMimeType());
		CloseableHttpResponse response = client.execute(POST);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JStatus.class);
	}

	private JPack addImageToPack(Map<String, Object> params, String oAuthToken)
			throws ClientProtocolException, IOException, ParseException,
			PackPackException {
		String topicId = (String) params.get(APIConstants.Topic.ID);
		String packId = (String) params.get(APIConstants.Pack.ID);
		String userId = (String) params.get(APIConstants.User.ID);
		String url = BASE_URL + ATTACHMENT + "image/topic/" + topicId
				+ "/pack/" + packId + "/usr/" + userId;
		return editPack(params, url, oAuthToken);
	}

	private JPack addVideoToPack(Map<String, Object> params, String oAuthToken)
			throws ClientProtocolException, IOException, ParseException,
			PackPackException {
		String topicId = (String) params.get(APIConstants.Topic.ID);
		String packId = (String) params.get(APIConstants.Pack.ID);
		String userId = (String) params.get(APIConstants.User.ID);
		String url = BASE_URL + ATTACHMENT + "video/topic/" + topicId
				+ "/pack/" + packId + "/usr/" + userId;
		return editPack(params, url, oAuthToken);
	}

	private JPack editPack(Map<String, Object> params, String url,
			String oAuthToken) throws ClientProtocolException, IOException,
			ParseException, PackPackException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpPut PUT = new HttpPut(url);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		Iterator<String> itr = params.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			if (APIConstants.Topic.ID.equals(key)
					|| APIConstants.Pack.ID.equals(key)
					|| APIConstants.User.ID.equals(key)) {
				continue;
			} else if (APIConstants.Attachment.FILE_ATTACHMENT.equals(key)) {
				File file = (File) params.get(key);
				builder.addBinaryBody(key, file,
						ContentType.APPLICATION_OCTET_STREAM, file.getName());
			} else {
				String text = (String) params.get(key);
				builder.addTextBody(key, text);
			}
		}
		HttpEntity entity = builder.build();
		PUT.setEntity(entity);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		PUT.addHeader(CONTENT_TYPE_HEADER,
				ContentType.MULTIPART_FORM_DATA.getMimeType());
		CloseableHttpResponse response = client.execute(PUT);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JPack.class);
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
			if (COMMAND.GET_PROFILE_PICTURE.equals(action)) {
				String userId = (String) params.get(APIConstants.User.ID);
				String fileName = (String) params
						.get(APIConstants.Attachment.FILE_NAME);
				result = getProfilePicture(userId, fileName, oAuthToken);
			} else if (COMMAND.GET_THUMBNAIL_IMAGE_ATTACHMENT.equals(action)) {
				String topicId = (String) params.get(APIConstants.Topic.ID);
				String packId = (String) params.get(APIConstants.Pack.ID);
				String fileName = (String) params
						.get(APIConstants.Attachment.FILE_NAME);
				result = getThumnailImage(topicId, packId, fileName, oAuthToken);
			} else if (COMMAND.GET_ORIGINAL_IMAGE_ATTACHMENT.equals(action)) {
				String topicId = (String) params.get(APIConstants.Topic.ID);
				String packId = (String) params.get(APIConstants.Pack.ID);
				String fileName = (String) params
						.get(APIConstants.Attachment.FILE_NAME);
				result = getOriginalImage(topicId, packId, fileName, oAuthToken);
			} else if (COMMAND.GET_THUMBNAIL_VIDEO_ATTACHMENT.equals(action)) {
				String topicId = (String) params.get(APIConstants.Topic.ID);
				String packId = (String) params.get(APIConstants.Pack.ID);
				String fileName = (String) params
						.get(APIConstants.Attachment.FILE_NAME);
				result = getThumnailVideo(topicId, packId, fileName, oAuthToken);
			} else if (COMMAND.GET_ORIGINAL_VIDEO_ATTACHMENT.equals(action)) {
				String topicId = (String) params.get(APIConstants.Topic.ID);
				String packId = (String) params.get(APIConstants.Pack.ID);
				String fileName = (String) params
						.get(APIConstants.Attachment.FILE_NAME);
				result = getOriginalVideo(topicId, packId, fileName, oAuthToken);
			} else if (COMMAND.UPLOAD_IMAGE_PACK.equals(action)) {
				result = uploadImagePack(params, oAuthToken);
			} else if (COMMAND.ADD_IMAGE_TO_PACK.equals(action)) {
				result = addImageToPack(params, oAuthToken);
			} else if (COMMAND.UPLOAD_VIDEO_PACK.equals(action)) {
				result = uploadVideoPack(params, oAuthToken);
			} else if (COMMAND.ADD_VIDEO_TO_PACK.equals(action)) {
				result = addVideoToPack(params, oAuthToken);
			}
			return result;
		}
	}
}