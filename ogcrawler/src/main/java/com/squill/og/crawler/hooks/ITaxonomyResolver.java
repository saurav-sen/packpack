package com.squill.og.crawler.hooks;

import com.squill.feed.web.model.JTaxonomy;

public interface ITaxonomyResolver {
	
	public JTaxonomy[] resolveTaxonomies(String text, String linkUrl) throws Exception;
}
