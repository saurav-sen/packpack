package com.squill.broadcast.feed.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.services.redis.INewsFeedService;
import com.pack.pack.services.registry.ServiceRegistry;

public class PageInfoCleanUpTask implements Runnable {
	
	private static Logger LOG = LoggerFactory
			.getLogger(PageInfoCleanUpTask.class);

	@Override
	public void run() {
		try {
			LOG.info("Cleaning up expired pages information");
			INewsFeedService service = ServiceRegistry.INSTANCE
					.findCompositeService(INewsFeedService.class);
			service.cleanupExpiredPageInfos();
			LOG.info("Successfully Cleaned up expired pages information");
		} catch (Exception e) {
			LOG.error("Failed during cleaning up expired pages information");
			LOG.error(e.getMessage(), e);
		}
	}
}
