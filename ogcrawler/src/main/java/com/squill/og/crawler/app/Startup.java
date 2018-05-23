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

import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.services.registry.ServiceRegistryModes;
import com.pack.pack.util.SystemPropertyUtil;
import com.squill.og.crawler.ICrawlable;
import com.squill.og.crawler.IWebCrawlable;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.internal.AppContext;
import com.squill.og.crawler.internal.WebApiImpl;
import com.squill.og.crawler.internal.WebSpiderService;
import com.squill.og.crawler.internal.WebsiteImpl;
import com.squill.og.crawler.model.ApiReader;
import com.squill.og.crawler.model.ApiRequestExecutor;
import com.squill.og.crawler.model.ArticleSummarizer;
import com.squill.og.crawler.model.ContentHandler;
import com.squill.og.crawler.model.FeedUploader;
import com.squill.og.crawler.model.GeoTagResolver;
import com.squill.og.crawler.model.LinkFilter;
import com.squill.og.crawler.model.Properties;
import com.squill.og.crawler.model.Property;
import com.squill.og.crawler.model.Scheduler;
import com.squill.og.crawler.model.TaxonomyClassifier;
import com.squill.og.crawler.model.WebCrawler;
import com.squill.og.crawler.model.WebCrawlers;
import com.squill.og.crawler.model.WebTracker;
import com.squill.services.exception.OgCrawlException;

/**
 * 
 * @author Saurav
 *
 */
public class Startup {

	private static final Logger LOG = LoggerFactory.getLogger(Startup.class);
	
	private String[] args;
	private Options options = new Options();
	
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
				System.setProperty(SystemPropertyKeys.WEB_CRAWLERS_CONFIG_FILE, optionValue);
				File file = new File(optionValue);
				System.setProperty(SystemPropertyKeys.WEB_CRAWLERS_CONFIG_DIR, file.getParent());
				File parentFile = file.getParentFile();
				System.setProperty(SystemPropertyKeys.WEB_CRAWLERS_BASE_DIR, parentFile.getParent());
				WebCrawlers crawlersDef = readCrawlerDefinition();
				readSystemPropertiesConfigured(crawlersDef);
				AppContext appContext = AppContext.INSTANCE.init();
				SystemPropertyUtil.init();
				ServiceRegistry.INSTANCE
						.init(ServiceRegistryModes.REDIS_ONLY_SERVICES);
				//check();
				IFeedUploader feedUploader = loadFeedUploader(crawlersDef);
				List<IWebSite> websites = readCrawlableWebSites(crawlersDef);
				List<IWebCrawlable> webApis = readRegisteredWebApis(crawlersDef);
				List<ICrawlable> allCrawlables = new ArrayList<ICrawlable>();
				allCrawlables.addAll(websites);
				allCrawlables.addAll(webApis);
				if(allCrawlables.isEmpty())
					return;
				WebSpiderService webSpiderService = appContext
						.findService(WebSpiderService.class);
				webSpiderService.startCrawling(allCrawlables, feedUploader);
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
	
	/*private void check() {
		try {
			IRssFeedService service = ServiceRegistry.INSTANCE
					.findCompositeService(IRssFeedService.class);
			if(service != null) {
				LOG.info("NOT NULL");
				LOG.debug("NOT NULL");
			}
			LOG.info("NULL");
			LOG.debug("NULL");
		} catch (PackPackException e) {
			LOG.error(e.getMessage(), e);
			LOG.debug(e.getMessage(), e);
		}
		
		System.exit(1);
	}*/
	
	private void stopApp() {
		try {
			AppContext appContext = AppContext.INSTANCE.init();
			WebSpiderService webSpiderService = appContext.findService(WebSpiderService.class);
			if (webSpiderService != null) {
				webSpiderService.shutdown();
			}
		} catch (OgCrawlException e) {
			LOG.debug(e.getMessage(), e);
		}
	}
	
	private void help() {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("java -jar ogcrawler-1.0.0-<X>.jar", options);
		System.exit(0);
	}
	
	private WebCrawlers readCrawlerDefinition() throws JAXBException {
		String loc = System.getProperty(SystemPropertyKeys.WEB_CRAWLERS_CONFIG_FILE);
		File file = new File(loc);
		JAXBContext jaxbInstance = JAXBContext.newInstance(WebCrawlers.class,
				WebCrawler.class, Scheduler.class, LinkFilter.class,
				FeedUploader.class, ContentHandler.class, Scheduler.class,
				Properties.class, Property.class, WebTracker.class,
				ApiReader.class, ApiRequestExecutor.class,
				ArticleSummarizer.class, GeoTagResolver.class,
				TaxonomyClassifier.class);
		Unmarshaller unmarshaller = jaxbInstance.createUnmarshaller();
		WebCrawlers crawlersDef = (WebCrawlers) unmarshaller.unmarshal(file);
		return crawlersDef;
	}
	
	private void readSystemPropertiesConfigured(WebCrawlers crawlersDef) {
		Properties properties = crawlersDef.getProperties();
		if(properties != null) {
			List<Property> list = properties.getProperty();
			if(list != null && !list.isEmpty()) {
				for(Property l : list) {
					String key = l.getKey();
					String value = l.getValue();
					if (value != null && !value.isEmpty()
							&& value.startsWith("${") && value.endsWith("}")) {
						String v1 = value.substring(0, value.length() - 1);
						v1 = v1.replaceFirst("\\$\\{", "");
						v1 = System.getProperty(v1);
						if(v1 != null) {
							value = v1;
						}
					}
					System.setProperty(key, value);
				}
			}
		}
	}
	
	private IFeedUploader loadFeedUploader(WebCrawlers crawlersDef) {
		IFeedUploader feedUploader = null;
		FeedUploader feedUploaderDef = crawlersDef.getFeedUploader();
		String uploader = feedUploaderDef.getUploader();
		try {
			feedUploader = AppContext.INSTANCE.findService(uploader,
					IFeedUploader.class);
		} catch (NoSuchBeanDefinitionException e) {
			LOG.error(e.getMessage(), e);
		}
		if (feedUploader == null) {
			try {
				Object newInstance = Class.forName(uploader).newInstance();
				if (newInstance instanceof IFeedUploader) {
					feedUploader = (IFeedUploader) newInstance;
				}
			} catch (InstantiationException e) {
				LOG.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return feedUploader;
	}
	
	private List<IWebSite> readCrawlableWebSites(WebCrawlers crawlersDef) {
		List<IWebSite> webSites = new ArrayList<IWebSite>();
		List<WebCrawler> crawlers = crawlersDef.getWebCrawler();
		for (WebCrawler crawler : crawlers) {
			IWebSite webSite = new WebsiteImpl(crawler, crawlersDef.getWebLinkTracker());
			webSites.add(webSite);
		}
		return webSites;
	}
	
	private List<IWebCrawlable> readRegisteredWebApis(WebCrawlers crawlersDef) {
		List<IWebCrawlable> webApis = new ArrayList<IWebCrawlable>();
		List<ApiReader> apiReaders = crawlersDef.getApiReader();
		for (ApiReader apiReader : apiReaders) {
			IWebCrawlable webApi = new WebApiImpl(apiReader, crawlersDef.getWebLinkTracker());
			webApis.add(webApi);
		}
		return webApis;
	}
}