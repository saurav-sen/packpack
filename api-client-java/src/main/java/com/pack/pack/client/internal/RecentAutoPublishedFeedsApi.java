package com.pack.pack.client.internal;

import static com.pack.pack.client.api.APIConstants.APPLICATION_JSON;
import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;
import static com.pack.pack.client.api.APIConstants.CONTENT_TYPE_HEADER;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
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
import com.pack.pack.model.web.StatusType;
import com.pack.pack.model.web.dto.FeedPublish;
import com.squill.feed.web.model.JRssFeeds;

/**
 * 
 * @author Saurav
 *
 */
public class RecentAutoPublishedFeedsApi extends BaseAPI {

	private Invoker invoker = new Invoker();

	protected RecentAutoPublishedFeedsApi(String baseUrl) {
		super(baseUrl);
	}

	@Override
	protected ApiInvoker getInvoker() {
		return invoker;
	}

	private JRssFeeds getAllRecentlyAutoUploadedFeeds(String deviceId)
			throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "publish/recent/";
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, deviceId);
		HttpResponse response = client.execute(GET);
		JRssFeeds container = JSONUtil.deserialize(
				EntityUtils.toString(response.getEntity()), JRssFeeds.class);
		return container;
	}

	private JStatus editAutoPublishedFeed(String deviceId, String feedId,
			String title, String summary, boolean isOpenDirectLink,
			boolean isNotify, boolean useExternalSummaryAlgo) throws Exception {
		FeedPublish req = new FeedPublish();
		req.setId(feedId);
		req.setNotify(isNotify);
		req.setOpenDirectLink(isOpenDirectLink);
		req.setSummaryText(summary);
		req.setTitleText(title);
		req.setUseExternalSummaryAlgo(useExternalSummaryAlgo);
		String json = JSONUtil.serialize(req);
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "publish/type/0";
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

	private JStatus deleteRecentAutoPublishedFeed(String deviceId, String feedId)
			throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "publish/id/" + feedId;
		HttpDelete DELETE = new HttpDelete(url);
		DELETE.addHeader(AUTHORIZATION_HEADER, deviceId);
		HttpResponse response = client.execute(DELETE);
		if (response.getStatusLine().getStatusCode() == 200) {
			String respBody = EntityUtils.toString(response.getEntity());
			return JSONUtil.deserialize(respBody, JStatus.class);
		}
		JStatus status = new JStatus();
		status.setStatus(StatusType.ERROR);
		status.setInfo("HTTP " + response.getStatusLine().getStatusCode());
		return status;
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
			if (COMMAND.GET_ALL_RECENT_NEWS_FEEDS.equals(action)) {
				String deviceId = (String) params
						.get(APIConstants.Device.DEVICE_ID);
				return getAllRecentlyAutoUploadedFeeds(deviceId);
			} else if (COMMAND.EDIT_RECENT_NEWS_FEED.equals(action)) {
				String deviceId = (String) params
						.get(APIConstants.Device.DEVICE_ID);
				String feedId = (String) params
						.get(APIConstants.FeedPublishInfo.FEED_ID);
				Object object = params
						.get(APIConstants.FeedPublishInfo.IS_NOTIFY);
				boolean isNotify = false;
				if (object != null) {
					isNotify = (boolean) object;
				}
				object = params
						.get(APIConstants.FeedPublishInfo.IS_OPEN_DIRECT_LINK);
				boolean isOpenDirectLink = false;
				if (object != null) {
					isOpenDirectLink = (boolean) object;
				}
				String titleText = (String) params
						.get(APIConstants.FeedPublishInfo.TITLE_TEXT);
				String summaryText = (String) params
						.get(APIConstants.FeedPublishInfo.SUMMARY_TEXT);
				object = params
						.get(APIConstants.FeedPublishInfo.USE_EXTERNAL_SUMMARY_ALGO);
				boolean useExternalAlgo = false;
				if (object != null) {
					useExternalAlgo = (boolean) object;
				}
				return editAutoPublishedFeed(deviceId, feedId, titleText,
						summaryText, isOpenDirectLink, isNotify,
						useExternalAlgo);
			} else if (COMMAND.DELETE_RECENT_FEED.equals(action)) {
				String deviceId = (String) params
						.get(APIConstants.Device.DEVICE_ID);
				String feedId = (String) params
						.get(APIConstants.FeedPublishInfo.FEED_ID);
				return deleteRecentAutoPublishedFeed(deviceId, feedId);

			}
			return null;
		}

		@Override
		public Object invoke(MultipartRequestProgressListener listener)
				throws Exception {
			throw new UnsupportedOperationException(
					"Progress track is not supported by Recently-PublishFeed's API");
		}
	}
}
