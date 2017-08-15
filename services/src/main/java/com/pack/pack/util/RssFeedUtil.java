package com.pack.pack.util;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.model.RSSFeed;
import com.pack.pack.model.RssFeedType;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.JRssFeedType;
import com.pack.pack.model.web.JRssFeeds;
import com.pack.pack.model.web.TTL;
import com.pack.pack.rss.IRssFeedService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.rabbitmq.MessagePublisher;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
public class RssFeedUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(RssFeedUtil.class);

	public static void uploadNewFeeds(JRssFeeds feeds, boolean sendNotification) {
		LOG.info("Classification done. Uploading feeds to DB");
		try {
			List<JRssFeed> list = new LinkedList<JRssFeed>();
			List<JRssFeed> newFeeds = feeds.getFeeds();
			if (newFeeds != null && !newFeeds.isEmpty()) {
				TTL ttl = new TTL();
				ttl.setTime((short) 2);
				ttl.setUnit(TimeUnit.DAYS);
				IRssFeedService service = ServiceRegistry.INSTANCE
						.findCompositeService(IRssFeedService.class);
				for (JRssFeed feed : newFeeds) {
					boolean f = service.upload(feed, ttl);
					if(f) {
						list.add(feed);
					}
				}
				/*JRssFeeds result = new JRssFeeds();
				result.setFeeds(list);*/
				MessagePublisher messagePublisher = ServiceRegistry.INSTANCE
						.findService(MessagePublisher.class);
				if(!sendNotification)
					return;
				for (JRssFeed l : list) {
					messagePublisher.broadcastNewRSSFeedUpload(l, null, sendNotification);
				}
			}
			LOG.info("Successfully uploaded feeds in DB");
		} catch (PackPackException e) {
			LOG.error(e.getErrorCode() + "::" + e.getMessage(), e);
		}
	}
	
	private static final String resolvePrefix(RSSFeed feed) {
		if(feed.getFeedType() == null) {
			return "Feeds_";
		} else if(RssFeedType.REFRESHMENT.name().equalsIgnoreCase(feed.getFeedType())) {
			return "Feeds_";
		} else if(RssFeedType.NEWS.name().equalsIgnoreCase(feed.getFeedType())) {
			return JRssFeedType.NEWS.name() + "_";
		}
		return "Feeds_";
	}
	
	private static final String resolvePrefix(JRssFeed feed) {
		if(feed.getFeedType() == null) {
			return "Feeds_";
		} else if(RssFeedType.REFRESHMENT.name().equalsIgnoreCase(feed.getFeedType())) {
			return "Feeds_";
		} else if(RssFeedType.NEWS.name().equalsIgnoreCase(feed.getFeedType())) {
			return JRssFeedType.NEWS.name() + "_";
		}
		return "Feeds_";
	}
	
	public static final String generateUploadKey(RSSFeed feed) {
		//return "Feeds_" + String.valueOf(feed.getOgUrl().hashCode());
		String key = feed.getOgImage();
		if(key == null) {
			key = feed.getOgUrl() != null ? feed.getOgUrl() : (feed.getHrefSource() != null ? feed.getHrefSource() : feed.getOgTitle());
		}
		return resolvePrefix(feed) + String.valueOf(key.hashCode());
	}
	
	public static final String generateUploadKey(JRssFeed feed) {
		String key = feed.getOgImage();
		if(key == null) {
			key = feed.getOgUrl() != null ? feed.getOgUrl() : (feed.getHrefSource() != null ? feed.getHrefSource() : feed.getOgTitle());
		}
		return resolvePrefix(feed) + String.valueOf(key.hashCode());
	}
}