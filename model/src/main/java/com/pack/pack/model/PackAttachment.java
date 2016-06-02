package com.pack.pack.model;

import java.util.LinkedList;
import java.util.List;

import org.ektorp.support.CouchDbDocument;


/**
 * 
 * @author Saurav
 *
 */
public class PackAttachment extends CouchDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7415670854730959021L;

	private String attachmentUrl;
	
	private String attachmentThumbnailUrl;
	
	private String mimeType;
	
	private PackAttachmentType type;
	
	private String title;
	
	private float avgRating;
	
	private long creationTime;
	
	private int likes;
	
	private int views;
	
	private int comments;
	
	private List<Comment> recentComments;
	
	private String attachmentParentPackId;

	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public PackAttachmentType getType() {
		return type;
	}

	public void setType(PackAttachmentType type) {
		this.type = type;
	}

	public String getAttachmentThumbnailUrl() {
		return attachmentThumbnailUrl;
	}

	public void setAttachmentThumbnailUrl(String attachmentThumbnailUrl) {
		this.attachmentThumbnailUrl = attachmentThumbnailUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public float getAvgRating() {
		return avgRating;
	}

	public void setAvgRating(float avgRating) {
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

	public String getAttachmentParentPackId() {
		return attachmentParentPackId;
	}

	public void setAttachmentParentPackId(String attachmentParentPackId) {
		this.attachmentParentPackId = attachmentParentPackId;
	}
}