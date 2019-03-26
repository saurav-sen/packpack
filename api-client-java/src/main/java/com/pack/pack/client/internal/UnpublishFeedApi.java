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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.model.web.dto.BookmarkDTO;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;
import com.squill.feed.web.model.JRssFeedUploadRequest;
import com.squill.feed.web.model.JRssFeeds;

/**
 * 
 * @author Saurav
 *
 */
public class UnpublishFeedApi extends BaseAPI {

	private Invoker invoker = new Invoker();

	protected UnpublishFeedApi(String baseUrl) {
		super(baseUrl);
	}

	@Override
	protected ApiInvoker getInvoker() {
		return invoker;
	}

	@SuppressWarnings("unchecked")
	private Pagination<JRssFeed> getAllUnpublishedFeeds(String deviceId,
			int pageNo) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "publish/unprovision/page/" + pageNo;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, deviceId);
		HttpResponse response = client.execute(GET);
		Pagination<JRssFeed> page = JSONUtil.deserialize(
				EntityUtils.toString(response.getEntity()), Pagination.class);
		if (page == null)
			return page;
		List<JRssFeed> feeds = page.getResult();
		String json = "{\"feeds\": " + JSONUtil.serialize(feeds, false) + "}";
		JRssFeeds container = JSONUtil.deserialize(json, JRssFeeds.class);
		feeds = container.getFeeds();
		page.setResult(feeds);
		return page;
	}

	private JStatus uploadUnpublishedFeed(String deviceId, JRssFeed content,
			String feedType, boolean isOpenDirectLink, boolean isNotify,
			boolean isCheckDuplicate) throws Exception {
		JRssFeedUploadRequest req = new JRssFeedUploadRequest();
		req.setContent(content);
		req.setFeedType(feedType);
		req.setCheckDuplicate(isCheckDuplicate);
		req.setNotify(isNotify);
		req.setOpenDirectLink(isOpenDirectLink);
		String json = JSONUtil.serialize(req);
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "publish/type/1";
		HttpPost POST = new HttpPost(url);
		POST.addHeader(AUTHORIZATION_HEADER, deviceId);
		POST.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		HttpEntity jsonBody = new StringEntity(json, UTF_8);
		POST.setEntity(jsonBody);
		HttpResponse response = client.execute(POST);
		if (response.getStatusLine().getStatusCode() == 200) {
			String respBody = EntityUtils.toString(response.getEntity());
			return JSONUtil.deserialize(respBody, JStatus.class);
		}
		JStatus status = new JStatus();
		status.setStatus(StatusType.ERROR);
		status.setInfo("HTTP " + response.getStatusLine().getStatusCode());
		return status;
	}

	private JRssFeed processUnpublishedLink(String webLink, String deviceId)
			throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "bookmark";
		HttpPost POST = new HttpPost(url);
		POST.addHeader(AUTHORIZATION_HEADER, deviceId);
		POST.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		BookmarkDTO dto = new BookmarkDTO();
		dto.setHyperlink(webLink);
		dto.setUserName(deviceId);
		String json = JSONUtil.serialize(dto);
		HttpEntity jsonBody = new StringEntity(json, UTF_8);
		POST.setEntity(jsonBody);
		HttpResponse response = client.execute(POST);
		String respBody = EntityUtils.toString(response.getEntity());
		return JSONUtil.deserialize(respBody, JRssFeed.class);
	}

	private class Invoker implements ApiInvoker {

		private COMMAND action;

		private Map<String, Object> params;

		@Override
		public void setConfiguration(Configuration configuration) {
			action = configuration.getAction();
			params = configuration.getApiParams();
		}

		@Override
		public Object invoke() throws Exception {
			if (COMMAND.GET_ALL_UNPUBLISHED_FEEDS.equals(action)) {
				String deviceId = (String) params
						.get(APIConstants.Device.DEVICE_ID);
				Object pageNoString = params.get(APIConstants.PageInfo.PAGE_NO);
				if (pageNoString == null) {
					pageNoString = 0;
				}
				int pageNo = (int) pageNoString;
				return getAllUnpublishedFeeds(deviceId, pageNo);
			} else if (COMMAND.PROCESS_UNPUBLISHED_LINK.equals(action)) {
				String webLink = (String) params
						.get(APIConstants.Bookmark.WEB_LINK);
				String deviceId = (String) params
						.get(APIConstants.Device.DEVICE_ID);
				return processUnpublishedLink(webLink, deviceId);
			} else if (COMMAND.UPLOAD_UNPUBLISHED_FEED.equals(action)) {
				Object object = params.get(APIConstants.Device.DEVICE_ID);
				if (object == null) {
					throw new RuntimeException("No Device ID specified");
				}
				String deviceId = (String) object;
				object = params
						.get(APIConstants.UnpublishedFeedPublishInfo.CONTENT);
				if (object == null) {
					throw new RuntimeException("No content specified");
				}
				String feedType = JRssFeedType.NEWS.name();
				object = params
						.get(APIConstants.UnpublishedFeedPublishInfo.FEED_TYPE);
				if (object != null) {
					String str = ((String) object).toUpperCase();
					try {
						feedType = JRssFeedType.valueOf(str).name();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				JRssFeed content = (JRssFeed) object;
				object = params
						.get(APIConstants.UnpublishedFeedPublishInfo.IS_CHECK_DUPLICATE);
				boolean isCheckDuplicate = false;
				if (object != null) {
					isCheckDuplicate = (boolean) object;
				}
				object = params
						.get(APIConstants.UnpublishedFeedPublishInfo.IS_NOTIFY);
				boolean isNotify = false;
				if (object != null) {
					isNotify = (boolean) object;
				}
				object = params
						.get(APIConstants.UnpublishedFeedPublishInfo.IS_OPEN_DIRECT_LINK);
				boolean isOpenDirectLink = false;
				if (object != null) {
					isOpenDirectLink = (boolean) object;
				}
				return uploadUnpublishedFeed(deviceId, content, feedType,
						isOpenDirectLink, isNotify, isCheckDuplicate);
			}
			return null;
		}

		@Override
		public Object invoke(MultipartRequestProgressListener listener)
				throws Exception {
			throw new UnsupportedOperationException(
					"Progress track is not supported by UnpublishFeed's API");
		}
	}
}
