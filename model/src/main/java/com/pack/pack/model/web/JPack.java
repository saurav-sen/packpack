package com.pack.pack.model.web;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;

/**
 * 
 * @author Saurav
 *
 */
public class JPack {

	private String id;
	
	private String story;
	
	private String creatorName;
	
	private String title;
	
	private String rating;
	
	private DateTime creationTime;
	
	private int likes;
	
	private int views;
	
	private List<JComment> recentComments;
	
	private List<JPackAttachment> packAttachments;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public DateTime getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(DateTime creationTime) {
		this.creationTime = creationTime;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public List<JComment> getRecentComments() {
		if(recentComments == null) {
			recentComments = new ArrayList<JComment>(3);
		}
		return recentComments;
	}

	public void setRecentComments(List<JComment> recentComments) {
		this.recentComments = recentComments;
	}

	public List<JPackAttachment> getAttachments() {
		if(packAttachments == null) {
			packAttachments = new LinkedList<JPackAttachment>();
		}
		return packAttachments;
	}

	public void setAttachments(List<JPackAttachment> attachments) {
		this.packAttachments = attachments;
	}
}