package com.pack.pack.model.web;

/**
 * 
 * @author Saurav
 *
 */
public class JPackAttachment {

	private String attachmentUrl;
	
	private String attachmentThumbnailUrl;
	
	private String mimeType;
	
	private String attachmentType;

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
}