package com.pack.pack.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
	private AttachmentType type;
	
	private String title;
	
	private String creatorId;
	
	private String description;
	
	private float avgRating;
	
	private long creationTime;
	
	private int likes;
	
	private int views;
	
	private int comments;
	
	private Map<String, Comment> recentComments;
	
	private String attachmentParentPackId;
	
	private String isExternalLink;
	
	private Map<String, String> extraMetaData;
	
	private String storyId;

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

	public AttachmentType getType() {
		return type;
	}

	public void setType(AttachmentType type) {
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

	public Collection<Comment> getRecentComments() {
		if(recentComments == null) {
			recentComments = new HashMap<String, Comment>();
		}
		return Collections.unmodifiableCollection(recentComments.values());
	}

	public void setRecentComments(List<Comment> comments) {
		for(Comment comment : comments) {
			recentComments.put(comment.getId(), comment);
		}
	}

	public String getAttachmentParentPackId() {
		return attachmentParentPackId;
	}

	public void setAttachmentParentPackId(String attachmentParentPackId) {
		this.attachmentParentPackId = attachmentParentPackId;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIsExternalLink() {
		return isExternalLink;
	}

	public void setIsExternalLink(String isExternalLink) {
		this.isExternalLink = isExternalLink;
	}
	
	public Map<String, String> getExtraMetaData() {
		if(extraMetaData == null) {
			extraMetaData = new HashMap<String, String>();
		}
		return extraMetaData;
	}

	public void setExtraMetaData(Map<String, String> extraMetaData) {
		this.extraMetaData = extraMetaData;
	}

	public String getStoryId() {
		return storyId;
	}

	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}
}