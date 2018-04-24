package com.squill.og.crawler.newsapi.reader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.og.crawler.app.SystemPropertyKeys;
import com.squill.og.crawler.hooks.IApiRequestExecutor;
import com.squill.og.crawler.internal.utils.JSONUtil;
import com.squill.og.crawler.model.web.JRssFeeds;
import com.squill.services.exception.OgCrawlException;

@Component("newsApiRequestExecutor")
@Scope("prototype")
public class NewsApiRequestExecutor implements IApiRequestExecutor {

	private String newsAPIKey;
	private List<NewsSource> newsSources;

	private static final Logger LOG = LoggerFactory
			.getLogger(NewsApiRequestExecutor.class);

	@PostConstruct
	private void init() {
		try {
			String configFileRelativePath = System
					.getProperty(SystemPropertyKeys.NEWS_API_ORG_SOURCES_FILE);
			String baseDirPath = System
					.getProperty(SystemPropertyKeys.WEB_CRAWLERS_BASE_DIR);
			if (!baseDirPath.endsWith(File.separator)
					&& !baseDirPath.endsWith("\\")
					&& !baseDirPath.endsWith("/")) {
				baseDirPath = baseDirPath + File.separator;
			}
			String configFilePath = baseDirPath + configFileRelativePath;
			File configFile = new File(configFilePath);
			Properties properties = new Properties();
			properties.load(new FileReader(configFile));
			this.newsAPIKey = properties
					.getProperty(SystemPropertyKeys.NEWS_API_KEY);
			String newsApiSourceRelativePath = properties
					.getProperty(SystemPropertyKeys.NEWS_SOURCES);

			this.newsSources = readNewsSources(configFile.getParent() + File.separator
					+ newsApiSourceRelativePath);
		} catch (Exception e) {
			LOG.error(
					"Failed to load news-sources configuration for newsapi.org :: "
							+ e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	private List<NewsSource> readNewsSources(String news_sources_file_name)
			throws Exception {
		String content = new String(Files.readAllBytes(Paths
				.get(news_sources_file_name)), Charset.forName("UTF-8"));
		NewsSources newsSources = JSONUtil.deserialize(content,
				NewsSources.class);
		return newsSources.getSources();
	}

	@Override
	public JRssFeeds execute(String webApiUniqueID) {
		JRssFeeds result = new JRssFeeds();
		try {
			LOG.info("Reading News from Sources");
			if (newsSources == null) {
				return result;
			}

			//List<NewsFeedGroup> newsFeedGroups = new LinkedList<NewsFeedGroup>();
			for (NewsSource newsSource : newsSources) {
				try {
					//NewsFeedGroup newsFeedGroup = new NewsFeedGroup();
					List<NewsFeed> newsFeedsList = new LinkedList<NewsFeed>();
					//String feedType = JRssFeedType.NEWS.name();
//					if (newsSource.getFeedType() != null) {
//						feedType = newsSource.getFeedType().toUpperCase();
//					}
					NewsFeeds newsFeeds = readFromSource(newsSource.getId());
					if (newsFeeds == null)
						continue;
					newsFeedsList.addAll(newsFeeds.getArticles());

					//newsFeedGroup.setFeedType(feedType);
					//newsFeedGroup.getNewsFeeds().addAll(newsFeedsList);

					JRssFeeds r = NewsFeedConverter.convert(newsFeeds);
					if(r != null) {
						result.getFeeds().addAll(r.getFeeds());
					}
					//newsFeedGroups.add(newsFeedGroup);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
			
			// uploadNewsFeeds(newsFeedGroups);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	private NewsFeeds readFromSource(String newsSource)
			throws ClientProtocolException, IOException, OgCrawlException {
		HttpClient client = new DefaultHttpClient();
		String get_URL = "https://newsapi.org/v1/articles?source=" + newsSource
				+ "&apiKey=" + newsAPIKey;
		HttpGet GET = new HttpGet(get_URL);
		LOG.debug(get_URL);
		HttpResponse response = client.execute(GET);
		if (response.getStatusLine().getStatusCode() == 200) {
			String json = EntityUtils.toString(response.getEntity());
			LOG.debug(json);
			return JSONUtil.deserialize(json, NewsFeeds.class);
		}
		return null;
	}

//	private class NewsFeedGroup {
//
//		private List<NewsFeed> newsFeeds;
//
//		public List<NewsFeed> getNewsFeeds() {
//			if (newsFeeds == null) {
//				newsFeeds = new LinkedList<NewsFeed>();
//			}
//			return newsFeeds;
//		}
//	}

}
