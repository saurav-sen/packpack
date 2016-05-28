package com.pack.pack.model.web;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class JPackAttachments {

	private List<JPackAttachment> attachments;

	public List<JPackAttachment> getAttachments() {
		if(attachments == null) {
			attachments = new ArrayList<JPackAttachment>();
		}
		return attachments;
	}

	public void setAttachments(List<JPackAttachment> attachments) {
		this.attachments = attachments;
	}
}
