package com.pack.pack.model.web;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Saurav
 *
 */
public class JPackAttachment {
	
	private String id;

	private String attachmentUrl;
	
	private String attachmentThumbnailUrl;
	
	private String mimeType;
	
	private String attachmentType;
	
	private String title;
	
	private String description;
	
	private Float avgRating;
	
	private long creationTime;
	
	private int likes;
	
	private int views;
	
	private Map<String, JComment> comments;
	
	private JUser creator;
	
	private boolean uploadProgress;
	
	private boolean isExternalLink;
	
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

	public String getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
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

	public Map<String, JComment> getComments() {
		if(comments == null) {
			comments = new HashMap<String, JComment>();
		}
		return comments;
	}

	public void setComments(Map<String, JComment> comments) {
		this.comments = comments;
	}
	
	public void addOrEditComment(JComment comment) {
		if(comment == null || comment.getId() == null) {
			throw new RuntimeException("Comment Id can't be NULL reference");
		}
		comments.put(comment.getId(), comment);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JUser getCreator() {
		return creator;
	}

	public void setCreator(JUser creator) {
		this.creator = creator;
	}

	public boolean isUploadProgress() {
		return uploadProgress;
	}

	public void setUploadProgress(boolean uploadProgress) {
		this.uploadProgress = uploadProgress;
	}

	public boolean isExternalLink() {
		return isExternalLink;
	}

	public void setExternalLink(boolean isExternalLink) {
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