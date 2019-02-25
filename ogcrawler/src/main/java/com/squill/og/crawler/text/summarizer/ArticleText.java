package com.squill.og.crawler.text.summarizer;

public class ArticleText {

	private String title;
	
	private String article;
	
	private String htmlSnippet;
	
	private boolean aylienBased = true;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArticle() {
		return article;
	}

	public void setArticle(String article) {
		this.article = article;
	}

	public String getHtmlSnippet() {
		return htmlSnippet;
	}

	public void setHtmlSnippet(String htmlSnippet) {
		this.htmlSnippet = htmlSnippet;
	}

	public boolean isAylienBased() {
		return aylienBased;
	}

	public void setAylienBased(boolean aylienBased) {
		this.aylienBased = aylienBased;
	}
}
