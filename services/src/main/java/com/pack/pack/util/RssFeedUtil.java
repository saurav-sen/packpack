package com.pack.pack.util;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.IRssFeedService;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.JRssFeeds;
import com.pack.pack.model.web.TTL;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
public class RssFeedUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(RssFeedUtil.class);

	public static void uploadNewFeeds(JRssFeeds feeds) {
		LOG.info("Classification done. Uploading feeds to DB");
		try {
			List<JRssFeed> list = new LinkedList<JRssFeed>();
			List<JRssFeed> newFeeds = feeds.getFeeds();
			if (newFeeds != null && !newFeeds.isEmpty()) {
				TTL ttl = new TTL();
				ttl.setTime((short) 1);
				ttl.setUnit(TimeUnit.DAYS);
				IRssFeedService service = ServiceRegistry.INSTANCE
						.findCompositeService(IRssFeedService.class);
				for (JRssFeed feed : newFeeds) {
					JRssFeed f = service.upload(feed, ttl);
					list.add(f);
				}
				JRssFeeds result = new JRssFeeds();
				result.setFeeds(list);
			}
			LOG.info("Successfully uploaded feeds in DB");
		} catch (PackPackException e) {
			LOG.error(e.getErrorCode() + "::" + e.getMessage(), e);
		}
	}
}