package com.pack.pack.services.redis;

import java.util.List;

import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.exception.PackPackException;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.TTL;

/**
 * 
 * @author Saurav
 *
 */
public interface INewsFeedService {

	/**
	 * 
	 * @param userId
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JRssFeed> getAllNewsRssFeeds(String userId, String pageLink) throws PackPackException;
	
	/**
	 * 
	 * @param userId
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JRssFeed> getAllSportsNewsRssFeeds(String userId, String pageLink) throws PackPackException;
	
	/**
	 * 
	 * @param userId
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JRssFeed> getAllScienceAndTechnologyNewsRssFeeds(String userId, String pageLink) throws PackPackException;
	
	/**
	 * 
	 * @param userId
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JRssFeed> getArticleNewsRssFeeds(String userId, String pageLink) throws PackPackException;

	/**
	 * 
	 * @param feeds
	 * @param ttl
	 * @param batchId
	 * @return
	 * @throws PackPackException
	 */
	public boolean upload(List<JRssFeed> feeds, TTL ttl, long batchId) throws PackPackException;
	
	/*
	 * 
	 */
	public void cleanupExpiredPageInfos();
}
