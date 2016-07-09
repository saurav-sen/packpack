package com.squill.og.crawler.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pack.pack.IRssFeedService;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.TTL;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.squill.og.crawler.ICrawlSchedule;
import com.squill.og.crawler.IHtmlContentHandler;
import com.squill.og.crawler.ILink;
import com.squill.og.crawler.IRobotScope;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.internal.WebSpiderService;

public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		WebSpiderService service = null;
		try {
			ServiceRegistry.INSTANCE.init();
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					"META-INF/beans.xml");
			service = context.getBean(WebSpiderService.class);
			List<IWebSite> websites = new ArrayList<IWebSite>();
			websites.add(new PhtographyCanvera());
			service.crawlWebSites(websites);
		} catch (BeansException e) {
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

	private static class PhtographyCanvera implements IWebSite {

		@Override
		public String getDomainUrl() {
			return "http://www.burrard-lucas.com";// "http://photographers.canvera.com";
		}

		@Override
		public IRobotScope getRobotScope() {
			return new IRobotScope() {

				@Override
				public boolean isScoped(String link) {
					/*
					 * if(link.contains("nature")) { return true; }
					 */
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
					/*
					 * try { System.out.println(JSONUtil.serialize(feed)); }
					 * catch (PackPackException e) { e.printStackTrace(); }
					 */
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
						// TODO Auto-generated catch block
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

	}
}