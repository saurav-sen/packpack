package com.pack.pack.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.ektorp.support.CouchDbDocument;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * 
 * @author Saurav
 *
 */
public class Pack extends CouchDbDocument {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3991470211498256682L;

	private String story;
	
	private String creatorId;
	
	private List<String> tags;
	
	private String title;
	
	private Float avgRating;
	
	private long creationTime;
	
	private int likes;
	
	private int views;
	
	private int comments;
	
	private List<Comment> recentComments;
	
	private List<PackAttachment> packAttachments;

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

	public List<String> getTags() {
		if(tags == null) {
			tags = new ArrayList<String>();
		}
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Float getAvgRating() {
		return avgRating;
	}

	public void setAvgRating(Float avgRating) {
		this.avgRating = avgRating;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
	
	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
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

	public List<Comment> getRecentComments() {
		if(recentComments == null) {
			recentComments = new ArrayList<Comment>(1);
		}
		return recentComments;
	}

	public void setRecentComments(List<Comment> recentComments) {
		this.recentComments = recentComments;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	public List<PackAttachment> getPackAttachments() {
		if(packAttachments == null) {
			packAttachments = new LinkedList<PackAttachment>();
		}
		return packAttachments;
	}

	public void setPackAttachments(List<PackAttachment> packAttachments) {
		this.packAttachments = packAttachments;
	}
}