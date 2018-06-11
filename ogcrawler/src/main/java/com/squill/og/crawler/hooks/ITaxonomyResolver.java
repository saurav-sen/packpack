package com.squill.og.crawler.hooks;

import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JTaxonomy;

public interface ITaxonomyResolver {
	
	public boolean canResolve(String linkUrl, String domainUrl, JRssFeed feed);
	
	public JTaxonomy[] resolveTaxonomies(String text, String linkUrl, String domainUrl) throws Exception;
}
