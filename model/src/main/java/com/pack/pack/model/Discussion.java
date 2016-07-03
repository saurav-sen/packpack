package com.pack.pack.model;

import java.util.HashSet;
import java.util.Set;

import org.ektorp.support.CouchDbDocument;

/**
 * 
 * @author Saurav
 *
 */
public class Discussion extends CouchDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2676650559707603068L;
	
	private String startedByUserId;
	
	private String discussionTitle;
	
	private String content;
	
	private String parentEntityId;
	
	private String parentEntityType;
	
	private int likes;
	
	private Set<String> likeUsers;
	
	private long dateTime;
	
	private String tag;

	public String getStartedByUserId() {
		return startedByUserId;
	}

	public void setStartedByUserId(String startedByUserId) {
		this.startedByUserId = startedByUserId;
	}

	public String getDiscussionTitle() {
		return discussionTitle;
	}

	public void setDiscussionTitle(String discussionTitle) {
		this.discussionTitle = discussionTitle;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getParentEntityId() {
		return parentEntityId;
	}

	public void setParentEntityId(String parentEntityId) {
		this.parentEntityId = parentEntityId;
	}

	public String getParentEntityType() {
		return parentEntityType;
	}

	public void setParentEntityType(String parentEntityType) {
		this.parentEntityType = parentEntityType;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public Set<String> getLikeUsers() {
		if(likeUsers == null) {
			likeUsers = new HashSet<String>();
		}
		return likeUsers;
	}

	public void setLikeUsers(Set<String> likeUsers) {
		this.likeUsers = likeUsers;
	}

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}