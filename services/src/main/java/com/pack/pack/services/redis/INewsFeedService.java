package com.pack.pack.services.redis;

import java.util.List;
import java.util.Set;

import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.dto.FeedPublish;
import com.pack.pack.services.exception.PackPackException;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.feed.web.model.TTL;

/**
 * 
 * @author Saurav
 *
 */
public interface INewsFeedService {
	
	/**
	 * 
	 * @return
	 * @throws PackPackException
	 */
	public List<JRssFeed> getAllFeeds() throws PackPackException;
	
	/**
	 * 
	 * @return
	 * @throws PackPackException
	 */
	public List<JRssFeed> getAllOpinionsFeeds() throws PackPackException;
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws PackPackException
	 */
	public JRssFeed getFeedById(String id) throws PackPackException;
	
	/**
	 * 
	 * @param feedPublish
	 * @return
	 * @throws PackPackException
	 */
	public boolean upload(FeedPublish feedPublish) throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @param pageNo
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JRssFeed> getAllNewsRssFeeds(String userId, int pageNo) throws PackPackException;
	
	/**
	 * 
	 * @param userId
	 * @param pageNo
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JRssFeed> getAllSportsNewsRssFeeds(String userId, int pageNo) throws PackPackException;
	
	/**
	 * 
	 * @param userId
	 * @param pageNo
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JRssFeed> getAllOpinionRssFeeds(String userId, int pageNo) throws PackPackException;
	
	/**
	 * 
	 * @param userId
	 * @param pageNo
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JRssFeed> getAllScienceAndTechnologyNewsRssFeeds(String userId, int pageNo) throws PackPackException;
	
	/**
	 * 
	 * @param userId
	 * @param pageNo
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JRssFeed> getArticleNewsRssFeeds(String userId, int pageNo) throws PackPackException;

	/**
	 * 
	 * @param feeds
	 * @param ttl
	 * @param batchId
	 * @return -- List of keys/ids of the uploaded feeds
	 * @throws PackPackException
	 */
	public Set<String> upload(List<JRssFeed> feeds, TTL ttl, long batchId) throws PackPackException;
	
	/**
	 * 
	 * @param recentFeedIds
	 * @throws PackPackException
	 */
	public void storeRecentFeedIds(Set<String> recentFeedIds) throws PackPackException;
	
	/**
	 * 
	 * @return
	 * @throws PackPackException
	 */
	public Set<String> getRecentFeedIds() throws PackPackException;
	
	/**
	 * 
	 * @return
	 * @throws PackPackException
	 */
	public JRssFeeds getRecentAutoUploadFeeds() throws PackPackException;
	
	/**
	 * 
	 * @param pageNo
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JRssFeed> getUnprovisionUploadFeeds(int pageNo) throws PackPackException;
	
	/**
	 * 
	 * @param feeds
	 * @param ttl
	 * @param batchId
	 * @return
	 * @throws PackPackException
	 */
	public Set<String> storeUnprovisionedFeeds(List<JRssFeed> feeds, TTL ttl, long batchId) throws PackPackException;
	
	/**
	 * 
	 * @param feed
	 * @return
	 * @throws PackPackException
	 */
	public boolean publishUnprovisionedFeed(JRssFeed feed) throws PackPackException;
	
	/*
	 * 
	 */
	public void cleanupExpiredPageInfos();
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws PackPackException
	 */
	public JRssFeed delete(String id) throws PackPackException;
}
