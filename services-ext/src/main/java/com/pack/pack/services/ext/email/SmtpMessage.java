package com.pack.pack.services.ext.email;

/**
 * 
 * @author Saurav
 *
 */
public class SmtpMessage {

	private String receipentEmailId;

	private String subject;

	private String content;

	private boolean isHtml;

	public SmtpMessage(String receipentEmailId, String subject, String content,
			boolean isHtml) {
		this.receipentEmailId = receipentEmailId;
		this.subject = subject;
		this.content = content;
		this.isHtml = isHtml;
	}

	public String getReceipentEmailId() {
		return receipentEmailId;
	}

	public String getSubject() {
		return subject;
	}

	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public boolean isHtml() {
		return isHtml;
	}

	public void setHtml(boolean isHtml) {
		this.isHtml = isHtml;
	}
}
