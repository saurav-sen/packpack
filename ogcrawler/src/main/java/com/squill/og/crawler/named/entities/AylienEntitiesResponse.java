package com.squill.og.crawler.named.entities;

public class AylienEntitiesResponse {

	private String text;
	
	private String language;
	
	private AylienEntities entities;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public AylienEntities getEntities() {
		return entities;
	}

	public void setEntities(AylienEntities entities) {
		this.entities = entities;
	}
}
