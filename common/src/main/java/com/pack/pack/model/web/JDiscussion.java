package com.pack.pack.model.web;

import java.util.HashSet;
import java.util.Set;


/**
 * 
 * @author Saurav
 *
 */
public class JDiscussion {
	
	private String id;

	private String fromUserId;
	
	private String content;
	
	private long dateTime;
	
	private int likes;
	
	private Set<String> likeUsers;
	
	private String title;
	
	private String parentId;
	
	private String parentType;
	
	private String tag;
	
	private JUser fromUser;

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentType() {
		return parentType;
	}

	public void setParentType(String parentType) {
		this.parentType = parentType;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public JUser getFromUser() {
		return fromUser;
	}

	public void setFromUser(JUser fromUser) {
		this.fromUser = fromUser;
	}
}