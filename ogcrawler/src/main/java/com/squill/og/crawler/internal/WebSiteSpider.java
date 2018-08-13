package com.squill.og.crawler.internal;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.feed.web.model.JRssFeed;
import com.squill.og.crawler.ILink;
import com.squill.og.crawler.IRobotScope;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.Spider;
import com.squill.og.crawler.SpiderSession;
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.IGeoLocationResolver;
import com.squill.og.crawler.hooks.IHtmlContentHandler;
import com.squill.og.crawler.hooks.ISpiderSession;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.utils.CoreConstants;
import com.squill.og.crawler.internal.utils.HttpRequestExecutor;
import com.squill.og.crawler.internal.utils.ResponseUtil;
import com.squill.og.crawler.internal.utils.WebSpiderUtils;
import com.squill.og.crawler.model.WebSpiderTracker;
import com.squill.og.crawler.rss.RSSConstants;

/**
 * 
 * @author Saurav
 *
 */
public class WebSiteSpider implements Spider {
	
	private IWebSite webSite;
	private IWebLinkTrackerService tracker;
	
	private static Logger $LOG = LoggerFactory.getLogger(WebSiteSpider.class);
	
	private Queue<ILink> links = new ConcurrentLinkedQueue<ILink>();
	
	private long crawlSchedulePeriodicTimeInMillis;
	
	private SpiderSession session;
	
	public WebSiteSpider(IWebSite domain, long crawlSchedulePeriodicTimeInMillis, IWebLinkTrackerService tracker, SpiderSession session) {
		this.webSite = domain;
		this.crawlSchedulePeriodicTimeInMillis = crawlSchedulePeriodicTimeInMillis;
		this.tracker = tracker;
		this.session = session;
	}
	
	@Override
	public void run() {
		if(session.isThresholdReached())
			return;
		IHtmlContentHandler contentHandler = webSite.getContentHandler();
		IGeoLocationResolver geoLocationResolver = webSite.getTargetLocationResolver();
		IFeedUploader feedUploader = session.getFeedUploader();
		try {
			session.begin(webSite);
			feedUploader.beginEach(session, webSite);
			if (links.isEmpty()) {
				List<? extends ILink> parseCrawlableURLs = WebSpiderUtils.parseCrawlableURLs(webSite);
				Collections.sort(parseCrawlableURLs, new Comparator<ILink>() {
					@Override
					public int compare(ILink o1, ILink o2) {
						return  (int)(o2.getLastModified() - o1.getLastModified());
					}
				});
				for(ILink parseCrawlableURL : parseCrawlableURLs) {
					links.offer(parseCrawlableURL);
				}
			}
			IRobotScope robotScope = webSite.getRobotScope();
			doCrawl(links, robotScope, contentHandler, geoLocationResolver, session, webSite.isPageLinkExtractorEnabled(), webSite.getUniqueId());
			List<? extends ILink> parseCrawlableURLs = robotScope.getAnyLeftOverLinks();
			for(ILink parseCrawlableURL : parseCrawlableURLs) {
				links.offer(parseCrawlableURL);
			}
			doCrawl(links, robotScope, contentHandler, geoLocationResolver, session, webSite.isPageLinkExtractorEnabled(), webSite.getUniqueId());
			Map<String, List<JRssFeed>> collectiveFeeds = contentHandler.getCollectiveFeeds(session);
			new AllInOneAITaskExecutor(session).executeTasks(collectiveFeeds, webSite);
			//AllInOneAITaskExecutor allInOneAITaskExecutor = new AllInOneAITaskExecutor(session);
			//collectiveFeeds = allInOneAITaskExecutor.executeTasks(collectiveFeeds, webSite);
			//JRssFeeds rssFeeds = uniteAll(collectiveFeeds);
			//session.addAttr(webSite, ISpiderSession.RSS_FEEDS_KEY, rssFeeds);
			feedUploader.endEach(session, webSite);
		} catch (Throwable e) {
			$LOG.error(e.getMessage(), e);
		} finally {
			session.end(webSite);
		}
	}
	
