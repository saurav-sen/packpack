package com.squill.og.crawler.internal.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.model.web.FeedClassifier;
import com.squill.og.crawler.model.web.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
public class FeedClassifierUtil {

	private static final Logger LOG = LoggerFactory
			.getLogger(FeedClassifierUtil.class);

	private FeedClassifierUtil() {
	}

	public static FeedClassifier classify(JRssFeed feed) {
		String classifiedType = feed.getPreClassifiedType();
		if (classifiedType != null) {
			try {
				FeedClassifier classifier = FeedClassifier
						.valueOf(classifiedType.toUpperCase());
				if (classifier != null) {
					return classifier;
				}
			} catch (Exception e) {
				LOG.info(e.getMessage(), e);
			}

		}
		// TODO -- Use ML api here to classify and upload in knowledge base.
		return null;
	}
}