package com.pack.pack.model;

import org.ektorp.support.CouchDbDocument;

/**
 * 
 * @author Saurav
 *
 */
public class TopicPackMap extends CouchDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5826202829616917377L;

	private String topicId;
	
	private String packId;
	
	private long dateTime;

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getPackId() {
		return packId;
	}

	public void setPackId(String packId) {
		this.packId = packId;
	}

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}
}
