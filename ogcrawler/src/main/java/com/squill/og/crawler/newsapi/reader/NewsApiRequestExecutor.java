package com.squill.og.crawler.newsapi.reader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.og.crawler.app.SystemPropertyKeys;
import com.squill.og.crawler.hooks.IApiRequestExecutor;
import com.squill.og.crawler.internal.utils.HttpRequestExecutor;
import com.squill.og.crawler.internal.utils.JSONUtil;
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
	public Map<String, List<JRssFeed>> execute(String webApiUniqueID) {
		Map<String, List<JRssFeed>> result = new HashMap<String, List<JRssFeed>>();
		try {
			LOG.info("Reading News from Sources");
			if (newsSources == null) {
				return result;
			}

			for (NewsSource newsSource : newsSources) {
				try {
					List<NewsFeed> newsFeedsList = new LinkedList<NewsFeed>();
					NewsFeeds newsFeeds = readFromSource(newsSource.getId());
					if (newsFeeds == null)
						continue;
					newsFeedsList.addAll(newsFeeds.getArticles());

					JRssFeeds r = NewsFeedConverter.convert(newsFeeds);
					if(r != null) {
						result.put(newsSource.getId(), r.getFeeds());
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

	private NewsFeeds readFromSource(String newsSource)
			throws ClientProtocolException, IOException, OgCrawlException {
		//HttpClient client = new DefaultHttpClient();
		String get_URL = "https://newsapi.org/v1/articles?source=" + newsSource
				+ "&apiKey=" + newsAPIKey;
		HttpGet GET = new HttpGet(get_URL);
		LOG.debug(get_URL);
		//HttpResponse response = client.execute(GET);
		HttpResponse response = new HttpRequestExecutor().GET(GET);
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
