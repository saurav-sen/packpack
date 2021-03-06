package com.pack.pack.data.upload;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.model.web.JRssFeeds;
import com.pack.pack.util.RssFeedUtil;

/**
 * 
 * @author Saurav
 *
 */
public class PeriodicFeedUploader {

	public static final PeriodicFeedUploader INSTANCE = new PeriodicFeedUploader();

	private ScheduledExecutorService pool;

	private static Logger LOG = LoggerFactory
			.getLogger(PeriodicFeedUploader.class);

	private PeriodicFeedUploader() {
	}

	public void start() {
		LOG.info("**********  Starting Periodic Feed Uploader  **********");
		pool = Executors.newSingleThreadScheduledExecutor();
		pool.scheduleAtFixedRate(new PeriodicFeedSelectionTask(), 0, 6,
				TimeUnit.HOURS);
		LOG.info("**********  Successfully Started Periodic Feed Uploader  **********");
	}

	public void stop() {
		if (pool != null) {
			pool.shutdownNow();
		}
	}

	private class PeriodicFeedSelectionTask implements Runnable {

		@Override
		public void run() {
			LOG.info("Uploading Selective Feeds (*** Periodic Feed Uploader ***)");
			JRssFeeds jRssFeeds = FeedUploadUtil.reloadSelectiveFeeds();
			RssFeedUtil.uploadNewFeeds(jRssFeeds);
			LOG.info("Uploaded" + jRssFeeds.getFeeds().size()
					+ " Feeds (*** Periodic Feed Uploader ***)");
		}
	}
}