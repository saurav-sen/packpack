package com.pack.pack.client.internal;

import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;
import static com.pack.pack.client.api.APIConstants.BASE_URL;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.client.internal.multipart.ProgressTrackedMultipartEntity;
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

	private Invoker invoker = new Invoker();

	@Override
	protected ApiInvoker getInvoker() {
		return invoker;
	}

	private HttpResponse getProfilePicture(String userId, String fileName,
			String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + ATTACHMENT + "profile/image/" + userId + "/"
				+ fileName;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		return response;
	}

	/*private HttpResponse getThumnailImage(String topicId, String packId,
			String fileName, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + ATTACHMENT + "image/" + topicId + "/" + packId
				+ "/thumbnail/" + fileName;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		return response;
	}*/

	private HttpResponse getOriginalImage(String topicId, String packId,
			String fileName, String oAuthToken, int width, int height) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + ATTACHMENT + "image/" + topicId + "/" + packId
				+ "/" + fileName + "?w=" + width + "&h=" + height;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		return response;
	}

	private HttpResponse getThumnailVideo(String topicId, String packId,
			String fileName, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + ATTACHMENT + "video/" + topicId + "/" + packId
				+ "/thumbnail/" + fileName;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		return response;
	}

	private HttpResponse getOriginalVideo(String topicId, String packId,
			String fileName, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + ATTACHMENT + "video/" + topicId + "/" + packId
				+ "/" + fileName;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		return response;
	}

	private JStatus uploadImagePack(Map<String, Object> params,
			String oAuthToken, MultipartRequestProgressListener listener)
			throws ClientProtocolException, IOException, ParseException,
			PackPackException {
		String topicId = (String) params.get(APIConstants.Topic.ID);
		String userId = (String) params.get(APIConstants.User.ID);
		String url = BASE_URL + ATTACHMENT + "image/topic/" + topicId + "/usr/"
				+ userId;
		return uploadPack(params, url, oAuthToken, listener);
	}

	private JStatus uploadVideoPack(Map<String, Object> params,
			String oAuthToken, MultipartRequestProgressListener listener)
			throws ClientProtocolException, IOException, ParseException,
			PackPackException {
		String topicId = (String) params.get(APIConstants.Topic.ID);
		String userId = (String) params.get(APIConstants.User.ID);
		String url = BASE_URL + ATTACHMENT + "video/topic/" + topicId + "/usr/"
				+ userId;
		return uploadPack(params, url, oAuthToken, listener);
	}

	private JStatus uploadPack(Map<String, Object> params, String url,
			String oAuthToken, MultipartRequestProgressListener listener)
			throws ClientProtocolException, IOException, ParseException,
			PackPackException {
		DefaultHttpClient client = new DefaultHttpClient();// new
															// DefaultHttpClient();
		HttpPost POST = new HttpPost(url);
		// MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		MultipartEntity multipartEntity = new ProgressTrackedMultipartEntity(
				listener);
		Iterator<String> itr = params.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			if (APIConstants.Topic.ID.equals(key)
					|| APIConstants.User.ID.equals(key)) {
				continue;
			} else if (APIConstants.Attachment.FILE_ATTACHMENT.equals(key)) {
				File file = (File) params.get(key);
				FileBody fileBody = new FileBody(file, file.getName(),
						HTTP.OCTET_STREAM_TYPE, null);
				multipartEntity.addPart(key, fileBody);
				/*
				 * builder.addBinaryBody(key, file, HTTP.OCTET_STREAM_TYPE,
				 * file.getName());
				 */
			} else {
				String text = (String) params.get(key);
				StringBody textBody = new StringBody(text);
				multipartEntity.addPart(key, textBody);
				// builder.addTextBody(key, text);
			}
		}
		POST.setEntity(multipartEntity);
		// HttpEntity entity = builder.build();
		// POST.setEntity(entity);
		POST.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		/*
		 * POST.addHeader(CONTENT_TYPE_HEADER,
		 * ContentType.MULTIPART_FORM_DATA.getMimeType());
		 */
		/*
		 * POST.addHeader(CONTENT_TYPE_HEADER, "multipart/form-data");
		 */
		HttpResponse response = client.execute(POST);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JStatus.class);
	}

	private JPack addImageToPack(Map<String, Object> params, String oAuthToken,
			MultipartRequestProgressListener listener)
			throws ClientProtocolException, IOException, ParseException,
			PackPackException {
		String topicId = (String) params.get(APIConstants.Topic.ID);
		String packId = (String) params.get(APIConstants.Pack.ID);
		String userId = (String) params.get(APIConstants.User.ID);
		String url = BASE_URL + ATTACHMENT + "image/topic/" + topicId
				+ "/pack/" + packId + "/usr/" + userId;
		return editPack(params, url, oAuthToken, listener);
	}

	private JPack addVideoToPack(Map<String, Object> params, String oAuthToken,
			MultipartRequestProgressListener listener)
			throws ClientProtocolException, IOException, ParseException,
			PackPackException {
		String topicId = (String) params.get(APIConstants.Topic.ID);
		String packId = (String) params.get(APIConstants.Pack.ID);
		String userId = (String) params.get(APIConstants.User.ID);
		String url = BASE_URL + ATTACHMENT + "video/topic/" + topicId
				+ "/pack/" + packId + "/usr/" + userId;
		return editPack(params, url, oAuthToken, listener);
	}

	private JPack editPack(Map<String, Object> params, String url,
			String oAuthToken, MultipartRequestProgressListener listener)
			throws ClientProtocolException, IOException, ParseException,
			PackPackException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPut PUT = new HttpPut(url);
		MultipartEntity multipartEntity = new ProgressTrackedMultipartEntity(
				listener);
		// MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		Iterator<String> itr = params.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			if (APIConstants.Topic.ID.equals(key)
					|| APIConstants.Pack.ID.equals(key)
					|| APIConstants.User.ID.equals(key)) {
				continue;
			} else if (APIConstants.Attachment.FILE_ATTACHMENT.equals(key)) {
				File file = (File) params.get(key);
				FileBody fileBody = new FileBody(file, file.getName(),
						HTTP.OCTET_STREAM_TYPE, null);
				multipartEntity.addPart(key, fileBody);
				/*
				 * builder.addBinaryBody(key, file,
				 * ContentType.APPLICATION_OCTET_STREAM, file.getName());
				 */
			} else {
				String text = (String) params.get(key);
				StringBody textBody = new StringBody(text);
				multipartEntity.addPart(key, textBody);
				// builder.addTextBody(key, text);
			}
		}
		PUT.setEntity(multipartEntity);
		// HttpEntity entity = builder.build();
		// PUT.setEntity(entity);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		/*
		 * PUT.addHeader(CONTENT_TYPE_HEADER, "multipart/form-data");
		 */
		/*
		 * PUT.addHeader(CONTENT_TYPE_HEADER,
		 * ContentType.MULTIPART_FORM_DATA.getMimeType());
		 */
		HttpResponse response = client.execute(PUT);
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
			return invoke(null);
		}

		@Override
		public Object invoke(MultipartRequestProgressListener listener)
				throws Exception {
			Object result = null;
			if (COMMAND.GET_PROFILE_PICTURE.equals(action)) {
				String userId = (String) params.get(APIConstants.User.ID);
				String fileName = (String) params
						.get(APIConstants.Attachment.FILE_NAME);
				result = getProfilePicture(userId, fileName, oAuthToken);
			} else if (COMMAND.GET_ORIGINAL_IMAGE_ATTACHMENT.equals(action)) {
				String topicId = (String) params.get(APIConstants.Topic.ID);
				String packId = (String) params.get(APIConstants.Pack.ID);
				String fileName = (String) params
						.get(APIConstants.Attachment.FILE_NAME);
				Integer widht = (Integer) params.get(APIConstants.Image.WIDTH);
				if(widht == null) {
					widht = -1;
				}
				Integer height = (Integer) params.get(APIConstants.Image.HEIGHT);
				if(height == null) {
					height = -1;
				}
				result = getOriginalImage(topicId, packId, fileName, oAuthToken, widht, height);
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
				result = uploadImagePack(params, oAuthToken, listener);
			} else if (COMMAND.ADD_IMAGE_TO_PACK.equals(action)) {
				result = addImageToPack(params, oAuthToken, listener);
			} else if (COMMAND.UPLOAD_VIDEO_PACK.equals(action)) {
				result = uploadVideoPack(params, oAuthToken, listener);
			} else if (COMMAND.ADD_VIDEO_TO_PACK.equals(action)) {
				result = addVideoToPack(params, oAuthToken, listener);
			}
			return result;
		}
	}
}