	/*private JRssFeeds uniteAll(Map<String, List<JRssFeed>> collectiveFeeds) {
		JRssFeeds rssFeeds = new JRssFeeds();
		Iterator<String> itr = collectiveFeeds.keySet().iterator();
		while(itr.hasNext()) {
			String key = itr.next();
			List<JRssFeed> values = collectiveFeeds.get(key);
			if(values == null || values.isEmpty())
				continue;
			rssFeeds.getFeeds().addAll(values);
		}
		return rssFeeds;
	}*/
	
	private void doCrawl(Queue<ILink> links, IRobotScope robotScope,
			IHtmlContentHandler contentHandler,
			IGeoLocationResolver geoLocationResolver, ISpiderSession session, boolean isPageLinkExtractorEnabled, String webSiteId) throws Exception {
		//int count = 0;
		int max = 0;
		while(links != null && !links.isEmpty()) {
			ILink link = links.poll();
			if(contentHandler.getThresholdFrequency() > 0 
					&& max >= contentHandler.getThresholdFrequency()) {
				$LOG.info("Threshold value reached... crawler will hung up for next scheduled time.");
				return;
			}
			/*if(count >= contentHandler.getFlushFrequency()) {
				count = 0;
			}*/
			WebSpiderTracker info = null;
			
			
			if(link == null || link.getUrl() == null || "".equals(link.getUrl().trim()))
				continue;
			
			boolean incrementCrawledLinkCount4NewLink = false;
			if(robotScope.isScoped(link.getUrl())) {
				String url = link.getUrl();
				long currentTimeMillis = System.currentTimeMillis();
				info = tracker.getTrackedInfo(url);
				if (info == null) {
					info = new WebSpiderTracker();
					info.setWebCrawlerId(webSiteId);
					incrementCrawledLinkCount4NewLink = true;
				} else if (currentTimeMillis - info.getLastCrawled() >= crawlSchedulePeriodicTimeInMillis) {
					$LOG.info("Skip URL <" + url + ">, as it has already visited within periodic delay time. "
							+ "Thereby could be crawler trap.");
					continue;
				}
				info.setLastCrawled(currentTimeMillis);
				info.setLink(link.getUrl());
				
				$LOG.info("Visiting " + url);
				contentHandler.preProcess(link, session);
				
				String html = new HttpRequestExecutor().GET(url, info);
				
				tracker.upsertCrawledInfo(url, info,
						RSSConstants.DEFAULT_TTL_WEB_TRACKING_INFO, false);
				
				if(CoreConstants.SKIP.equalsIgnoreCase(html)) {
					continue;
				}
				
				HtmlPage htmlPage = ResponseUtil.getParseableHtml(html, link.getUrl());
				List<PageLink> extractAllPageLinks = isPageLinkExtractorEnabled ? new PageLinkExtractor(
						robotScope, null).extractAllPageLinks(htmlPage, webSite) : new LinkedList<PageLink>();
				$LOG.info("*** Extracted Page Links ***");
				if(extractAllPageLinks != null && !extractAllPageLinks.isEmpty()) {
					links.addAll(extractAllPageLinks);
					if($LOG.isDebugEnabled()) {
						for(PageLink extractAllPageLink : extractAllPageLinks) {
							$LOG.info(extractAllPageLink.getLink());
						}
					}
				}
				$LOG.info("********************************");
				
				try {
					contentHandler.postProcess(html, link, session);
				} catch (Exception e1) {
					$LOG.error(e1.getMessage());
					return;
				}
				try {
					//Default crawl delay
					long delay = robotScope != null ? robotScope.getDefaultCrawlDelay() : 2000;
					if(delay < 2000) {
						delay = 2000;
					}
					if(delay > 2 * 60 * 1000) { // If delay is more than 2 minutes
						robotScope.halt();
						Thread.sleep(2000);
					} else {
						Thread.sleep(delay);
					}
				} catch (Exception e) {
					$LOG.error(e.getMessage(), e);
				}
				//count++;
				if(incrementCrawledLinkCount4NewLink) {
					max++;
					robotScope.incrementLinkCount();
				}
			}
		}
	}
}