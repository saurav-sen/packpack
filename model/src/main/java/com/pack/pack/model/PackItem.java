package com.pack.pack.model;

import java.util.LinkedList;
import java.util.List;

import org.ektorp.support.CouchDbDocument;

/**
 * 
 * @author Saurav
 *
 */
public class PackItem extends CouchDbDocument {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6832764658895056278L;

	private String title;
	
	private Float avgRating;
	
	private long creationTime;
	
	private int likes;
	
	private int views;
	
	private int comments;
	
	private List<Comment> recentComments;
	
	private String parentPackId;
	
	private PackAttachment packAttachment;

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

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	public List<Comment> getRecentComments() {
		if(recentComments == null) {
			recentComments = new LinkedList<Comment>();
		}
		return recentComments;
	}

	public void setRecentComments(List<Comment> recentComments) {
		this.recentComments = recentComments;
	}

	public String getParentPackId() {
		return parentPackId;
	}

	public void setParentPackId(String parentPackId) {
		this.parentPackId = parentPackId;
	}

	public PackAttachment getPackAttachment() {
		return packAttachment;
	}

	public void setPackAttachment(PackAttachment packAttachment) {
		this.packAttachment = packAttachment;
	}
}