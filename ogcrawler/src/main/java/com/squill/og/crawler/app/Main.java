package com.squill.og.crawler.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;

import com.pack.pack.services.exception.PackPackException;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.internal.AppContext;
import com.squill.og.crawler.internal.WebSpiderService;
import com.squill.og.crawler.internal.WebsiteImpl;
import com.squill.og.crawler.model.LinkFilter;
import com.squill.og.crawler.model.Scheduler;
import com.squill.og.crawler.model.WebCrawler;
import com.squill.og.crawler.model.WebCrawlers;

public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
	private static final String WEB_CRAWLERS_CONFIG_FILE = "web.crawlers.config.file";

	public static void main(String[] args) {
		WebSpiderService service = null;
		try {
			AppContext appContext = AppContext.INSTANCE.init();
			service = appContext.findService(WebSpiderService.class);
			List<IWebSite> websites = readCrawlerDefinition();
			if(websites == null || websites.isEmpty())
				return;
			//List<IWebSite> websites = new ArrayList<IWebSite>();
			//websites.add(new PhtographyCanvera());
			service.crawlWebSites(websites);
		} catch (BeansException e) {
			e.printStackTrace();
			LOG.debug(e.getMessage(), e);
		} catch (JAXBException e) {
			e.printStackTrace();
			LOG.debug(e.getMessage(), e);
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
	
	private static List<IWebSite> readCrawlerDefinition() throws JAXBException {
		List<IWebSite> webSites = new ArrayList<IWebSite>();
		String loc = System.getProperty(WEB_CRAWLERS_CONFIG_FILE);
		File file = new File(loc);
		JAXBContext jaxbInstance = JAXBContext.newInstance(
				WebCrawlers.class, WebCrawler.class, Scheduler.class,
				LinkFilter.class);
		Unmarshaller unmarshaller = jaxbInstance.createUnmarshaller();
		WebCrawlers crawlersDef = (WebCrawlers) unmarshaller.unmarshal(file);
		List<WebCrawler> crawlers = crawlersDef.getWebCrawler();
		for(WebCrawler crawler : crawlers) {
			IWebSite webSite = new WebsiteImpl(crawler);
			webSites.add(webSite);
		}
		return webSites;
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