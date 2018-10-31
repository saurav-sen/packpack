package com.pack.pack.services.ext.article.comparator;

import static com.pack.pack.services.ext.text.summerize.STOP_WORDS.STOP_WORDS;

import java.util.regex.Pattern;

import com.pack.pack.util.LanguageUtil;

/**
 * 
 * @author Saurav
 *
 */
public class ArticleInfo {

	private String originalText;

	private String textWithoutStopWords;

	private String[][] wordMatrix;

	private boolean matchFound;

	private String articleId;

	public ArticleInfo(String originalText, String articleId) {
		this.originalText = originalText;
		this.articleId = articleId;
	}

	public String getOriginalText() {
		return originalText;
	}

	public String getTextWithoutStopWords() {
		if (textWithoutStopWords == null) {
			textWithoutStopWords = filterStopWords(originalText);
		}
		return textWithoutStopWords;
	}

	public String[][] getWordMatrix() {
		if (wordMatrix == null) {
			String sentenceWithoutStopWords = getTextWithoutStopWords();
			if (sentenceWithoutStopWords == null
					|| sentenceWithoutStopWords.trim().isEmpty()) {
				wordMatrix = new String[0][0];
			} else {
				wordMatrix = LanguageUtil
						.prepareWordMatrix(sentenceWithoutStopWords);
			}
		}
		return wordMatrix;
	}

	private String filterStopWords(String text) {
		if (text == null || text.trim().isEmpty())
			return text;
		for (int i = 0; i < STOP_WORDS.length; i++) {
			if (text.contains("\\s+" + STOP_WORDS[i])) {
				text = text.replaceAll("\\s+" + Pattern.quote(STOP_WORDS[i]),
						"");
			}
		}
		return text;
	}

	public boolean isMatchFound() {
		return matchFound;
	}

	public void setMatchFound(boolean matchFound) {
		this.matchFound = matchFound;
	}

	public String getArticleId() {
		return articleId;
	}
}