package com.pack.pack;

import java.io.InputStream;

import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.JTopics;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public interface ITopicService {

	/**
	 * 
	 * @param topicId
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JPack> getAllPacks(String topicId, String pageLink)
			throws PackPackException;

	/**
	 * 
	 * @param topicId
	 * @return
	 * @throws PackPackException
	 */
	public JTopic getTopicById(String topicId) throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @param topicId
	 * @throws PackPackException
	 */
	public void followTopic(String userId, String topicId)
			throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JTopic> getUserFollowedTopics(String userId,
			String pageLink) throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JTopic> getAllTopicListing(String userId, String pageLink)
			throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @param category
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JTopic> getUserFollowedTopicsFilteredByCategory(
			String userId, String category, String pageLink)
			throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @param topicId
	 * @throws PackPackException
	 */
	public void neglectTopic(String userId, String topicId)
			throws PackPackException;

	/**
	 * 
	 * @param topic
	 * @param wallpaper
	 * @param wallpaperName
	 * @return
	 * @throws PackPackException
	 */
	public JTopic createNewTopic(JTopic topic, InputStream wallpaper,
			String wallpaperName) throws PackPackException;

	/**
	 * 
	 * @param categoryName
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JTopic> getAllTopicsByCategoryName(String categoryName,
			String pageLink) throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @return
	 * @throws PackPackException
	 */
	public JTopics getAllTopicsOwnedByUser(String userId)
			throws PackPackException;
	
	/**
	 * 
	 * @param topicId
	 * @param key
	 * @param value
	 * @param ownerId
	 * @return
	 * @throws PackPackException
	 */
	public JTopic editTopicSettings(String topicId, String key, String value,
			String ownerId) throws PackPackException;
}