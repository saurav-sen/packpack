package com.squill.og.crawler.internal.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;
import com.squill.feed.web.model.JTaxonomy;
import com.squill.og.crawler.iptc.subjectcodes.SubjectCodeRegistry;

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

	public static String classify(JRssFeed feed) {
		List<JTaxonomy> taxonomies = feed.getTaxonomies();
		if (taxonomies != null && !taxonomies.isEmpty()) {
			for (JTaxonomy taxonomy : taxonomies) {
				JRssFeedType feedType = SubjectCodeRegistry.INSTANCE
						.resolveSquillFeedType(taxonomy);
				if (feedType == null)
					continue;
				return feedType.name();
			}
		}
		String classifiedType = feed.getFeedType();
		if (classifiedType != null) {
			try {
				JRssFeedType classifier = JRssFeedType.valueOf(classifiedType
						.toUpperCase());
				if (classifier != null) {
					return classifier.name();
				}
			} catch (Exception e) {
				LOG.info(e.getMessage(), e);
			}

		}
		// TODO -- Use ML api here to classify and upload in knowledge base.
		return null;
	}
}