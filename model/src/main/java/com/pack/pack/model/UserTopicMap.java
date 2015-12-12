package com.pack.pack.model;

import org.ektorp.support.CouchDbDocument;

/**
 * 
 * @author Saurav
 *
 */
public class UserTopicMap extends CouchDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6945068817455255731L;

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