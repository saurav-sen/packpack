package com.pack.pack.services.ext.text.summerize;

/**
 * 
 * @author Saurav
 *
 */
public interface ISentenceFinder {

	public String[] findSentences(String text);
	
	public String[] findWords(String sentence) throws Exception;
}
