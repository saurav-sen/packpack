package com.squill.news.reader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
import com.pack.pack.model.web.JRssFeeds;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.services.registry.ServiceRegistryModes;
import com.pack.pack.util.RssFeedUtil;

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

				String list = (String) properties.get(NEWS_SOURCES);
				String newsAPIKey = (String) properties
						.getProperty(NEWS_API_KEY);
				System.setProperty(NEWS_SOURCES, list);
				System.setProperty(NEWS_API_KEY, newsAPIKey);

				ServiceRegistry.INSTANCE
						.init(ServiceRegistryModes.REDIS_ONLY_SERVICES);
				Runnable cmd = new NewsReader();
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
			//stopApp();
		}
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

		private String[] newsSources;

		private String newsAPIKey;

		NewsReader() {
			String list = System.getProperty(NEWS_SOURCES);
			this.newsSources = list.split(",");
			this.newsAPIKey = System.getProperty(NEWS_API_KEY);
		}

		@Override
		public void run() {
			try {
				LOG.info("Reading News from Sources");
				if (newsSources == null) {
					return;
				}

				List<NewsFeed> newsFeedsList = new ArrayList<NewsFeed>();

				for (String newsSource : newsSources) {
					try {
						NewsFeeds newsFeeds = readFromSource(newsSource);
						if (newsFeeds == null)
							continue;
						newsFeedsList.addAll(newsFeeds.getArticles());
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				}

				uploadNewsFeeds(newsFeedsList);
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

		private void uploadNewsFeeds(List<NewsFeed> newsFeeds) {
			NewsFeeds nfc = new NewsFeeds();
			nfc.getArticles().addAll(newsFeeds);
			JRssFeeds feeds = NewsFeedConverter.convert(nfc);
			LOG.info("Uploading news feeds: Total = " + newsFeeds.size());
			RssFeedUtil.uploadNewFeeds(feeds, false);
		}
	}
}
