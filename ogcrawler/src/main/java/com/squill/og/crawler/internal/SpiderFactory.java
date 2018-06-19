package com.squill.og.crawler.internal;

import com.squill.crawler.email.SupportEmailSpider;
import com.squill.og.crawler.ICrawlable;
import com.squill.og.crawler.IWebApi;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.Spider;
import com.squill.og.crawler.SpiderSession;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;

/**
 * 
 * @author Saurav
 *
 */
public class SpiderFactory {

	public static final SpiderFactory INSTANCE = new SpiderFactory();

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
}
