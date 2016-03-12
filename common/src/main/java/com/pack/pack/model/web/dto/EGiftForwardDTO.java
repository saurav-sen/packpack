package com.pack.pack.model.web.dto;

/**
 * 
 * @author Saurav
 *
 */
public class EGiftForwardDTO extends ForwardDTO {

	private String title;
	
	private String message;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}