package com.pack.pack.model.web.dto;

/**
 * 
 * @author Saurav
 *
 */
public class TopicFollowDTO {

	private String userId;
	
	private String topicId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}
}