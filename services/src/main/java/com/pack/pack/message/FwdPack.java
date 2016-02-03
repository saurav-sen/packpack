package com.pack.pack.message;

import java.util.ArrayList;
import java.util.List;

import com.pack.pack.model.web.JPackAttachment;

/**
 * 
 * @author Saurav
 *
 */
public class FwdPack {

	private String packId;
	
	private String fromUserId;
	
	private String fromUserName;
	
	private String fromUserProfilePicUrl;
	
	private int views;
	
	private int likes;
	
	private int comments;
	
	private String message;
	
	private List<JPackAttachment> attachments;

	public List<JPackAttachment> getAttachments() {
		if(attachments == null) {
			attachments = new ArrayList<JPackAttachment>(5);
		}
		return attachments;
	}

	public void setAttachments(List<JPackAttachment> attachments) {
		this.attachments = attachments;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPackId() {
		return packId;
	}

	public void setPackId(String packId) {
		this.packId = packId;
	}

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

	public String getFromUserProfilePicUrl() {
		return fromUserProfilePicUrl;
	}

	public void setFromUserProfilePicUrl(String fromUserProfilePicUrl) {
		this.fromUserProfilePicUrl = fromUserProfilePicUrl;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}
}