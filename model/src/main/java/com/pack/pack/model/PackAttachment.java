package com.pack.pack.model;


/**
 * 
 * @author Saurav
 *
 */
public class PackAttachment {

	private String attachmentUrl;
	
	private String attachmentThumbnailUrl;
	
	private String mimeType;
	
	private PackAttachmentType type;

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
}