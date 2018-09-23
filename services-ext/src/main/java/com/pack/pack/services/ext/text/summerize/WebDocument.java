package com.pack.pack.services.ext.text.summerize;

import org.jsoup.nodes.Document;

public class WebDocument {

	private String title;

	private String description;

	private String articleSummary;

	private String articleFullText;

	private String imageUrl;

	private Document document;

	private boolean success = false;
	
	private String sourceUrl;

	WebDocument(String title, String description, String imageUrl, String sourceUrl,
			Document document) {
		this.title = title;
		this.description = description;
		this.imageUrl = imageUrl;
		this.sourceUrl = sourceUrl;
		this.document = document;
		setSuccess(false);
	}

	public String getTitle() {
		if(title == null || title.trim().isEmpty()) {
			return null;
		}
		return title;
	}

	public String getFilteredHtml() {
		if (document == null) {
			return "";
		}
		return document.outerHtml();
	}

	public boolean isSuccess() {
		return success;
	}

	public WebDocument setSuccess(boolean success) {
		this.success = success;
		return this;
	}

	public String getArticleSummary() {
		return articleSummary;
	}

	public void setArticleSummary(String articleSummary) {
		this.articleSummary = articleSummary;
	}

	public String getArticleFullText() {
		return articleFullText;
	}

	public void setArticleFullText(String articleFullText) {
		this.articleFullText = articleFullText;
	}

	public String getImageUrl() {
		if(imageUrl == null || imageUrl.trim().isEmpty()) {
			return null;
		}
		return imageUrl;
	}

	public String getDescription() {
		if(description == null || description.trim().isEmpty()) {
			return null;
		}
		return description;
	}

	Document getDocument() {
		return document;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}
}