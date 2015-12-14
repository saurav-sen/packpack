package com.pack.pack;

import com.pack.pack.model.web.JPacks;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.JTopics;
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
	 * @param pageNo
	 * @return
	 * @throws PackPackException
	 */
	public JPacks getAllPacks(String topicId, int pageNo) throws PackPackException;
	
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
	public void followTopic(String userId, String topicId) throws PackPackException;
	
	/**
	 * 
	 * @param userId
	 * @return
	 * @throws PackPackException
	 */
	public JTopics getUserFollowedTopics(String userId) throws PackPackException;
	
	/**
	 * 
	 * @param userId
	 * @param topicId
	 * @throws PackPackException
	 */
	public void neglectTopic(String userId, String topicId) throws PackPackException;
}