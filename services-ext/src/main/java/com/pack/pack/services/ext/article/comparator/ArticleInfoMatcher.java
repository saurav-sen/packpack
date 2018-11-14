package com.pack.pack.services.ext.article.comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.util.LanguageUtil;
import com.pack.pack.util.LanguageUtil.Similarity;

import static com.pack.pack.util.LanguageUtil.Similarity.HIGH;
import static com.pack.pack.util.LanguageUtil.Similarity.MEDIUM;
import static com.pack.pack.util.LanguageUtil.Similarity.DERIVED_HIGH;
import static com.pack.pack.util.LanguageUtil.Similarity.DERIVED_MEDIUM;

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
				src.getWordMatrix(), tgt.getTextWithoutStopWords(),
				tgt.getRawText(), src.getRawText());
		if (similarity == HIGH || similarity == MEDIUM
				|| similarity == DERIVED_HIGH || similarity == DERIVED_MEDIUM) {
			$LOG.debug("ARTICLE_DUPLICATE_FOUND \"" + src.getRawText()
					+ "\" & \"" + tgt.getRawText() + "\""
					+ " similarity level = " + similarity.name());
			return true;
		} else {
			similarity = LanguageUtil.calculateSimilarity(tgt.getWordMatrix(),
					src.getTextWithoutStopWords(), src.getRawText(),
					tgt.getRawText());
			if (similarity == HIGH || similarity == MEDIUM
					|| similarity == DERIVED_HIGH
					|| similarity == DERIVED_MEDIUM) {
				$LOG.debug("ARTICLE_DUPLICATE_FOUND \"" + src.getRawText()
						+ "\" & \"" + tgt.getRawText() + "\""
						+ " similarity level = " + similarity.name());
				return true;
			}
		}
		$LOG.trace("ARTICLE_DUPLICATE_NOT_FOUND \"" + src.getOriginalText()
				+ "\" & \"" + tgt.getOriginalText() + "\""
				+ " similarity level = " + similarity.name());
		return false;
	}
}