package com.pack.pack;

import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JTopic;
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
	 * @param topicId
	 * @throws PackPackException
	 */
	public void neglectTopic(String userId, String topicId)
			throws PackPackException;

	/**
	 * 
	 * @param topic
	 * @return
	 * @throws PackPackException
	 */
	public JTopic createNewTopic(JTopic topic) throws PackPackException;
}