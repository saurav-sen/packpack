package com.squill.broadcast.feed.upload;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.services.redis.INewsFeedService;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.RssFeedUtil;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.feed.web.model.TTL;
import com.squill.og.crawler.internal.utils.HtmlUtil;

public class NewsFeedsIntegrityCheckerTask implements Runnable {
	
	private static Logger $LOG = LoggerFactory
			.getLogger(NewsFeedsIntegrityCheckerTask.class);

	@Override
	public void run() {
		try {
			$LOG.info("Cleaning up expired pages information");
			INewsFeedService service = ServiceRegistry.INSTANCE
					.findCompositeService(INewsFeedService.class);
			service.cleanupExpiredPageInfos();
			$LOG.info("Successfully Cleaned up expired pages information");
			$LOG.info("Starting to upload & generate HTML pages for pending feed in the last batch execution if Any.");
			List<JRssFeed> feeds = service.getAllFeeds();
			JRssFeeds rssFeeds = new JRssFeeds();
			int count = 0;
			for(JRssFeed feed : feeds) {
				String shareableUrl = feed.getShareableUrl();
				if(shareableUrl == null || shareableUrl.trim().isEmpty()) {
					rssFeeds.getFeeds().add(feed);
					HtmlUtil.generateNewsFeedsSharedHtmlPage(feed);
					count++;
				} else if(!HtmlUtil.isSharedPageFileExists(feed)) {
					rssFeeds.getFeeds().add(feed);
					HtmlUtil.generateNewsFeedsSharedHtmlPage(feed);
					count++;
				}
				String squillUrl = feed.getSquillUrl();
				if(squillUrl == null || squillUrl.trim().isEmpty()) {
					rssFeeds.getFeeds().add(feed);
					HtmlUtil.generateNewsFeedsFullHtmlPage(feed);
					count++;
				} else if(!HtmlUtil.isFullPageFileExists(feed)) {
					rssFeeds.getFeeds().add(feed);
					HtmlUtil.generateNewsFeedsFullHtmlPage(feed);
					count++;
				}
			}
			if ($LOG.isDebugEnabled()) {
				if (count > 0) {
					$LOG.debug("Found " + count + " to be uploaded back again");
				} else {
					$LOG.debug("No pending feed(s) for upload");
				}
			}
			
			TTL ttl = new TTL();
			ttl.setTime((short) 1);
			ttl.setUnit(TimeUnit.DAYS);
			long batchId = System.currentTimeMillis();
			RssFeedUtil.uploadNewsFeeds(rssFeeds, ttl, batchId, true);
			$LOG.info("Done Pending Feed Upload");
		} catch (Exception e) {
			$LOG.error("Failed during cleaning up expired pages information");
			$LOG.error(e.getMessage(), e);
		}
	}
}
