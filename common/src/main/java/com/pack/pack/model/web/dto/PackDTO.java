package com.pack.pack.model.web.dto;

/**
 * 
 * @author Saurav
 *
 */
public class PackDTO {

	private String userId;
	
	private String title;
	
	private String story;
	
	private String topicId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}
}