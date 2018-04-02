package com.squill.og.crawler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.hooks.GenSession;
import com.squill.og.crawler.hooks.IGeoLocationResolver;
import com.squill.og.crawler.hooks.IHtmlContentHandler;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.HtmlPage;
import com.squill.og.crawler.internal.PageLink;
import com.squill.og.crawler.internal.PageLinkExtractor;
import com.squill.og.crawler.internal.utils.CoreConstants;
import com.squill.og.crawler.internal.utils.HttpRequestExecutor;
import com.squill.og.crawler.internal.utils.ResponseUtil;
import com.squill.og.crawler.internal.utils.WebSpiderUtils;
import com.squill.og.crawler.model.WebSpiderTracker;

/**
 * 
 * @author Saurav
 *
 */
public class WebSiteSpider implements Runnable {
	
	private IWebSite webSite;
	private IWebLinkTrackerService tracker;
	
	private static Logger LOG = LoggerFactory.getLogger(WebSiteSpider.class);
	
	private Queue<ILink> links = new ConcurrentLinkedQueue<ILink>();
	
	private long crawlSchedulePeriodicTimeInMillis;
	
	public WebSiteSpider(IWebSite domain, long crawlSchedulePeriodicTimeInMillis, IWebLinkTrackerService tracker) {
		this.webSite = domain;
		this.crawlSchedulePeriodicTimeInMillis = crawlSchedulePeriodicTimeInMillis;
		this.tracker = tracker;
	}

	@Override
	public void run() {
		GenSession session = new GenSessionImpl(webSite);
		IHtmlContentHandler contentHandler = webSite.getContentHandler();
		IGeoLocationResolver geoLocationResolver = webSite.getTargetLocationResolver();
		try {
			if (links.isEmpty()) {
				List<? extends ILink> parseCrawlableURLs = WebSpiderUtils.parseCrawlableURLs(webSite);
				for(ILink parseCrawlableURL : parseCrawlableURLs) {
					links.offer(parseCrawlableURL);
				}
			}
			IRobotScope robotScope = webSite.getRobotScope();
			doCrawl(links, robotScope, contentHandler, geoLocationResolver, session);
			List<? extends ILink> parseCrawlableURLs = robotScope.getAnyLeftOverLinks();
			for(ILink parseCrawlableURL : parseCrawlableURLs) {
				links.offer(parseCrawlableURL);
			}
			doCrawl(links, robotScope, contentHandler, geoLocationResolver, session);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		} finally {
			contentHandler.postComplete(session);
		}
	}
	
	private void doCrawl(Queue<ILink> links, IRobotScope robotScope,
			IHtmlContentHandler contentHandler,
			IGeoLocationResolver geoLocationResolver, GenSession session) throws Exception {
		int count = 0;
		int max = 0;
		while(links != null && !links.isEmpty()) {
			ILink link = links.poll();
			if(contentHandler.getThresholdFrequency() > 0 
					&& max >= contentHandler.getThresholdFrequency()) {
				contentHandler.flush(session);
				LOG.info("Threshold value reached... crawler will hung up for next scheduled time.");
				return;
			}
			if(count >= contentHandler.getFlushFrequency()) {
				contentHandler.flush(session);
				count = 0;
			}
			WebSpiderTracker info = null;
			
			
			if(link == null || link.getUrl() == null || "".equals(link.getUrl().trim()))
				continue;
			if(robotScope.isScoped(link.getUrl())) {
				String url = link.getUrl();
				long currentTimeMillis = System.currentTimeMillis();
				info = tracker.getTrackedInfo(link.getUrl());
				if (info == null) {
					info = new WebSpiderTracker();
				} else if (currentTimeMillis - info.getLastCrawled() >= crawlSchedulePeriodicTimeInMillis) {
					LOG.info("Skip URL <" + url + ">, as it has already visited within periodic delay time. "
							+ "Thereby could be crawler trap.");
					continue;
				}
				info.setLastCrawled(currentTimeMillis);
				info.setLink(link.getUrl());
				
				LOG.info("Visiting " + url);
				contentHandler.preProcess(link, geoLocationResolver, session);
				
				String html = new HttpRequestExecutor().GET(url, info);
				
				long ttlSeconds = 10 * 24 * 60 * 60;
				tracker.addCrawledInfo(link.getUrl(), info, ttlSeconds);
				
				if(CoreConstants.SKIP.equalsIgnoreCase(html)) {
					continue;
				}
				
				HtmlPage htmlPage = ResponseUtil.getParseableHtml(html, link.getUrl());
				List<PageLink> extractAllPageLinks = new PageLinkExtractor(
						robotScope, null).extractAllPageLinks(htmlPage, webSite);
				LOG.info("*** Extracted Page Links ***");
				if(extractAllPageLinks != null && !extractAllPageLinks.isEmpty()) {
					links.addAll(extractAllPageLinks);
					if(LOG.isDebugEnabled()) {
						for(PageLink extractAllPageLink : extractAllPageLinks) {
							LOG.info(extractAllPageLink.getLink());
						}
					}
				}
				LOG.info("********************************");
				
				try {
					contentHandler.postProcess(html, link, geoLocationResolver, session);
				} catch (Exception e1) {
					LOG.error(e1.getMessage());
					return;
				}
				try {
					//Default crawl delay
					int delay = robotScope != null ? robotScope.getDefaultCrawlDelay() : 2000;
					if(delay < 2000) {
						delay = 2000;
					}
					Thread.sleep(2000);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				count++;
				max++;
			}
		}
	}
	
	private class GenSessionImpl implements GenSession {
		
		private Map<String, Object> attrMap = new HashMap<String, Object>();
		
		private IWebSite currentWebSite;
		
		GenSessionImpl(IWebSite currentWebSite) {
			this.currentWebSite = currentWebSite;
		}

		@Override
		public void addAttr(String key, Object value) {
			attrMap.put(key, value);
		}

		@Override
		public Object getAttr(String key) {
			return attrMap.get(key);
		}
		
		@Override
		public IWebSite getCurrentWebSite() {
			return currentWebSite;
		}
	}
}