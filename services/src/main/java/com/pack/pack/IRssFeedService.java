package com.pack.pack;

import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.TTL;
import com.pack.pack.services.exception.PackPackException;

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
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JRssFeed> getAllRssFeeds(String userId, String pageLink)
			throws PackPackException;
	
	/**
	 * 
	 * @param feed
	 * @param ttl
	 * @return
	 */
	public JRssFeed upload(JRssFeed feed, TTL ttl);
}