package com.squill.og.crawler.content.handlers;

/**
 * 
 * @author Saurav
 *
 */
public interface IFeedClassificationResolver {

	public String resolveClassifierType(String feedTitle,
			String feedDescription, String url);
}
