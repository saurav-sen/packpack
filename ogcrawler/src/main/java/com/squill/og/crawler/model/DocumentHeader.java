package com.squill.og.crawler.model;

import java.util.ArrayList;
import java.util.List;

import com.pack.pack.util.LanguageUtil;

public class DocumentHeader {

	private String title;
	
	private List<String> words;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public List<String> getWords() {
		if(words == null) {
			try {
				if(title != null) {
					 words = LanguageUtil.getWords(title);
				}
			} catch (Exception e) {
				words = new ArrayList<String>(1);
			}
		}
		return words;
	}
	
	public void setWords(List<String> words) {
		this.words = words;
	}
}
