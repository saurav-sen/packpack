package com.squill.broadcast.feed.upload;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.util.RssFeedUtil;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.feed.web.model.TTL;

public class PeriodicFeedUploadTask implements Runnable {
	
	private static Logger LOG = LoggerFactory
			.getLogger(PeriodicFeedUploadTask.class);

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
