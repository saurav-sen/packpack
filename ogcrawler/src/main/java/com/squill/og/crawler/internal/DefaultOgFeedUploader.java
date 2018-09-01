package com.squill.og.crawler.internal;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.util.RssFeedUtil;
import com.pack.pack.util.SystemPropertyUtil;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.feed.web.model.TTL;
import com.squill.og.crawler.IWebCrawlable;
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.ISpiderSession;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.utils.HtmlUtil;
import com.squill.og.crawler.internal.utils.JSONUtil;
import com.squill.og.crawler.internal.utils.NotificationUtil;
import com.squill.og.crawler.model.WebSpiderTracker;
import com.squill.og.crawler.rss.RSSConstants;

/**
 * 
 * @author Saurav
 *
 */
@Component("defaultOgFeedUploader")
@Scope("prototype")
public class DefaultOgFeedUploader implements IFeedUploader {

	/*private static final String BASE_URL_CONFIG = "BASE_URL";
	private static final String URL_PART_CONFIG = "URL_PART";
	private static final String API_KEY_CONFIG = "API_KEY";
	
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	private static final String APPLICATION_JSON = "application/json";*/

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultOgFeedUploader.class);
	
	private int count = 0;
	
	@Override
	public void beginEach(ISpiderSession session, IWebCrawlable webCrawlable) {
		if(!webCrawlable.isUploadIndependently()) {
			count++;
		}
	}
	
	private List<WebSpiderTracker> getAllFeedsToBeUploaded(IWebCrawlable webCrawlable) {
		List<WebSpiderTracker> result = new LinkedList<WebSpiderTracker>();
		IWebLinkTrackerService webLinkTrackerService = webCrawlable.getTrackerService();
		List<WebSpiderTracker> allTrackedInfo = webLinkTrackerService.getAllTrackedInfo();
		if(allTrackedInfo == null || allTrackedInfo.isEmpty())
			return result;
		String webCrawlerId = webCrawlable.getUniqueId();
		for(WebSpiderTracker trackedInfo : allTrackedInfo) {
			if(trackedInfo.isUploadCompleted())
				continue;
			if(!webCrawlerId.equals(trackedInfo.getWebCrawlerId()))
				continue;
			JRssFeed feedToUpload = trackedInfo.getFeedToUpload();
			if(feedToUpload == null)
				continue;
			result.add(trackedInfo);
			
			trackedInfo.setUploadCompleted(true);
			//webLinkTrackerService.upsertCrawledInfo(trackedInfo.getLink(), trackedInfo, RSSConstants.DEFAULT_TTL_WEB_TRACKING_INFO, false);
		}
		return result;
	}
	
	@Override
	public void endEach(ISpiderSession session, IWebCrawlable webCrawlable) {
		Map<String, List<JRssFeed>> map = new HashMap<String, List<JRssFeed>>();
		if(!webCrawlable.isUploadIndependently()) {
			count--;
		}
		
		/*JRssFeeds feeds = session.getFeeds(webCrawlable);
		if(feeds == null) {
			LOG.debug("No feeds found for :: " + webCrawlable.getUniqueId());
			return;
		}
		List<JRssFeed> list = feeds.getFeeds();*/
		List<WebSpiderTracker> list = getAllFeedsToBeUploaded(webCrawlable);
		if(list == null || list.isEmpty()) {
			LOG.debug("No feeds found for :: " + webCrawlable.getUniqueId());
			return;
		}
		map.put(webCrawlable.getUniqueId(), readFeeds(list));
		if (webCrawlable.isUploadIndependently()) {
			try {
				uploadBulk(map, session.getBatchId(), webCrawlable);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		} else if (count == 0) {
			try {
				uploadBulk(map, session.getBatchId(), webCrawlable);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		
		try {
			while(session.hashMoreNotificationMessages()) {
				NotificationUtil.broadcastNewRSSFeedUploadSummary(session.getTopNotificationMessage());
			}
		} catch (PackPackException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	protected List<JRssFeed> readFeeds(List<WebSpiderTracker> list) {
		List<JRssFeed> result = new ArrayList<JRssFeed>();
		if(list == null)
			return result;
		for(WebSpiderTracker l : list) {
			JRssFeed feedToUpload = l.getFeedToUpload();
			if(feedToUpload == null)
				continue;
			result.add(feedToUpload);
		}
		return result;
	}
	
	protected JRssFeeds adapt(Map<String, List<JRssFeed>> map) {
		JRssFeeds allFeeds = new JRssFeeds();
		Iterator<List<JRssFeed>> itr = map.values().iterator();
		while(itr.hasNext()) {
			List<JRssFeed> list = itr.next();
			if(list == null || list.isEmpty())
				continue;
			allFeeds.getFeeds().addAll(list);
		}
		return allFeeds;
	}
	
	private JRssFeeds storeInArchive(String id, List<JRssFeed> list) {
		JRssFeeds c = new JRssFeeds();
		LOG.debug("storeInArchive(String id, List<JRssFeed> list)");
		try {
			String filePath = resolveArchiveFilePath(id);
			File file = new File(filePath);
			if(!file.exists()) {
				c.getFeeds().addAll(list);
				String json = JSONUtil.serialize(c);
				LOG.debug("Writing to file @ " + filePath);
				Files.write(Paths.get(filePath), json.getBytes(), StandardOpenOption.CREATE);
			} else {
				String json = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
				c = JSONUtil.deserialize(json, JRssFeeds.class, true);
				c.getFeeds().addAll(list);
				json = JSONUtil.serialize(c);
				LOG.debug("Writing to file @ " + filePath);
				Files.write(Paths.get(filePath), json.getBytes(), StandardOpenOption.WRITE);
			}
		} catch (Exception e) {
			LOG.error("Failed storing in archive :: " + e.getMessage(), e);
		}
		return c;
	}
	
	private String resolveArchiveFilePath(String id) {
		DateTime dateTime = new DateTime();
		int day = dateTime.getDayOfMonth();
		int month = dateTime.getMonthOfYear();
		int year = dateTime.getYear();
		String dirPath = SystemPropertyUtil.getDefaultArchiveFolder();
		File file = new File(dirPath);
		if(!file.exists()) {
			file.mkdir();
		}
		dirPath = dirPath + id;
		StringBuilder fileName = new StringBuilder(dirPath).append("-");
		if(day < 10) {
			fileName.append("0");
		}
		fileName.append(String.valueOf(day));
		fileName.append("-");
		if(month < 10) {
			fileName.append("0");
		}
		fileName.append(String.valueOf(month));
		fileName.append("-");
		fileName.append(String.valueOf(year));
		fileName.append(".json");
		return fileName.toString();
	}
	
	private Map<String, List<JRssFeed>> storeInArchive(Map<String, List<JRssFeed>> map) {
		Map<String, List<JRssFeed>> result = new HashMap<String, List<JRssFeed>>();
		LOG.debug("Storing in archive");
		Iterator<String> itr = map.keySet().iterator();
		while(itr.hasNext()) {
			String id = itr.next();
			List<JRssFeed> list = map.get(id);
			if(list.isEmpty())
				continue;
			JRssFeeds c = storeInArchive(id, list);
			if(c != null) {
				result.put(id, c.getFeeds());
			}
		}
		return result;
	}

	private void uploadBulk(Map<String, List<JRssFeed>> map, long batchId, IWebCrawlable webCrawlable) throws Exception {
		if(!ServiceIdResolver.isUploadMode()) {
			LOG.debug("Not running in Upload Mode, ignoring upload");
			return;
		}
		if(LOG.isDebugEnabled()) {
			JRssFeeds rssFeeds = adapt(map);
			LOG.debug("Total feeds to be uploaded without backlog = " + rssFeeds.getFeeds().size());
		}
		dataCleanUp(map);
		map = storeInArchive(map);
		JRssFeeds rssFeeds = adapt(map);
		LOG.info("Uploading news feeds: Total = " + rssFeeds.getFeeds().size());
		TTL ttl = new TTL();
		ttl.setTime((short) 1);
		ttl.setUnit(TimeUnit.DAYS);
		
		HtmlUtil.generateNewsFeedsHtmlPages(rssFeeds);
		
		//RssFeedUtil.uploadNewsFeeds(rssFeeds, ttl, batchId, true);
		
		RssFeedUtil.uploadNewsFeeds(rssFeeds, ttl, System.currentTimeMillis(), true);
		
		IWebLinkTrackerService webLinkTrackerService = webCrawlable.getTrackerService();
		List<JRssFeed> feeds = rssFeeds.getFeeds();
		for(JRssFeed feed : feeds) {
			String link = feed.getOgUrl();
			WebSpiderTracker info = webLinkTrackerService.getTrackedInfo(link);
			if(info == null) {
				info = new WebSpiderTracker();
				info.setWebCrawlerId(webCrawlable.getUniqueId());
			}
			info.setLink(link);
			info.setUploadCompleted(true);
			webLinkTrackerService.upsertCrawledInfo(link, info, RSSConstants.DEFAULT_TTL_WEB_TRACKING_INFO, false);
		}
		
		//NotificationUtil.broadcastNewRSSFeedUploadSummary("You have new Items");
	}
	
	private void dataCleanUp(Map<String, List<JRssFeed>> map) {
		Iterator<String> itr = map.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			List<JRssFeed> list = map.get(key);
			if (list == null || list.isEmpty())
				continue;
			Iterator<JRssFeed> itrFeeds = list.iterator();
			while(itrFeeds.hasNext()) {
				JRssFeed feed = itrFeeds.next();
				String articleSummaryText = feed.getArticleSummaryText();
				String fullArticleText = feed.getFullArticleText();
				if (articleSummaryText != null
						&& !articleSummaryText.trim().isEmpty()
						&& fullArticleText != null
						&& !fullArticleText.trim().isEmpty()) {
					articleSummaryText = articleSummaryText.replaceAll("\\n",
							" ").replaceAll(" +", " ");
					feed.setArticleSummaryText(articleSummaryText);
					fullArticleText = fullArticleText.replaceAll("\\n", " ")
							.replaceAll(" +", " ");
					feed.setFullArticleText(fullArticleText);
				} else {
					itrFeeds.remove();
				}
			}
		}
	}
}