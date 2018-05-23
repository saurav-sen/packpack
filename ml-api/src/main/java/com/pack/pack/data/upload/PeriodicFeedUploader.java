package com.pack.pack.data.upload;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.util.RssFeedUtil;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.feed.web.model.TTL;

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
		pool.scheduleAtFixedRate(new PeriodicFeedSelectionTask(), 0, 1,
				TimeUnit.DAYS);
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
			try {
				LOG.info("Uploading Selective Feeds (*** Periodic Feed Uploader ***)");
				JRssFeeds jRssFeeds = FeedUploadUtil.reloadSelectiveFeeds(false);
				if(jRssFeeds == null)
					return;
				TTL ttl = new TTL();
				ttl.setTime((short)2);
				ttl.setUnit(TimeUnit.DAYS);
				long batchId = System.currentTimeMillis();
				RssFeedUtil.uploadRefreshmentFeeds(jRssFeeds, ttl, batchId, false);
				LOG.info("Uploaded" + jRssFeeds.getFeeds().size()
						+ " Feeds (*** Periodic Feed Uploader ***)");
			} catch (Throwable e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
}