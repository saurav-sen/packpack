package com.pack.pack.client.internal;

import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.Pagination;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;
import com.squill.feed.web.model.JRssFeeds;

/**
 * 
 * @author Saurav
 *
 */
class RefreshmentFeedsApi extends BaseAPI {

	RefreshmentFeedsApi(String baseUrl) {
		super(baseUrl);
	}

	private Invoker invoker = new Invoker();

	@Override
	protected ApiInvoker getInvoker() {
		return invoker;
	}

	@SuppressWarnings("unchecked")
	private Pagination<JRssFeed> getAllFeeds(String userId, int pageNo,
			String userName, String source) throws Exception {
		String url = getBaseUrl() + "refreshment/usr/" + userId + "/page/"
				+ String.valueOf(pageNo) + "?source=" + source;
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(AUTHORIZATION_HEADER, userName);
		Pagination<JRssFeed> page = JSONUtil.deserialize(
				new HttpRequestExecutor().GET(url, headers, true),
				Pagination.class);
		if (page == null)
			return page;
		List<JRssFeed> feeds = page.getResult();
		String json = "{\"feeds\": " + JSONUtil.serialize(feeds, false) + "}";
		JRssFeeds container = JSONUtil.deserialize(json, JRssFeeds.class);
		feeds = container.getFeeds();
		page.setResult(feeds);
		return page;
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
			if (COMMAND.GET_ALL_REFRESHMENT_FEEDS.equals(action)) {
				String userId = (String) params.get(APIConstants.User.ID);
				String source = JRssFeedType.REFRESHMENT.name();
				Object pageNoString = params.get(APIConstants.PageInfo.PAGE_NO);
				if (pageNoString == null) {
					pageNoString = 0;
				}
				int pageNo = (int) pageNoString;
				return getAllFeeds(userId, pageNo, userName, source);
			}
			return null;
		}

		@Override
		public Object invoke(MultipartRequestProgressListener listener)
				throws Exception {
			throw new UnsupportedOperationException(
					"Progress track is not supported by Home API");
		}
	}
}