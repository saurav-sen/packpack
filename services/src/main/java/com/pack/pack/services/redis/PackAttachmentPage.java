package com.pack.pack.services.redis;

import java.util.LinkedList;
import java.util.List;

import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.Pagination;

/**
 * 
 * @author Saurav
 *
 */
public class PackAttachmentPage {

	private String previousLink;
	private String nextLink;
	
	private List<JPackAttachment> attachments;

	public String getPreviousLink() {
		return previousLink;
	}

	public void setPreviousLink(String previousLink) {
		this.previousLink = previousLink;
	}

	public String getNextLink() {
		return nextLink;
	}

	public void setNextLink(String nextLink) {
		this.nextLink = nextLink;
	}

	public List<JPackAttachment> getAttachments() {
		if(attachments == null) {
			attachments = new LinkedList<JPackAttachment>();
		}
		return attachments;
	}

	public void setAttachments(List<JPackAttachment> attachments) {
		this.attachments = attachments;
	}
	
	public Pagination<JPackAttachment> convert() {
		Pagination<JPackAttachment> page = new Pagination<JPackAttachment>();
		page.setNextLink(nextLink);
		page.setPreviousLink(previousLink);
		page.setResult(getAttachments());
		return page;
	}
	
	public static PackAttachmentPage build(Pagination<JPackAttachment> page) {
		PackAttachmentPage r = new PackAttachmentPage();
		r.setNextLink(page.getNextLink());
		r.setPreviousLink(page.getPreviousLink());
		r.setAttachments(page.getResult());
		return r;
	}
}