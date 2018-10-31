package com.pack.pack.services.ext.article.comparator;

import com.pack.pack.util.LanguageUtil;
import com.pack.pack.util.LanguageUtil.Similarity;

/**
 * 
 * @author Saurav
 *
 */
public class ArticleInfoMatcher {

	boolean isEQUAL(ArticleInfo src, ArticleInfo tgt) {
		Similarity similarity = LanguageUtil.calculateSimilarity(
				src.getWordMatrix(), tgt.getTextWithoutStopWords());
		if (similarity == Similarity.HIGH) {
			return true;
		}
		return false;
	}
}