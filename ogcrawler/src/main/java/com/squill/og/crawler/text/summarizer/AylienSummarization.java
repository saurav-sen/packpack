package com.squill.og.crawler.text.summarizer;

import java.util.LinkedList;
import java.util.List;

public class AylienSummarization {

	private String text;

	private List<String> sentences;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getSentences() {
		if (sentences == null) {
			sentences = new LinkedList<String>();
		}
		return sentences;
	}

	public void setSentences(List<String> sentences) {
		this.sentences = sentences;
	}

	public String extractedAllSummary(boolean addNewLine) {
		StringBuilder builder = new StringBuilder();
		List<String> sentences2 = getSentences();
		for (String text : sentences2) {
			builder.append(text);
			builder.append("\n");
		}
		return builder.toString();
	}
}
