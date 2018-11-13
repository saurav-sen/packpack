package com.squill.og.crawler.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.services.ext.article.comparator.ArticleInfo;
import com.pack.pack.services.ext.article.comparator.TitleBasedArticleComparator;
import com.pack.pack.util.RssFeedUtil;
import com.pack.pack.util.SystemPropertyUtil;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.feed.web.model.TTL;
import com.squill.og.crawler.IWebCrawlable;
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.ISpiderSession;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.utils.ArchiveUtil;
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
	
	public static final String CONTENT_TYPE_HEADER = "Content-Type";
	public static final String APPLICATION_JSON = "application/json";
	
	protected static final String UTF_8 = "UTF-8";

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
				uploadBulk(map, session, webCrawlable);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		} else if (count == 0) {
			try {
				uploadBulk(map, session, webCrawlable);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		
		session.fireTopNotificationIfAny();
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
	
	private void uploadBulk(Map<String, List<JRssFeed>> map, ISpiderSession session, IWebCrawlable webCrawlable) throws Exception {
		if(!ServiceIdResolver.isUploadMode()) {
			LOG.debug("Not running in Upload Mode, ignoring upload");
			return;
		}
		//long batchId = session.getBatchId();
		if(LOG.isDebugEnabled()) {
			JRssFeeds rssFeeds = adapt(map);
			LOG.debug("Total feeds to be uploaded without backlog = " + rssFeeds.getFeeds().size());
		}
		dataCleanUp(map);
		ArchiveUtil.storeInArchive(map);
		//map = storeInArchive(map);
		JRssFeeds rssFeeds = adapt(map);
		LOG.info("Uploading news feeds: Total = " + rssFeeds.getFeeds().size());
		TTL ttl = new TTL();
		ttl.setTime((short) 1);
		ttl.setUnit(TimeUnit.DAYS);
		
		List<String> notificationMessages = new ArrayList<String>();
		{
			// [START] :: Check for duplicates to remove from upload list & also to generate notification.
			List<JRssFeed> list = rssFeeds.getFeeds();
			List<ArticleInfo> articles = new ArrayList<ArticleInfo>();
			for(JRssFeed l : list) {
				ArticleInfo article = new ArticleInfo(l.getOgTitle(), null);
				article.setReferenceObject(l);
				articles.add(article);
			}
			int len = articles.size() - 1;
			Object OBJECT = new Object();
			Map<Integer, Object> tmp = new HashMap<Integer, Object>();
			Map<Integer, List<Integer>> probableDuplicates = new TitleBasedArticleComparator().findProbableDuplicates(articles);
			if(probableDuplicates != null && !probableDuplicates.isEmpty()) {
				Iterator<Integer> itr = probableDuplicates.keySet().iterator();
				while(itr.hasNext()) {
					int index = itr.next();
					if(tmp.containsKey(index))
						continue;
					List<Integer> indices = probableDuplicates.get(index);
					if(indices != null && !indices.isEmpty()) {
						for(int i : indices) {
							if(i < 0 || i > len)
								continue;
							ArticleInfo article = articles.get(i);
							JRssFeed f = (JRssFeed)(article.getReferenceObject());
							if(f == null)
								continue;
							list.remove(f);
							tmp.put(i, OBJECT);
							if(notificationMessages.isEmpty()) {
								notificationMessages.add("NEWS: " + f.getOgTitle());
							}
							LOG.info("Duplicate Detected " + f.getOgTitle());
						}
					}
				}
			}
			
			articles.clear();
			articles = null;
			tmp.clear();
			tmp = null;
			probableDuplicates.clear();
			probableDuplicates = null;
			OBJECT = null;
			// [END] :: Check for duplicates to remove from upload list & also to generate notification.
		}
		
		HtmlUtil.generateNewsFeedsHtmlPages(rssFeeds);
		
		//RssFeedUtil.uploadNewsFeeds(rssFeeds, ttl, batchId, true);
		
		RssFeedUtil.uploadNewsFeeds(rssFeeds, ttl, System.currentTimeMillis(), true);
		
		if(SystemPropertyUtil.isUploadToProd()) {
			String json = JSONUtil.serialize(rssFeeds);
			HttpPost POST = new HttpPost("http://api.squill.in/api/feeds");
			POST.addHeader("Authorization", "f651b01535824fdc8a7f9fb231bdae38");
			POST.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
			
			HttpEntity jsonBody = new StringEntity(json, UTF_8);
			POST.setEntity(jsonBody);
			
			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(POST);
			if(response.getStatusLine().getStatusCode() == 200) {
				LOG.info("Successfully Uploaded to Production");
			} else {
				LOG.error("Failed to Upload to Production");
			}
		}
		
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
		
		if (notificationMessages.isEmpty()
				&& ((System.currentTimeMillis() - LocalData.INSTANCE
						.getLastNotifiedTimestamp()) > (2 * 60 * 60 * 1000))) {
			if (!feeds.isEmpty()) {
				JRssFeed f = feeds.get(Math.abs(new Random().nextInt())
						% feeds.size());
				notificationMessages.add(f.getOgTitle());
			}
		}
		
		if(session.hashMoreNotificationMessages()) {
			return;
		}
		
		for (String notificationMessage : notificationMessages) {
			NotificationUtil.broadcastNewRSSFeedUploadSummary(notificationMessage);
			LocalData.INSTANCE.setLastNotifiedTimestamp(System.currentTimeMillis());
		}
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