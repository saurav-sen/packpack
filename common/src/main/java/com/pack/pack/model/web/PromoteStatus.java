package com.pack.pack.model.web;

/**
 * 
 * @author Saurav
 *
 */
public class PromoteStatus {

	private StatusType status;

	private String publicUrl;

	public StatusType getStatus() {
		return status;
	}

	public void setStatus(StatusType status) {
		this.status = status;
	}

	public String getPublicUrl() {
		return publicUrl;
	}

	public void setPublicUrl(String publicUrl) {
		this.publicUrl = publicUrl;
	}
}