package com.pack.pack.client.internal;

import static com.pack.pack.client.api.APIConstants.APPLICATION_JSON;
import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;
import static com.pack.pack.client.api.APIConstants.CONTENT_TYPE_HEADER;

import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.dto.BookmarkDTO;
import com.squill.feed.web.model.JRssFeed;

class FeedReaderApi extends BaseAPI {
	
	private Invoker invoker = new Invoker();
	
	FeedReaderApi(String baseUrl) {
		super(baseUrl);
	}

	@Override
	protected ApiInvoker getInvoker() {
		return invoker;
	}
	
	private JRssFeed doCrawlExternalPublicLink(String externalPublicLink) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpGet GET = new HttpGet(externalPublicLink);
		HttpResponse response = client.execute(GET);
		int statusCode = response.getStatusLine().getStatusCode();
		while(statusCode == 302) {
			Header header = response.getFirstHeader("location");
			String url = header.getValue();
			GET = new HttpGet(url);
			response = client.execute(GET);
			statusCode = response.getStatusLine().getStatusCode();
		}
		if(statusCode == 200) {
			String htmlContent = EntityUtils.toString(response.getEntity());
			Document doc = Jsoup.parse(htmlContent);
			
			String title = null;
			Elements metaOgTitle = doc.select("meta[property=og:title]");
			if (metaOgTitle != null) {
				title = metaOgTitle.attr("content");
			}

			String description = null;
			Elements metaOgDescription = doc.select("meta[property=og:description]");
			if (metaOgDescription != null) {
				description = metaOgDescription.attr("content");
			}

			String imageUrl = null;
			Elements metaOgImage = doc.select("meta[property=og:image]");
			if (metaOgImage != null) {
				imageUrl = metaOgImage.attr("content");
			}

			String resourceUrl = null;
			/*Elements metaOgUrl = doc.select("meta[property=al:android:url]");
			if (metaOgUrl != null) {
				resourceUrl = metaOgUrl.attr("content");
			}*/
			Elements metaOgUrl = doc.select("meta[property=og:url]");
			if (metaOgUrl != null) {
				resourceUrl = metaOgUrl.attr("content");
			}
			
			JRssFeed feed = new JRssFeed();
			feed.setOgTitle(title);
			feed.setOgDescription(description);
			feed.setOgImage(imageUrl);
			feed.setOgUrl(resourceUrl);
			return feed;
		}
		return null;
	}
	
	private JRssFeed processBookmark(String webLink, String userName)
			throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "bookmark";
		HttpPost POST = new HttpPost(url);
		POST.addHeader(AUTHORIZATION_HEADER, userName);
		POST.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		BookmarkDTO dto = new BookmarkDTO();
		dto.setHyperlink(webLink);
		dto.setUserName(userName);
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
			if (COMMAND.CRAWL_FEED.equals(action)) {
				String externalPublicLink = (String) params.get(APIConstants.ExternalResource.RESOURCE_URL);
				return doCrawlExternalPublicLink(externalPublicLink);
			} else if(COMMAND.PROCESS_BOOKMARK.equals(action)) {
				String webLink = (String) params.get(APIConstants.Bookmark.WEB_LINK);
				String userName = (String) params.get(APIConstants.User.USERNAME);
				return processBookmark(webLink, userName);
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