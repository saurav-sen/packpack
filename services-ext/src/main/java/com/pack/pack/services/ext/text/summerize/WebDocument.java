package com.pack.pack.services.ext.text.summerize;

import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kohlschutter.boilerpipe.sax.HTMLHighlighter;

/**
 * 
 * @author Saurav
 *
 */
public class WebDocument {

	private String title;

	private String description;

	private String articleSummary;

	private String articleFullText;

	private String imageUrl;

	private Document document;

	private boolean success = false;
	
	private String sourceUrl;
	
	private String inputUrl;
	
	private String extractedHtmlSnippet;
	
	private boolean compatible = true;

	WebDocument(String title, String description, String imageUrl,
			String sourceUrl, String inputUrl, Document document,
			String extractedHtmlSnippet) {
		this.title = title;
		this.description = description;
		this.imageUrl = imageUrl;
		this.sourceUrl = sourceUrl;
		this.inputUrl = inputUrl;
		this.document = document;
		this.extractedHtmlSnippet = extractedHtmlSnippet;
		if(extractedHtmlSnippet != null) {
			this.document = Jsoup.parse(extractedHtmlSnippet);
			setSuccess(true);
		} else {
			setSuccess(false);
		}
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
		Document document2 = Jsoup.parse(document.outerHtml());
		Elements elements = document2
				.getElementsByClass(HTMLHighlighter.BOILERPIPE_MARK_CSS_NON_ARTICLE_CLASS_NAME);
		if (elements != null && !elements.isEmpty()) {
			Iterator<Element> itr = elements.iterator();
			while (itr.hasNext()) {
				Element element = itr.next();
				element.remove();
			}
			return document2.outerHtml();
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
	
	public String getInputUrl() {
		return inputUrl;
	}

	public String getExtractedHtmlSnippet() {
		return extractedHtmlSnippet;
	}

	public boolean isCompatible() {
		return compatible;
	}

	public WebDocument setCompatible(boolean compatible) {
		this.compatible = compatible;
		return this;
	}
}