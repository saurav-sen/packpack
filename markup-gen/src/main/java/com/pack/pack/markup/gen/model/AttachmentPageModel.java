package com.pack.pack.markup.gen.model;

import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.PackAttachmentType;

/**
 * 
 * @author Saurav
 *
 */
public class AttachmentPageModel extends AttachmentModel {

	private boolean showEmbedded;

	public boolean isShowEmbedded() {
		return showEmbedded;
	}

	public void setShowEmbedded(boolean showEmbedded) {
		this.showEmbedded = showEmbedded;
	}

	public static AttachmentPageModel build(JPackAttachment attachment) {
		if (attachment == null)
			return null;
		AttachmentPageModel model = new AttachmentPageModel();
		model.setAttachmentThumbnailUrl(attachment.getAttachmentThumbnailUrl());
		String mimeType = attachment.getMimeType();
		model.setMimeType(mimeType);
		String attachmentUrl = attachment.getAttachmentUrl();
		boolean showEmbedded = false;
		if (PackAttachmentType.VIDEO.name().equals(mimeType)
				&& attachmentUrl.contains("youtube.com/")) {
			showEmbedded = true;
			String[] split = attachmentUrl.split("v=");
			attachmentUrl = "https://www.youtube.com/embed/" + split[1];
		}
		model.setShowEmbedded(showEmbedded);
		model.setAttachmentUrl(attachmentUrl);
		model.setDescription(attachment.getDescription());
		model.setMimeType(attachment.getMimeType());
		model.setTitle(attachment.getTitle());
		return model;
	}
}