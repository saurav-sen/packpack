package com.pack.pack.services.redis.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.redis.IBookmarkTempStoreService;
import com.pack.pack.services.redis.RedisCacheService;
import com.pack.pack.services.registry.ServiceRegistry;
import com.squill.feed.web.model.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class BookmarkTempStoreServiceImpl implements IBookmarkTempStoreService {

	private static final Logger $_LOG = LoggerFactory
			.getLogger(BookmarkTempStoreServiceImpl.class);

	private static final String KEY_PREFIX = "BK_";

	@Override
	public JRssFeed getStoredBookmarkIfAny(String link) {
		if (link == null || link.trim().isEmpty())
			return null;
		try {
			$_LOG.debug("Retrieving Bookmark information if any");
			String key = KEY_PREFIX + link;
			RedisCacheService service = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			JRssFeed fromCache = service.getFromCache(key, JRssFeed.class);
			if (fromCache != null) {
				$_LOG.debug("Found Bookmark information for link @ " + link);
			}
			return fromCache;
		} catch (PackPackException e) {
			$_LOG.error("Error Retrieving Bookmark {0}", e.getMessage(), e);
			return null;
		}
	}

	@Override
	public void storeNewBookmark(String link, JRssFeed feed) {
		try {
			$_LOG.debug("Storing Bookmark temporarily");
			$_LOG.debug("Saving (Bookmark) " + feed.getOgType() + " Feed @ "
					+ feed.getOgUrl());
			$_LOG.info("Uploading Feed (Bookmark) Titled :: "
					+ feed.getOgTitle());
			long ttlSeconds = 15 * 60; // 15 minutes TTL
			String key = KEY_PREFIX + link;
			RedisCacheService service = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			service.addToCache(key, feed, ttlSeconds);
			$_LOG.info("Successfully stored Bookmark Feed @ " + key);
		} catch (PackPackException e) {
			$_LOG.error("Error Storing Bookmark {0}", e.getMessage(), e);
		}
	}
}
