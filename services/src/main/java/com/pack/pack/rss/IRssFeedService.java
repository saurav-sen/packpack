package com.pack.pack.rss;

import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.exception.PackPackException;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.TTL;

/**
 * 
 * @author Saurav
 *
 */
public interface IRssFeedService {

	/**
	 * 
	 * @param userId
	 * @param pageLink
	 * @param source
	 * @param apiVersion
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JRssFeed> getAllRssFeeds(String userId, String pageLink,
			String source, String apiVersion) throws PackPackException;

	/**
	 * 
	 * @param feed
	 * @param ttl
	 * @return -- non existent true else false
	 * @throws PackPackException
	 */
	public boolean upload(JRssFeed feed, TTL ttl) throws PackPackException;

	/**
	 * 
	 * @param topicId
	 * @return
	 */
	public JRssFeed generateRssFeedForTopic(String topicId);
}