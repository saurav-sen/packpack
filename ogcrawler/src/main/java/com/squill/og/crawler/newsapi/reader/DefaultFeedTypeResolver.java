package com.squill.og.crawler.newsapi.reader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.feed.web.model.JRssFeedType;

class DefaultFeedTypeResolver {

	private Map<String, JRssFeedType> defaultFeedTypeMap = new HashMap<String, JRssFeedType>();

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultFeedTypeResolver.class);

	public DefaultFeedTypeResolver(List<NewsSource> newsSources) {
		for (NewsSource newsSource : newsSources) {
			String orgHomePage = newsSource.getOrgHomePage();
			if (!orgHomePage.endsWith("/")) {
				orgHomePage = orgHomePage + "/";
			}
			try {
				JRssFeedType type = JRssFeedType.valueOf(newsSource
						.getFeedType().toUpperCase());
				defaultFeedTypeMap.put(orgHomePage, type);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	JRssFeedType resolveDefaultFeedType(NewsFeed article) {
		JRssFeedType type = null;
		try {
			URL url = new URL(article.getUrl());
			String key = url.getProtocol() + "://" + url.getHost() + "/";
			type = defaultFeedTypeMap.get(key);
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		}
		return type != null ? type : JRssFeedType.NEWS;
	}
}
