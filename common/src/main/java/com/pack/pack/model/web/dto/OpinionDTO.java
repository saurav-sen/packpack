package com.pack.pack.model.web.dto;

/**
 * 
 * @author Saurav
 *
 */
public class OpinionDTO {
	
	private String title;
	
	private String content;
	
	private String imageUrl;
	
	private String url;
	
	private String author;
	
	private String type;
	
	private boolean noImage = false;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isNoImage() {
		return noImage;
	}

	public void setNoImage(boolean noImage) {
		this.noImage = noImage;
	}
}
