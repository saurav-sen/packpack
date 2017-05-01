package com.pack.pack.model;

import org.ektorp.support.CouchDbDocument;

/**
 * 
 * @author Saurav
 *
 */
public class PackAttachmentStory extends CouchDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5685440417046281201L;
	
	private String content;
	
	private String parentAttachmentId;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getParentAttachmentId() {
		return parentAttachmentId;
	}

	public void setParentAttachmentId(String parentAttachmentId) {
		this.parentAttachmentId = parentAttachmentId;
	}
}
