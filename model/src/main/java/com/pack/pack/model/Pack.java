package com.pack.pack.model;

import java.util.ArrayList;
import java.util.List;

import org.ektorp.support.CouchDbDocument;
import org.joda.time.DateTime;

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

	private String packImageId;
	
	private String story;
	
	private String creatorId;
	
	private List<String> tags;
	
	private String title;
	
	private Float avgRating;
	
	private DateTime creationTime;
	
	private int likes;
	
	private int views;
	
	private List<Comment> recentComments;

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

	public DateTime getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(DateTime creationTime) {
		this.creationTime = creationTime;
	}
	
	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getPackImageId() {
		return packImageId;
	}

	public void setPackImageId(String packImageId) {
		this.packImageId = packImageId;
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
}