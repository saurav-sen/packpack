package com.pack.pack.model;

import java.util.LinkedList;
import java.util.List;

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
	
	private List<Reply> replies;
	
	private String discussionParentTopicId;

	public List<Reply> getReplies() {
		if(replies == null) {
			replies = new LinkedList<Reply>();
		}
		return replies;
	}

	public void setReplies(List<Reply> replies) {
		this.replies = replies;
	}

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

	public String getDiscussionParentTopicId() {
		return discussionParentTopicId;
	}

	public void setDiscussionParentTopicId(String discussionParentTopicId) {
		this.discussionParentTopicId = discussionParentTopicId;
	}
}