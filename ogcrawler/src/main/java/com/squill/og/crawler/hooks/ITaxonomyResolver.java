package com.squill.og.crawler.hooks;

import com.squill.og.crawler.model.web.JTaxonomy;

public interface ITaxonomyResolver {
	
	public JTaxonomy[] resolveTaxonomies(String text, String linkUrl) throws Exception;
}
