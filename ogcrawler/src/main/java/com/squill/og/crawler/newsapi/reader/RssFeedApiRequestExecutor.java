package com.squill.og.crawler.newsapi.reader;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.og.crawler.app.SystemPropertyKeys;
import com.squill.og.crawler.external.feed.ExtFeed;
import com.squill.og.crawler.external.feed.ExtFeedEntry;
import com.squill.og.crawler.external.feed.ExternalRssFeedParser;
import com.squill.og.crawler.hooks.IApiRequestExecutor;
import com.squill.og.crawler.internal.utils.WebFeedSpiderUtil;
import com.squill.utils.DateTimeUtil;
import com.squill.utils.JSONUtil;

/**
 * 
 * @author Saurav
 *
 */
@Component("rssFeedApiRequestExecutor")
@Scope("prototype")
public class RssFeedApiRequestExecutor implements IApiRequestExecutor {
	
	private List<RssNewsSource> rssNewsSources;
	
	private DefaultFeedTypeResolver typeResolver;
	
	private static final String RSS_FEED_PROVIDERS_CONFIG = "rss-feed-providers.cfg";

	private static final Logger LOG = LoggerFactory
			.getLogger(RssFeedApiRequestExecutor.class);
	
	@PostConstruct
	private void init() {
		try {
			String baseDirPath = System
					.getProperty(SystemPropertyKeys.WEB_CRAWLERS_CONFIG_DIR);
			if (!baseDirPath.endsWith(File.separator)
					&& !baseDirPath.endsWith("\\")
					&& !baseDirPath.endsWith("/")) {
				baseDirPath = baseDirPath + File.separator;
			}
			String configFilePath = baseDirPath + RSS_FEED_PROVIDERS_CONFIG;

			this.rssNewsSources = readRssNewsSources(configFilePath);
			typeResolver = new DefaultFeedTypeResolver(this.rssNewsSources);
		} catch (Exception e) {
			LOG.error(
					"Failed to load news-sources configuration for RSS feed fetch :: "
							+ e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, List<JRssFeed>> execute(String webApiUniqueID) {
		Map<String, List<JRssFeed>> result = new HashMap<String, List<JRssFeed>>();
		try {
			LOG.info("Reading News from Sources");
			if (rssNewsSources == null) {
				return result;
			}

			for (RssNewsSource rssNewsSource : rssNewsSources) {
				try {
					if(!rssNewsSource.isActive()) {
						continue;
					}
					LOG.info("Reading News from Source = " + rssNewsSource.getRssFeedUrl());
					ExtFeed extFeed = new ExternalRssFeedParser(
							rssNewsSource.getRssFeedUrl()).parse();
					List<ExtFeedEntry> entries = extFeed.getEntries();
					Iterator<ExtFeedEntry> itr = entries.iterator();
					while(itr.hasNext()) {
						ExtFeedEntry entry = itr.next();
						long dt = DateTimeUtil.parse(entry.getPubDate());
						if(dt <= 0) {
							LOG.error("Failed to parser DateTime = " + entry.getPubDate());
							itr.remove();
						} else {
							entry.setDateTime(dt);
						}
					}
					Collections.sort(entries, new Comparator<ExtFeedEntry>() {
						@Override
						public int compare(ExtFeedEntry o1, ExtFeedEntry o2) {
							if(o1.getDateTime() == o2.getDateTime()) {
								return 0;
							} else if(o1.getDateTime() > o2.getDateTime()) {
								return -1;
							}
							return 1;
						}
					});
					
					List<ExtFeedEntry> newEntries = new ArrayList<ExtFeedEntry>();
					int len = entries.size();
					if(len > 3) {
						len = 3;
					}
					for(int i=0; i<len; i++) {
						newEntries.add(entries.get(i));
					}
					
					LOG.info("Total feeds = " + newEntries.size());
					List<JRssFeed> feeds = WebFeedSpiderUtil.converAll(
							newEntries, rssNewsSource.getBaseUrl());
					if(feeds != null && !feeds.isEmpty()) {
						JRssFeeds c = new JRssFeeds();
						c.getFeeds().addAll(feeds);
						LOG.info(JSONUtil.serialize(c));
						for(JRssFeed feed : feeds) {
							feed.setFeedType(typeResolver.resolveDefaultFeedType(feed, rssNewsSource.getFeedType()).name());
						}
						result.put(rssNewsSource.getId(), feeds);
					}
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return result;
	}

	private List<RssNewsSource> readRssNewsSources(String news_sources_file_name)
			throws Exception {
		String content = new String(Files.readAllBytes(Paths
				.get(news_sources_file_name)), Charset.forName("UTF-8"));
		RssNewsSources newsSources = JSONUtil.deserialize(content,
				RssNewsSources.class);
		return newsSources.getSources();
	}
}
