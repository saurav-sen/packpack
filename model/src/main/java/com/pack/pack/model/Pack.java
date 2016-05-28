package com.pack.pack.model;

import java.util.ArrayList;
import java.util.List;

import org.ektorp.support.CouchDbDocument;

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
	
	private String packParentTopicId;
	
	private List<Comment> comments;
	
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

	public String getPackParentTopicId() {
		return packParentTopicId;
	}

	public void setPackParentTopicId(String packParentTopicId) {
		this.packParentTopicId = packParentTopicId;
	}

	public List<Comment> getComments() {
		if(comments == null) {
			comments = new ArrayList<Comment>();
		}
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
}