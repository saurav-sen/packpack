package com.pack.pack.client.internal;

import static com.pack.pack.client.api.APIConstants.APPLICATION_JSON;
import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;
import static com.pack.pack.client.api.APIConstants.CONTENT_TYPE_HEADER;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.ByteBody;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.client.internal.multipart.ProgressTrackedMultipartEntity;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JAttachmentStoryID;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.PromoteStatus;
import com.pack.pack.model.web.dto.EntityPromoteDTO;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
class AttachmentsApi extends BaseAPI {

	AttachmentsApi(String baseUrl) {
		super(baseUrl);
	}

	private static final String ATTACHMENT = "attachment/";

	private Invoker invoker = new Invoker();

	@Override
	protected ApiInvoker getInvoker() {
		return invoker;
	}

	private HttpResponse getProfilePicture(String userId, String fileName,
			String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + ATTACHMENT + "profile/image/" + userId + "/"
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
		String url = getBaseUrl() + ATTACHMENT + "image/" + topicId + "/" + packId
				+ "/" + fileName + "?w=" + width + "&h=" + height;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		return response;
	}

	private HttpResponse getThumnailVideo(String topicId, String packId,
			String fileName, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + ATTACHMENT + "video/" + topicId + "/" + packId
				+ "/thumbnail/" + fileName;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		return response;
	}

	private HttpResponse getOriginalVideo(String topicId, String packId,
			String fileName, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + ATTACHMENT + "video/" + topicId + "/" + packId
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
		String url = getBaseUrl() + ATTACHMENT + "image/topic/" + topicId + "/usr/"
				+ userId;
		return uploadPack(params, url, oAuthToken, listener);
	}

	private JStatus uploadVideoPack(Map<String, Object> params,
			String oAuthToken, MultipartRequestProgressListener listener)
			throws ClientProtocolException, IOException, ParseException,
			PackPackException {
		String topicId = (String) params.get(APIConstants.Topic.ID);
		String userId = (String) params.get(APIConstants.User.ID);
		String url = getBaseUrl() + ATTACHMENT + "video/topic/" + topicId + "/usr/"
				+ userId;
		return uploadPack(params, url, oAuthToken, listener);
	}
	
