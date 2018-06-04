package com.pack.pack.services.redis;

import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.exception.PackPackException;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.TTL;

/**
 * 
 * @author Saurav
 *
 */
public interface IRefreshmentFeedService {

	/**
	 * 
	 * @param userId
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JRssFeed> getAllRssFeeds(String userId, String pageLink) throws PackPackException;

	/**
	 * 
	 * @param feed
	 * @param ttl
	 * @param batchId
	 * @return
	 * @throws PackPackException
	 */
	public boolean upload(JRssFeed feed, TTL ttl, long batchId) throws PackPackException;
}