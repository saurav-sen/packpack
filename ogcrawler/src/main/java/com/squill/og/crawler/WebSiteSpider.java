package com.squill.og.crawler;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		IHtmlContentHandler contentHandler = webSite.getContentHandler();
		try {
			if (links == null && links.isEmpty()) {
				List<? extends ILink> parseCrawlableURLs = WebSpiderUtils.parseCrawlableURLs(webSite);
				for(ILink parseCrawlableURL : parseCrawlableURLs) {
					links.offer(parseCrawlableURL);
				}
			}
			IRobotScope robotScope = webSite.getRobotScope();
			doCrawl(links, robotScope, contentHandler);
			List<? extends ILink> parseCrawlableURLs = robotScope.getAnyLeftOverLinks();
			for(ILink parseCrawlableURL : parseCrawlableURLs) {
				links.offer(parseCrawlableURL);
			}
			doCrawl(links, robotScope, contentHandler);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		} finally {
			contentHandler.postComplete();
		}
	}
	
	private void doCrawl(Queue<ILink> links, IRobotScope robotScope,
			IHtmlContentHandler contentHandler) throws Exception {
		int count = 0;
		int max = 0;
		while(links != null && !links.isEmpty()) {
			ILink link = links.poll();
			if(contentHandler.getThresholdFrequency() > 0 
					&& max >= contentHandler.getThresholdFrequency()) {
				contentHandler.flush();
				LOG.info("Threshold value reached... crawler will hung up for next scheduled time.");
				return;
			}
			if(count >= contentHandler.getFlushFrequency()) {
				contentHandler.flush();
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
				contentHandler.preProcess(link);
				
				String html = doCrawl(url, info);
				
				HtmlPage htmlPage = ResponseUtil.getParseableHtml(html, link.getUrl());
				List<PageLink> extractAllPageLinks = new PageLinkExtractor(
						robotScope, null).extractAllPageLinks(htmlPage);
				LOG.debug("*** Extracted Page Links ***");
				if(extractAllPageLinks != null && !extractAllPageLinks.isEmpty()) {
					links.addAll(extractAllPageLinks);
					if(LOG.isDebugEnabled()) {
						for(PageLink extractAllPageLink : extractAllPageLinks) {
							LOG.debug(extractAllPageLink.getLink());
						}
					}
				}
				LOG.debug("********************************");
				
				long ttlSeconds = 10 * 24 * 60 * 60;
				tracker.addCrawledInfo(link.getUrl(), info, ttlSeconds);
				
				if(CoreConstants.SKIP.equalsIgnoreCase(html)) {
					continue;
				}
				try {
					contentHandler.postProcess(html, link);
				} catch (Exception e1) {
					LOG.error(e1.getMessage());
					return;
				}
				/*if(info != null) {
					info.setLastCrawled(new Date(System.currentTimeMillis()));
					info.setVisited(true);
					tracker.trackLink(info);
				}*/
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
	
	public String doCrawl(String link, WebSpiderTracker info) throws Exception {
		/*DefaultHttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 200000);
		HttpConnectionParams.setSoTimeout(params, 200000);
		HttpGet GET = new HttpGet(link);
		HttpContext HTTP_CONTEXT = new BasicHttpContext();
		HTTP_CONTEXT.setAttribute(CoreProtocolPNames.USER_AGENT, 
				CoreConstants.TROVE_ROBOT_USER_AGENT_STRING);
		HttpResponse response = client.execute(GET, HTTP_CONTEXT);
		if(response.getStatusLine().getStatusCode() == 200) {
			return EntityUtils.toString(response.getEntity());
		}*/
		return new HttpRequestExecutor().GET(link, info);
	}
}