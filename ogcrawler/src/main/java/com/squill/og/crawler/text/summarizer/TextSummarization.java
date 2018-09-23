package com.squill.og.crawler.text.summarizer;

import java.util.LinkedList;
import java.util.List;

public class TextSummarization {

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
			int len = text.length();
			int indexOf = text.trim().indexOf(":");
			if(indexOf > 0 && (len / indexOf) >= 8) {
				if(indexOf < len-2) {
					text = text.substring(indexOf+1);
				}
			}
			builder.append(text.trim());
			if(!text.trim().endsWith(".")) {
				builder.append(".");
			}
			builder.append("\n");
		}
		return builder.toString();
	}
}
