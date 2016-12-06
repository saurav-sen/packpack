package com.squill.og.crawler.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JRssFeeds;
import com.pack.pack.oauth.OAuthConstants;
import com.squill.og.crawler.IFeedUploader;

/**
 * 
 * @author Saurav
 *
 */
@Component("defaultOgFeedUploader")
@Scope("prototype")
public class DefaultOgFeedUploader implements IFeedUploader {
	
	private Map<String, String> configuration = new HashMap<String, String>(5);
	
	private static final String BASE_URL_CONFIG = "BASE_URL";
	private static final String URL_PART_CONFIG = "URL_PART";
	private static final String API_KEY_CONFIG = "API_KEY";

	@Override
	public void uploadBulk(JRssFeeds rssFeeds) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String baseUrl = configuration.get(BASE_URL_CONFIG);
		baseUrl = baseUrl + configuration.get(URL_PART_CONFIG);
		HttpPut PUT = new HttpPut(baseUrl);
		PUT.addHeader(OAuthConstants.AUTHORIZATION_HEADER,
				configuration.get(API_KEY_CONFIG));
		// String baseUrl = System.getProperty("base.url");
		// baseUrl = baseUrl + "/home/bulk_upload";
		// HttpPut PUT = new HttpPut(baseUrl);
		// PUT.addHeader(OAuthConstants.AUTHORIZATION_HEADER,
		// OAuthConstants.RSS_FEED_UPLOAD_API_KEY);
		String json = JSONUtil.serialize(rssFeeds);
		PUT.setEntity(new StringEntity(json));
		HttpResponse response = client.execute(PUT);
	}

	@Override
	public void addConfig(String key, String value) {
		configuration.put(key, value);
	}
}