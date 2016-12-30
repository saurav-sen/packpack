package com.squill.og.crawler.internal.utils;

import com.squill.og.crawler.model.web.FeedClassifier;
import com.squill.og.crawler.model.web.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
public class FeedClassifierUtil {

	private FeedClassifierUtil() {
	}
	
	public static FeedClassifier classify(JRssFeed feed) {
		//TODO -- Use ML api here to classify and upload in knowledge base.
		return null;
	}
}