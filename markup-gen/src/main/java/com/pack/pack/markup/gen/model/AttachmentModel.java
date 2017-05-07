package com.pack.pack.markup.gen.model;

import com.pack.pack.model.web.JPackAttachment;

/**
 * 
 * @author Saurav
 *
 */
public class AttachmentModel {

	private String mimeType;

	private String title;

	private String attachmentThumbnailUrl;

	private String attachmentUrl;

	private String description;

	private String youtube;

	private String youtubeVideoID;

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAttachmentThumbnailUrl() {
		return attachmentThumbnailUrl;
	}

	public void setAttachmentThumbnailUrl(String attachmentThumbnailUrl) {
		this.attachmentThumbnailUrl = attachmentThumbnailUrl;
	}

	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static AttachmentModel build(JPackAttachment attachment) {
		if (attachment == null)
			return null;
		AttachmentModel model = new AttachmentModel();
		model.setAttachmentThumbnailUrl(attachment.getAttachmentThumbnailUrl());
		model.setAttachmentUrl(attachment.getAttachmentUrl());
		model.setDescription(attachment.getDescription());
		model.setMimeType(attachment.getMimeType());
		model.setTitle(attachment.getTitle());
		String url = attachment.getAttachmentUrl();
		if (url.contains("youtube.com")) {
			model.setYoutube("true");
			try {
				String[] split = new java.net.URL(url).getQuery().split("v=");
				model.setYoutubeVideoID(split[1]);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			model.setYoutube("false");
		}
		return model;
	}

	public String getYoutubeVideoID() {
		return youtubeVideoID;
	}

	public void setYoutubeVideoID(String youtubeVideoID) {
		this.youtubeVideoID = youtubeVideoID;
	}

	public String getYoutube() {
		return youtube;
	}

	public void setYoutube(String youtube) {
		this.youtube = youtube;
	}
}