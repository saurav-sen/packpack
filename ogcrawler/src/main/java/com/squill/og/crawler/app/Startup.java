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
import com.squill.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public class Startup {

	private static final Logger LOG = LoggerFactory.getLogger(Startup.class);
	
	private static final String WEB_CRAWLERS_CONFIG_FILE = "web.crawlers.config.file";
	
	private String[] args;
	private Options options = new Options();
	
	private IWebLinkTrackerService historyTracker;
	
	private WebSpiderService service;
	
	public Startup(String[] args) {
		this.args = args;
		options.addOption("h", "help", false, "Show Help");
		options.addOption("f", "file", true, "Configuration File For Crawler Definition");
	}
	
	public static void main(String[] args) {
		final Startup app = new Startup(args);
		try {
			app.startApp();
		} catch (BeansException e) {
			LOG.debug(e.getMessage(), e);
		} catch (JAXBException e) {
			LOG.debug(e.getMessage(), e);
		}
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				LOG.info("Shutting Down...");
				app.stopApp();
			}
		}));
	}
	
	private void startApp() throws BeansException, JAXBException {
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
				service.crawlWebSites(websites);
			} else {
				help();
			}
		} catch (ParseException e) {
			LOG.trace(e.getMessage(), e);
			help();
		} finally {
			stopApp();
		}
	}
	
	private void stopApp() {
		try {
			if (service != null) {
				service.shutdown();
			}
		} catch (PackPackException e) {
			LOG.debug(e.getMessage(), e);
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
}