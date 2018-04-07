package com.squill.og.crawler.content.handlers;

import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public interface IFeedClassificationResolver {

	public String resolvePrimaryClassifierType(String feedTitle,
			String feedDescription, String url);

	public List<String> resolveIPTCTypes(String feedTitle,
			String feedDescription, String url);
}
