package com.pack.pack.model;

import java.util.LinkedList;
import java.util.List;


public class Reply {

	private String fromUserId;
	
	private String reply;
	
	private long dateTime;
	
	private List<Reply> replies;

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}

	public List<Reply> getReplies() {
		if(replies == null) {
			replies = new LinkedList<Reply>();
		}
		return replies;
	}

	public void setReplies(List<Reply> replies) {
		this.replies = replies;
	}
}
