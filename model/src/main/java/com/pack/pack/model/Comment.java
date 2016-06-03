package com.pack.pack.model;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class Comment {

	private String fromUser;
	
	private String comment;
	
	private long dateTime;
	
	private List<Comment> replies;
	
	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
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

	public List<Comment> getReplies() {
		if(replies == null) {
			replies = new LinkedList<Comment>();
		}
		return replies;
	}

	public void setReplies(List<Comment> replies) {
		this.replies = replies;
	}
}