	private JPackAttachment uploadVideoPackFromExternalLink(String topicId,
			String packId, String userId, String title, String description,
			String attachmentUrl, String attachmentThumbnailUrl,
			String oAuthToken) throws ClientProtocolException, IOException,
			ParseException, PackPackException {
		JRssFeed feed = new JRssFeed();
		feed.setOgTitle(title);
		feed.setOgDescription(description);
		feed.setOgUrl(attachmentUrl);
		feed.setOgImage(attachmentThumbnailUrl);
		String json = JSONUtil.serialize(feed);
		String url = getBaseUrl() + ATTACHMENT + "video/topic/" + topicId
				+ "/pack/" + packId + "/usr/" + userId;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPut PUT = new HttpPut(url);
		HttpEntity jsonBody = new StringEntity(json, UTF_8);
		PUT.setEntity(GZipUtil.compress(jsonBody));
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		PUT.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		PUT.addHeader(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);
		HttpResponse response = client.execute(PUT);
		return JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JPackAttachment.class);
	}

	private JStatus uploadPack(Map<String, Object> params, String url,
			String oAuthToken, MultipartRequestProgressListener listener)
			throws ClientProtocolException, IOException, ParseException,
			PackPackException {
		DefaultHttpClient client = new DefaultHttpClient();// new
															// DefaultHttpClient();
		HttpPut PUT = new HttpPut(url);
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
				Object obj = params.get(key);
				if(obj instanceof File) {
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
				} else if(obj instanceof ContentBody) {
					ContentBody contentBody = (ContentBody)obj;
					multipartEntity.addPart(key, contentBody);
				}
				/*
				 * builder.addBinaryBody(key, file, HTTP.OCTET_STREAM_TYPE,
				 * file.getName());
				 */
			} else {
				String text = (String) params.get(key);
				//GZi stream = new ByteArrayOutputStream();
				
				StringBody textBody = new StringBody(text);//, TEXT_PLAIN, Charset.forName(UTF_8));
				multipartEntity.addPart(key, textBody);
				// builder.addTextBody(key, text);
			}
		}
		PUT.setEntity(GZipUtil.compress(multipartEntity));
		// HttpEntity entity = builder.build();
		// POST.setEntity(entity);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		PUT.addHeader(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);
		/*
		 * POST.addHeader(CONTENT_TYPE_HEADER,
		 * ContentType.MULTIPART_FORM_DATA.getMimeType());
		 */
		/*
		 * POST.addHeader(CONTENT_TYPE_HEADER, "multipart/form-data");
		 */
		HttpResponse response = client.execute(PUT);
		return JSONUtil.deserialize(EntityUtils.toString(GZipUtil.decompress(response.getEntity())),
				JStatus.class);
	}

	private JPackAttachment addImageToPack(Map<String, Object> params, String oAuthToken,
			MultipartRequestProgressListener listener)
			throws ClientProtocolException, IOException, ParseException,
			PackPackException {
		String topicId = (String) params.get(APIConstants.Topic.ID);
		String packId = (String) params.get(APIConstants.Pack.ID);
		String userId = (String) params.get(APIConstants.User.ID);
		String url = getBaseUrl() + ATTACHMENT + "image/topic/" + topicId
				+ "/pack/" + packId + "/usr/" + userId;
		return editPack(params, url, oAuthToken, listener);
	}

	private JPackAttachment addVideoToPack(Map<String, Object> params, String oAuthToken,
			MultipartRequestProgressListener listener)
			throws ClientProtocolException, IOException, ParseException,
			PackPackException {
		String topicId = (String) params.get(APIConstants.Topic.ID);
		String packId = (String) params.get(APIConstants.Pack.ID);
		String userId = (String) params.get(APIConstants.User.ID);
		String url = getBaseUrl() + ATTACHMENT + "video/topic/" + topicId
				+ "/pack/" + packId + "/usr/" + userId;
		return editPack(params, url, oAuthToken, listener);
	}

	private JPackAttachment editPack(Map<String, Object> params, String url,
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
				Object obj = params.get(key);
				if(obj instanceof File) {
					File file = (File) params.get(key);
					FileBody fileBody = new FileBody(file, file.getName(),
							HTTP.OCTET_STREAM_TYPE, null);
					multipartEntity.addPart(key, fileBody);
				} else if(obj instanceof ByteBody) {
					ByteBody byteBody = (ByteBody)obj;
					byte[] bytes = byteBody.getBytes();
					ByteArrayBody byteArrayBody = new ByteArrayBody(bytes, 
							UUID.randomUUID().toString());
					multipartEntity.addPart(key, byteArrayBody);
				} else if(obj instanceof ContentBody) {
					ContentBody contentBody = (ContentBody)obj;
					multipartEntity.addPart(key, contentBody);
				}
				/*
				 * builder.addBinaryBody(key, file,
				 * ContentType.APPLICATION_OCTET_STREAM, file.getName());
				 */
			} else if (APIConstants.Attachment.IS_COMPRESSED.equals(key)) { 
				Boolean isCompressed = (Boolean) params.get(key);
				if(isCompressed == null) {
					isCompressed = true;
				}
				multipartEntity.addPart(key, new StringBody(String.valueOf(isCompressed)));
			} else {
				String text = (String) params.get(key);
				StringBody textBody = new StringBody(text, TEXT_PLAIN, Charset.forName(UTF_8));
				multipartEntity.addPart(key, textBody);
				// builder.addTextBody(key, text);
			}
		}
		PUT.setEntity(GZipUtil.compress(multipartEntity));
		// HttpEntity entity = builder.build();
		// PUT.setEntity(entity);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		PUT.addHeader(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);
		/*
		 * PUT.addHeader(CONTENT_TYPE_HEADER, "multipart/form-data");
		 */
		/*
		 * PUT.addHeader(CONTENT_TYPE_HEADER,
		 * ContentType.MULTIPART_FORM_DATA.getMimeType());
		 */
		HttpResponse response = client.execute(PUT);
		return JSONUtil.deserialize(EntityUtils.toString(GZipUtil.decompress(response.getEntity())),
				JPackAttachment.class);
	}
	
	private PromoteStatus promotePackAttachment(String packAttachmentId, String userId,
			String oAuthToken) throws Exception {
		String url = getBaseUrl() + "promote/usr/" + userId;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		PUT.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		EntityPromoteDTO dto = new EntityPromoteDTO();
		dto.setId(packAttachmentId);
		dto.setType(JPackAttachment.class.getName());
		String json = JSONUtil.serialize(dto);
		HttpEntity jsonBody = new StringEntity(json, UTF_8);
		PUT.setEntity(jsonBody);
		HttpResponse response = client.execute(PUT);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				PromoteStatus.class);
	}
	
	private JStatus deleteAttachment(String packAttachmentId, String packId,
			String topicId, String oAuthToken) throws Exception {
		String url = getBaseUrl() + ATTACHMENT + packAttachmentId + "/pack/"
				+ packId + "/topic/" + topicId;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpDelete DELETE = new HttpDelete(url);
		DELETE.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(DELETE);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JStatus.class);
	}
	
	private String addStoryToAttachment(String packAttachmentId, String story,
			String oAuthToken) throws Exception {
		String url = getBaseUrl() + ATTACHMENT + packAttachmentId + "/story";
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		PUT.addHeader(CONTENT_TYPE_HEADER, "text/html" + UTF_8_CHARSET);
		
		PUT.addHeader(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);
		
		HttpEntity stringBody = new StringEntity(story, UTF_8);
		
		//PUT.setEntity(GZipUtil.compress(stringBody));
		PUT.setEntity(stringBody);
		
		PUT.setEntity(stringBody);
		HttpResponse response = client.execute(PUT);
		JAttachmentStoryID storyId = JSONUtil.deserialize(
				EntityUtils.toString(response.getEntity()),
				JAttachmentStoryID.class);
		return storyId.getStoryId();
	}

	private String loadStoryForAttachment(String packAttachmentId,
			String userId, String oAuthToken) throws Exception {
		String url = getBaseUrl() + ATTACHMENT + packAttachmentId + "/story"
				+ "/user/" + userId;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);

		// return EntityUtils.toString(response.getEntity());
		if (response.getStatusLine().getStatusCode() == 200) {
			/*return EntityUtils.toString(GZipUtil.decompress(response
					.getEntity()));*/
			return EntityUtils.toString(response.getEntity());
		} else {
			return null;
		}
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
			} else if(COMMAND.ADD_VIDEO_TO_PACK_EXTERNAL_LINK.equals(action)) {
				String topicId = (String) params.get(APIConstants.Topic.ID);
				String packId = (String) params.get(APIConstants.Pack.ID);
				String userId = (String) params.get(APIConstants.User.ID);

				String title = (String) params
						.get(APIConstants.Attachment.TITLE);
				String description = (String) params
						.get(APIConstants.Attachment.DESCRIPTION);
				String attachmentUrl = (String) params
						.get(APIConstants.Attachment.ATTACHMENT_URL);
				String attachmentThumbnailUrl = (String) params
						.get(APIConstants.Attachment.ATTACHMENT_THUMBNAIL_URL);

				result = uploadVideoPackFromExternalLink(topicId, packId,
						userId, title, description, attachmentUrl,
						attachmentThumbnailUrl, oAuthToken);
			} else if (COMMAND.ADD_VIDEO_TO_PACK.equals(action)) {
				result = addVideoToPack(params, oAuthToken, listener);
			} else if(action == COMMAND.PROMOTE_PACK_ATTACHMENT) {
				String packAttachmentId = (String) params.get(APIConstants.PackAttachment.ID);
				String userId = (String) params.get(APIConstants.User.ID);
				result = promotePackAttachment(packAttachmentId, userId, oAuthToken);
			} else if (action == COMMAND.DELETE_ATTACHMENT) {
				String packAttachmentId = (String) params
						.get(APIConstants.PackAttachment.ID);
				String packId = (String) params.get(APIConstants.Pack.ID);
				String topicId = (String) params.get(APIConstants.Topic.ID);
				result = deleteAttachment(packAttachmentId, packId, topicId,
						oAuthToken);
			} else if (action == COMMAND.ADD_STORY_TO_ATTACHMENT) {
				String packAttachmentId = (String) params
						.get(APIConstants.PackAttachment.ID);
				String story = (String) params
						.get(APIConstants.AttachmentStory.STORY);
				result = addStoryToAttachment(packAttachmentId, story,
						oAuthToken);
			} else if (action == COMMAND.GET_STORY_FROM_ATTACHMENT) {
				String packAttachmentId = (String) params
						.get(APIConstants.PackAttachment.ID);
				String userId = (String) params.get(APIConstants.User.ID);
				result = loadStoryForAttachment(packAttachmentId, userId,
						oAuthToken);
			}
			return result;
		}
	}
}