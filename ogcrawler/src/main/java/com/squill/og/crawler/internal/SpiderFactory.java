package com.squill.og.crawler.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.crawler.email.SupportEmailSpider;
import com.squill.og.crawler.ICrawlable;
import com.squill.og.crawler.IWebApi;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.Spider;
import com.squill.og.crawler.SpiderSession;
import com.squill.og.crawler.hooks.ISpiderSession;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;

/**
 * 
 * @author Saurav
 *
 */
public class SpiderFactory {

	public static final SpiderFactory INSTANCE = new SpiderFactory();
	
	private static Logger LOG = LoggerFactory.getLogger(SpiderFactory.class);

	private SpiderFactory() {
	}

	public Spider createNewSpiderInstance(ICrawlable crawlable,
			long crawlSchedulePeriodicTimeInMillis,
			IWebLinkTrackerService trackerService, SpiderSession session) {
		if (crawlable instanceof IWebSite) {
			IWebSite webSite = (IWebSite) crawlable;
			return new WebSiteSpider(webSite,
					crawlSchedulePeriodicTimeInMillis,
					webSite.getTrackerService(), session);
		} else if(crawlable instanceof IWebApi) {
			IWebApi webApi = (IWebApi) crawlable;
			return new WebApiSpider(webApi, crawlSchedulePeriodicTimeInMillis,
					webApi.getTrackerService(), session);
		}
		return null;
	}
	
	public Spider createSupportEmailSpider() {
		return new SupportEmailSpider();
	}
	
	public Runnable createSpiderSessionRefresher(ISpiderSession session) {
		return new SpiderSessionRefresher(session);
	}
	
	private class SpiderSessionRefresher implements Runnable {
		
		private ISpiderSession session;
		
		SpiderSessionRefresher(ISpiderSession session) {
			this.session = session;
		}
		
		@Override
		public void run() {
			LOG.info("Refreshing WebSpider Session");
			session.refresh();
			LOG.info("Done refreshing WebSpider Session");
		}
	}
}
