package com.squill.news.reader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JRssFeedType;
import com.pack.pack.model.web.JRssFeeds;
import com.pack.pack.model.web.TTL;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.services.registry.ServiceRegistryModes;
import com.pack.pack.util.RssFeedUtil;
import com.pack.pack.util.SystemPropertyUtil;

public class Startup {

	private static final String NEWS_SOURCES = "news.sources";
	private static final String NEWS_API_KEY = "news.api.key";

	private String[] args;
	private Options options = new Options();

	private static final Logger LOG = LoggerFactory.getLogger(Startup.class);

	private ScheduledExecutorService scheduler;

	public Startup(String[] args) {
		this.args = args;
		options.addOption("h", "help", false, "Show Help");
		options.addOption("f", "file", true,
				"Configuration File For news reader");

		scheduler = Executors.newSingleThreadScheduledExecutor();
	}

	public static void main(String[] args) {
		final Startup app = new Startup(args);
		try {
			app.startApp();
		} catch (Exception e) {
			LOG.debug(e.getMessage(), e);
		}
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				LOG.info("Shutting Down News Reader...");
				app.stopApp();
			}
		}));
	}

	private void startApp() throws Exception {
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine command = parser.parse(options, args);
			if (command.hasOption("h")) {
				help();
			} else if (command.hasOption("f")) {
				String optionValue = command.getOptionValue("f");
				Properties properties = new Properties();
				properties.load(new FileReader(new File(optionValue)));

				String news_sources_file_name = (String) properties
						.get(NEWS_SOURCES);
				String newsAPIKey = (String) properties
						.getProperty(NEWS_API_KEY);

				List<NewsSource> newsSources = readNewsSources(news_sources_file_name);

				SystemPropertyUtil.init();
				ServiceRegistry.INSTANCE
						.init(ServiceRegistryModes.REDIS_ONLY_SERVICES);
				Runnable cmd = new NewsReader(newsSources, newsAPIKey);
				scheduler.scheduleAtFixedRate(cmd, 0, 2, TimeUnit.HOURS);
			} else {
				help();
			}
		} catch (ParseException e) {
			LOG.trace(e.getMessage(), e);
			help();
		} catch (Exception e) {
			LOG.trace(e.getMessage(), e);
			help();
		} finally {
			// stopApp();
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

	private void stopApp() {
		scheduler.shutdownNow();
		while (!scheduler.isTerminated()) {
			LOG.warn("Waiting for News Reader to shutdown now...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		LOG.info("News Reader Shutdown successfully");
	}

	private void help() {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("java -jar news-reader-1.0.0-<X>.jar", options);
		System.exit(0);
	}

	private class NewsReader implements Runnable {

		private List<NewsSource> newsSources;

		private String newsAPIKey;

		NewsReader(List<NewsSource> newsSources, String newsAPIKey) {
			this.newsSources = newsSources;
			this.newsAPIKey = newsAPIKey;
		}

		@Override
		public void run() {
			try {
				LOG.info("Reading News from Sources");
				if (newsSources == null) {
					return;
				}

				List<Startup.NewsFeedGroup> newsFeedGroups = new LinkedList<Startup.NewsFeedGroup>();
				for (NewsSource newsSource : newsSources) {
					try {
						Startup.NewsFeedGroup newsFeedGroup = new Startup.NewsFeedGroup();
						List<NewsFeed> newsFeedsList = new LinkedList<NewsFeed>();
						String feedType = JRssFeedType.NEWS.name();
						if (newsSource.getFeedType() != null) {
							feedType = newsSource.getFeedType().toUpperCase();
						}
						NewsFeeds newsFeeds = readFromSource(newsSource.getId());
						if (newsFeeds == null)
							continue;
						newsFeedsList.addAll(newsFeeds.getArticles());

						newsFeedGroup.setFeedType(feedType);
						newsFeedGroup.getNewsFeeds().addAll(newsFeedsList);

						newsFeedGroups.add(newsFeedGroup);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				}

				uploadNewsFeeds(newsFeedGroups);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}

		private NewsFeeds readFromSource(String newsSource)
				throws ClientProtocolException, IOException, PackPackException {
			HttpClient client = new DefaultHttpClient();
			String get_URL = "https://newsapi.org/v1/articles?source="
					+ newsSource + "&apiKey=" + newsAPIKey;
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

		private void uploadNewsFeeds(List<Startup.NewsFeedGroup> newsFeedGroups) {
			for (NewsFeedGroup newsFeedGroup : newsFeedGroups) {
				uploadNewsFeeds(newsFeedGroup);
			}
		}

		private void uploadNewsFeeds(Startup.NewsFeedGroup newsFeedGroup) {
			List<NewsFeed> newsFeeds = newsFeedGroup.getNewsFeeds();
			NewsFeeds nfc = new NewsFeeds();
			nfc.getArticles().addAll(newsFeeds);
			JRssFeeds feeds = NewsFeedConverter.convert(nfc,
					newsFeedGroup.getFeedType());
			LOG.info("Uploading news feeds: Total = " + newsFeeds.size());
			TTL ttl = new TTL();
			ttl.setTime((short) 1);
			ttl.setUnit(TimeUnit.DAYS);
			RssFeedUtil.uploadNewFeeds(feeds, ttl, true);
		}
	}

	private class NewsFeedGroup {

		private String feedType;

		private List<NewsFeed> newsFeeds;

		public String getFeedType() {
			return feedType;
		}

		public void setFeedType(String feedType) {
			this.feedType = feedType;
		}

		public List<NewsFeed> getNewsFeeds() {
			if (newsFeeds == null) {
				newsFeeds = new LinkedList<NewsFeed>();
			}
			return newsFeeds;
		}
	}
}
