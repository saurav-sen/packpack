package com.squill.og.crawler.article.taxonomy;

import java.util.ArrayList;
import java.util.List;

public class AylienTaxonomyResponse {

	private String text;
	
	private String taxonomy;
	
	private String language;
	
	private List<AylienTaxonomyType> categories;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTaxonomy() {
		return taxonomy;
	}

	public void setTaxonomy(String taxonomy) {
		this.taxonomy = taxonomy;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<AylienTaxonomyType> getCategories() {
		if(categories == null) {
			categories = new ArrayList<AylienTaxonomyType>();
		}
		return categories;
	}

	public void setCategories(List<AylienTaxonomyType> categories) {
		this.categories = categories;
	}
}
