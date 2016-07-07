package com.pack.pack;

import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.Pagination;
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
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JRssFeed> getAllRssFeeds(String userId)
			throws PackPackException;
}