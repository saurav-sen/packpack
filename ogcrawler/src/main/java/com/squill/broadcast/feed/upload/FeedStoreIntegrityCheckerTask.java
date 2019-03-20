package com.squill.broadcast.feed.upload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.services.redis.INewsFeedService;
import com.pack.pack.services.registry.ServiceRegistry;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.utils.HtmlUtil;

public class FeedStoreIntegrityCheckerTask implements Runnable {

	private static Logger $LOG = LoggerFactory
			.getLogger(FeedStoreIntegrityCheckerTask.class);

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
			final Map<String, Object> allRequiredHtmlPageIdList = new HashMap<String, Object>();
			final Object OBJ = new Object();
			for (JRssFeed feed : feeds) {
				if (!JRssFeedType.REFRESHMENT.name().equals(feed.getFeedType())) {
					String shareableUrl = feed.getShareableUrl();
					if (shareableUrl == null || shareableUrl.trim().isEmpty()) {
						rssFeeds.getFeeds().add(feed);
						HtmlUtil.generateNewsFeedsSharedHtmlPage(feed);
						count++;
					} else if (!HtmlUtil.isSharedPageFileExists(feed)) {
						rssFeeds.getFeeds().add(feed);
						HtmlUtil.generateNewsFeedsSharedHtmlPage(feed);
						count++;
					}
					String squillUrl = feed.getSquillUrl();
					if (squillUrl == null || squillUrl.trim().isEmpty()) {
						rssFeeds.getFeeds().add(feed);
						HtmlUtil.generateNewsFeedsFullHtmlPage(feed);
						count++;
					} else if (!HtmlUtil.isFullPageFileExists(feed)) {
						rssFeeds.getFeeds().add(feed);
						HtmlUtil.generateNewsFeedsFullHtmlPage(feed);
						count++;
					}
				}
				String htmlPageId = HtmlUtil.resolveHtmlPageId(feed);
				if (htmlPageId != null) {
					allRequiredHtmlPageIdList.put(htmlPageId, OBJ);
				}
			}
			if (count > 0) {
				$LOG.info("Found " + count + " to be uploaded back again");
			} else {
				$LOG.info("No pending feed(s) for upload");
			}

			/*TTL ttl = new TTL();
			ttl.setTime((short) 1);
			ttl.setUnit(TimeUnit.DAYS);
			long batchId = System.currentTimeMillis();
			RssFeedUtil.uploadNewsFeeds(rssFeeds, ttl, batchId, true);*/

			/*$LOG.info("Cleaning Up Un-Used files from HTML shared pages location");

			String htmlFolder = SystemPropertyUtil
					.getDefaultArchiveHtmlFolder();
			if (htmlFolder != null) {
				Files.list(Paths.get(htmlFolder))
						.filter(p -> {
							if (Files.isDirectory(p)) {
								return false;
							}
							if (Files.isSymbolicLink(p)) {
								return false;
							}
							String fileName = p.getFileName().toString();
							if (allRequiredHtmlPageIdList.get(fileName) != null) {
								return false;
							}
							if (!fileName.endsWith(".jpg")
									&& !fileName.endsWith(".png")
									&& !fileName.endsWith(".jpeg")
									&& !fileName.endsWith(".gif")
									&& !fileName.endsWith(".css")
									&& !fileName.endsWith(".html")
									&& !fileName.endsWith(".htm")
									&& !fileName.endsWith(".js")
									&& !fileName.endsWith(".json")
									&& !fileName.endsWith(".xml")
									&& !fileName.endsWith(".txt")
									&& !fileName.endsWith(".doc")
									&& !fileName.endsWith(".docx")
									&& !fileName.endsWith(".ppt")
									&& !fileName.endsWith(".pptx")
									&& !fileName.endsWith(".xls")
									&& !fileName.endsWith(".xlsx")
									&& !fileName.endsWith(".odt")) {
								return true;
							}
							return false;
						}).forEach(DeleteCommand::delete);
			}

			$LOG.info("Done Pending Feed Upload");*/
		} catch (Exception e) {
			$LOG.error("Failed during cleaning up expired pages information");
			$LOG.error(e.getMessage(), e);
		}
	}

	private static class DeleteCommand {

		public static void delete(Path p) {
			try {
				$LOG.debug("Trying to delete <" + p.toString() + ">");
				Files.delete(p);
			} catch (IOException e) {
				$LOG.error(e.getMessage(), e);
			}
		}
	}
}
