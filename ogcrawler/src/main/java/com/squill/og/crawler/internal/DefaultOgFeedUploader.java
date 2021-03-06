package com.squill.og.crawler.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.internal.utils.CoreConstants;
import com.squill.og.crawler.internal.utils.JSONUtil;
import com.squill.og.crawler.model.web.JRssFeeds;

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
	
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	private static final String APPLICATION_JSON = "application/json";

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultOgFeedUploader.class);

	@Override
	public void uploadBulk(JRssFeeds rssFeeds) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = configuration.get(BASE_URL_CONFIG);
		if(url != null && !url.isEmpty() && url.startsWith("${") && url.endsWith("}")) {
			url = url.substring(0, url.length()-1);
			url = url.replaceFirst("\\$\\{", "");
			url = System.getProperty(url);
		}
		url = url + configuration.get(URL_PART_CONFIG);
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(CoreConstants.AUTHORIZATION_HEADER,
				configuration.get(API_KEY_CONFIG));
		PUT.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		String json = JSONUtil.serialize(rssFeeds);
		PUT.setEntity(new StringEntity(json));
		LOG.info("Invoking 'PUT " + url
				+ "' (ML Api) for classification and bulk upload of feeds");
		client.execute(PUT);
	}

	@Override
	public void addConfig(String key, String value) {
		configuration.put(key, value);
	}
}