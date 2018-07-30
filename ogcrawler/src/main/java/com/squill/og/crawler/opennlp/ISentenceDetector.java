package com.squill.og.crawler.opennlp;

public interface ISentenceDetector {

	public String[] detectSentences(String text) throws Exception;
	
	public String[] tokenize(String sentence) throws Exception;
}
