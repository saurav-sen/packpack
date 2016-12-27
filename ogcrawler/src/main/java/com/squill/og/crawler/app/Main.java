package com.squill.og.crawler.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.pack.pack.services.exception.PackPackException;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.AppContext;
import com.squill.og.crawler.internal.WebSpiderService;
import com.squill.og.crawler.internal.WebsiteImpl;
import com.squill.og.crawler.model.Config;
import com.squill.og.crawler.model.ContentHandler;
import com.squill.og.crawler.model.FeedUploader;
import com.squill.og.crawler.model.LinkFilter;
import com.squill.og.crawler.model.Properties;
import com.squill.og.crawler.model.Property;
import com.squill.og.crawler.model.Scheduler;
import com.squill.og.crawler.model.WebCrawler;
import com.squill.og.crawler.model.WebCrawlers;
import com.squill.og.crawler.model.WebTracker;

/**
 * 
 * @author Saurav
 *
 */
public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
	private static final String WEB_CRAWLERS_CONFIG_FILE = "web.crawlers.config.file";
	
	private String[] args;
	private Options options = new Options();
	
	private IWebLinkTrackerService historyTracker;
	
	public Main(String[] args) {
		this.args = args;
		options.addOption("h", "help", false, "Show Help");
		options.addOption("f", "file", true, "Configuration File For Crawler Definition");
	}
	
	public static void main(String[] args) {
		try {
			new Main(args).startApp();
		} catch (BeansException e) {
			LOG.debug(e.getMessage(), e);
		} catch (JAXBException e) {
			LOG.debug(e.getMessage(), e);
		}
	}
	
	private void startApp() throws BeansException, JAXBException {
		WebSpiderService service = null;
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine command = parser.parse(options, args);
			if(command.hasOption("h")) {
				help();
			} else if(command.hasOption("f")) {
				String optionValue = command.getOptionValue("f");
				System.setProperty(WEB_CRAWLERS_CONFIG_FILE, optionValue);
				AppContext appContext = AppContext.INSTANCE.init();
				service = appContext.findService(WebSpiderService.class);
				List<IWebSite> websites = readCrawlerDefinition();
				if(websites == null || websites.isEmpty())
					return;
				service.setTrackerService(historyTracker);
				//List<IWebSite> websites = new ArrayList<IWebSite>();
				//websites.add(new PhtographyCanvera());
				service.crawlWebSites(websites);
			} else {
				help();
			}
		} catch (ParseException e) {
			LOG.trace(e.getMessage(), e);
			help();
		} finally {
			try {
				if (service != null) {
					service.shutdown();
				}
			} catch (PackPackException e) {
				LOG.debug(e.getMessage(), e);
			}
		}
	}
	
	private void help() {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("java -jar ogcrawler-1.0.0-<X>.jar", options);
		System.exit(0);
	}
	
	private List<IWebSite> readCrawlerDefinition() throws JAXBException {
		List<IWebSite> webSites = new ArrayList<IWebSite>();
		String loc = System.getProperty(WEB_CRAWLERS_CONFIG_FILE);
		File file = new File(loc);
		JAXBContext jaxbInstance = JAXBContext.newInstance(WebCrawlers.class,
				WebCrawler.class, Scheduler.class, LinkFilter.class,
				FeedUploader.class, Config.class, ContentHandler.class,
				Scheduler.class, Properties.class, Property.class,
				WebTracker.class);
		Unmarshaller unmarshaller = jaxbInstance.createUnmarshaller();
		WebCrawlers crawlersDef = (WebCrawlers) unmarshaller.unmarshal(file);
		Properties properties = crawlersDef.getProperties();
		if(properties != null) {
			List<Property> list = properties.getProperty();
			if(list != null && !list.isEmpty()) {
				for(Property l : list) {
					String key = l.getKey();
					String value = l.getValue();
					System.setProperty(key, value);
				}
			}
		}
		List<WebCrawler> crawlers = crawlersDef.getWebCrawler();
		historyTracker = loadWebHistoryTracker(crawlersDef.getWebTracker());
		for (WebCrawler crawler : crawlers) {
			IWebSite webSite = new WebsiteImpl(crawler);
			webSites.add(webSite);
		}
		return webSites;
	}
	
	private IWebLinkTrackerService loadWebHistoryTracker(WebTracker webTracker) {
		if(webTracker == null)
			return null;
		IWebLinkTrackerService historyTracker = null;
		String serviceId = webTracker.getServiceId();
		try {
			historyTracker = AppContext.INSTANCE.findService(
					serviceId, IWebLinkTrackerService.class);
		} catch (NoSuchBeanDefinitionException e) {
			LOG.error(e.getMessage(), e);
		}
		if(historyTracker == null) {
			try {
				Object newInstance = Class.forName(serviceId).newInstance();
				if(newInstance instanceof IWebLinkTrackerService) {
					historyTracker = (IWebLinkTrackerService)newInstance;
				}
			} catch (InstantiationException e) {
				LOG.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return historyTracker;
	}
	

	/*private static class PhtographyCanvera implements IWebSite {

		@Override
		public String getDomainUrl() {
			return "http://www.burrard-lucas.com";// "http://photographers.canvera.com";
		}

		@Override
		public IRobotScope getRobotScope() {
			return new IRobotScope() {

				@Override
				public boolean isScoped(String link) {
					if (link.contains("collections/")) {
						return true;
					}
					return false;
				}

				@Override
				public int getDefaultCrawlDelay() {
					return 2;
				}

				@Override
				public List<? extends ILink> getAnyLeftOverLinks() {
					return Collections.emptyList();
				}
			};
		}

		@Override
		public IHtmlContentHandler getContentHandler() {
			return new IHtmlContentHandler() {

				private List<JRssFeed> feeds = new ArrayList<JRssFeed>();

				@Override
				public void preProcess(ILink link) {
				}

				@Override
				public void postProcess(String htmlContent, ILink link) {
					Document doc = Jsoup.parse(htmlContent);

					String title = null;
					Elements metaOgTitle = doc
							.select("meta[property=og:title]");
					if (metaOgTitle != null) {
						title = metaOgTitle.attr("content");
					}

					String description = null;
					Elements metaOgDescription = doc
							.select("meta[property=og:title]");
					if (metaOgDescription != null) {
						description = metaOgDescription.attr("content");
					}

					String type = null;
					Elements metaOgType = doc.select("meta[property=og:type]");
					if (metaOgType != null) {
						type = metaOgType.attr("content");
					}

					String imageUrl = null;
					Elements metaOgImage = doc
							.select("meta[property=og:image]");
					if (metaOgImage != null) {
						imageUrl = metaOgImage.attr("content");
					}

					String hrefUrl = null;
					Elements metaOgUrl = doc.select("meta[property=og:url]");
					if (metaOgUrl != null) {
						hrefUrl = metaOgUrl.attr("content");
					}

					JRssFeed feed = new JRssFeed();
					feed.setOgTitle(title);
					feed.setOgDescription(description);
					feed.setOgImage(imageUrl);
					feed.setOgUrl(hrefUrl);
					feed.setHrefSource(hrefUrl);
					feed.setOgType(type);

					feeds.add(feed);
				}

				@Override
				public void postComplete() {
					List<JRssFeed> list = new ArrayList<JRssFeed>();
					list.addAll(feeds);
					feeds.clear();
					uploadAll(list);
					list.clear();
				}

				private void uploadAll(List<JRssFeed> feeds) {
					for (JRssFeed feed : feeds) {
						upload(feed);
					}
				}

				private void upload(JRssFeed feed) {
					try {
						TTL ttl = new TTL();
						ttl.setTime((short)1);
						ttl.setUnit(TimeUnit.DAYS);
						IRssFeedService service = ServiceRegistry.INSTANCE
								.findCompositeService(IRssFeedService.class);
						service.upload(feed, ttl);
					} catch (PackPackException e) {
						e.printStackTrace();
					}
				}

				@Override
				public int getThresholdFrequency() {
					return 10;
				}

				@Override
				public int getFlushFrequency() {
					return 50;
				}
				
				@Override
				public void setFlushFrequency(int flushFrequency) {
					
				}
				
				@Override
				public void setThresholdFrequency(int thresholdFrequency) {
				}

				@Override
				public void flush() {
					postComplete();
				}
			};
		}

		@Override
		public ICrawlSchedule getSchedule() {
			return new ICrawlSchedule() {

				@Override
				public TimeUnit getTimeUnit() {
					return TimeUnit.DAYS;
				}

				@Override
				public long getPeriodicDelay() {
					return 1;
				}

				@Override
				public long getInitialDelay() {
					return 0;
				}
			};
		}

		@Override
		public boolean needToTrackCrawlingHistory() {
			return false;
		}

		@Override
		public boolean shouldCheckRobotRules() {
			return true;
		}

	}*/
}