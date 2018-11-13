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
	
	private Object referenceObject;

	public ArticleInfo(String originalText, String articleId) {
		this.originalText = originalText.replaceAll(Pattern.quote("- Times of India"), "").toLowerCase().trim();
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

	public Object getReferenceObject() {
		return referenceObject;
	}

	public void setReferenceObject(Object referenceObject) {
		this.referenceObject = referenceObject;
	}
	
	@Override
	public String toString() {
		return originalText;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(obj instanceof ArticleInfo) {
			ArticleInfo articleInfo = (ArticleInfo)obj;
			Object ref1 = articleInfo.getReferenceObject();
			if(referenceObject != null && ref1 != null) {
				return referenceObject.equals(ref1);
			}
			if(referenceObject == null && ref1 == null) {
				return this.equals(obj);
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		StringBuilder code = new StringBuilder(this.getClass().getName());
		if(referenceObject != null) {
			code.append("_");
			code.append(referenceObject.getClass().getName());
			code.append("_");
			code.append(referenceObject.toString());
		} else {
			code.append("_");
			code.append(originalText);
		}
		return code.toString().hashCode();
	}
}