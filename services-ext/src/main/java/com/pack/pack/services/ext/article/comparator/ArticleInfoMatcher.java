package com.pack.pack.services.ext.article.comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.util.LanguageUtil;
import com.pack.pack.util.LanguageUtil.Similarity;

/**
 * 
 * @author Saurav
 *
 */
public class ArticleInfoMatcher {

	private static final Logger $LOG = LoggerFactory
			.getLogger(ArticleInfoMatcher.class);

	boolean isEQUAL(ArticleInfo src, ArticleInfo tgt) {
		Similarity similarity = LanguageUtil.calculateSimilarity(
				src.getWordMatrix(), tgt.getTextWithoutStopWords());
		if (similarity == Similarity.HIGH || similarity == Similarity.MEDIUM) {
			$LOG.debug("ARTICLE_DUPLICATE_FOUND \"" + src.getOriginalText()
					+ "\" & \"" + tgt.getOriginalText() + "\""
					+ " similarity level = " + similarity.name());
			return true;
		} else {
			similarity = LanguageUtil.calculateSimilarity(tgt.getWordMatrix(),
					src.getTextWithoutStopWords());
			if (similarity == Similarity.HIGH
					|| similarity == Similarity.MEDIUM) {
				$LOG.debug("ARTICLE_DUPLICATE_FOUND \"" + src.getOriginalText()
						+ "\" & \"" + tgt.getOriginalText() + "\""
						+ " similarity level = " + similarity.name());
				return true;
			}
		}
		return false;
	}
}