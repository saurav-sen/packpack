package com.pack.pack.rss.services;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.model.RSSFeed;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.ShortenUrlInfo;
import com.pack.pack.rss.INewsFeedService;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.redis.RssFeedRepositoryService;
import com.pack.pack.services.redis.UrlShortener;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.ModelConverter;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.TTL;

/**
 * 
 * @author Saurav
 *
 */

public class NewsFeedServiceImpl implements INewsFeedService {

	private static final Logger LOG = LoggerFactory
			.getLogger(NewsFeedServiceImpl.class);

	@Override
	public Pagination<JRssFeed> getAllRssFeeds(String userId, String pageLink)
			throws PackPackException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean upload(List<JRssFeed> feeds, TTL ttl, long batchId)
			throws PackPackException {
		if (feeds == null || feeds.isEmpty())
			return false;
		try {
			RssFeedRepositoryService service = ServiceRegistry.INSTANCE
					.findService(RssFeedRepositoryService.class);
			for (JRssFeed feed : feeds) {
				upload(service, feed, ttl, batchId);
			}
			return true;
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61, e.getMessage());
		}
	}

	private boolean upload(RssFeedRepositoryService service, JRssFeed feed,
			TTL ttl, long batchId) throws PackPackException,
			NoSuchAlgorithmException {
		boolean checkFeedExists = service.checkFeedExists(feed);
		if (!checkFeedExists) {
			ShortenUrlInfo shortenUrlInfo = UrlShortener
					.calculateShortenShareableUrl(feed);
			feed.setShareableUrl(shortenUrlInfo.getUrl());
			feed.setBatchId(batchId);
			RSSFeed rssFeed = ModelConverter.convert(feed);
			service.uploadNewsFeed(rssFeed, ttl, batchId);
		}
		return !checkFeedExists;
	}
}
