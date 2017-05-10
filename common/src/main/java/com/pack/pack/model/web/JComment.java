package com.pack.pack.model.web;



/**
 * 
 * @author Saurav
 *
 */
public class JComment {
	
	private String id;

	private String fromUserId;
	
	private String fromUserName;
	
	private String comment;
	
	private long dateTime;
	
	private String fromUserDisplayName;
	
	private String fromUserProfilePictureUrl;

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}

	public String getFromUserDisplayName() {
		return fromUserDisplayName;
	}

	public void setFromUserDisplayName(String fromUserDisplayName) {
		this.fromUserDisplayName = fromUserDisplayName;
	}

	public String getFromUserProfilePictureUrl() {
		return fromUserProfilePictureUrl;
	}

	public void setFromUserProfilePictureUrl(String fromUserProfilePictureUrl) {
		this.fromUserProfilePictureUrl = fromUserProfilePictureUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}