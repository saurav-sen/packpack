package com.squill.og.crawler.newsapi.reader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;

class DefaultFeedTypeResolver {

	private Map<String, JRssFeedType> defaultFeedTypeMap = new HashMap<String, JRssFeedType>();

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultFeedTypeResolver.class);

	public DefaultFeedTypeResolver(List<? extends NewsSource> newsSources) {
		for (NewsSource newsSource : newsSources) {
			List<String> orgHomePages = newsSource.getOrgHomePages();
			for(String orgHomePage : orgHomePages) {
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
	
	JRssFeedType resolveDefaultFeedType(JRssFeed article, String defaultName) {
		JRssFeedType type = null;
		try {
			URL url = new URL(article.getOgUrl());
			String key = url.getProtocol() + "://" + url.getHost() + "/";
			type = defaultFeedTypeMap.get(key);
		} catch (MalformedURLException e) {
			LOG.error(article.getOgUrl());
			LOG.error(e.getMessage(), e);
		}
		if(type != null) {
			return type;
		} else if(defaultName != null) {
			try {
				type = JRssFeedType.valueOf(defaultName.trim().toUpperCase());
			} catch (Exception e) {
				// ignore
			}
		}
		return type != null ? type : JRssFeedType.NEWS;
	}
}
