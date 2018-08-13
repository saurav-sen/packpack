package com.squill.og.crawler.content.handlers;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;
import com.squill.feed.web.model.JTaxonomy;
import com.squill.og.crawler.hooks.ITaxonomyResolver;
import com.squill.og.crawler.iptc.subjectcodes.SubjectCodeRegistry;

@Component("basicTaxonomyResolver")
@Scope("prototype")
public class BasicTaxonomyResolver implements ITaxonomyResolver {

	@Override
	public JTaxonomy[] resolveTaxonomies(String text, String linkUrl,
			String domainUrl) throws Exception {
		JTaxonomy jTaxonomy = tryResolveTaxonomy(domainUrl);
		if (jTaxonomy == null)
			return new JTaxonomy[0];
		JTaxonomy[] result = new JTaxonomy[] { jTaxonomy };
		return result;
	}

	@Override
	public boolean canResolve(String linkUrl, String domainUrl, JRssFeed feed) {
		JTaxonomy jTaxonomy = tryResolveTaxonomy(domainUrl);
		return jTaxonomy != null ? true : false;
	}

	private JTaxonomy tryResolveTaxonomy(String domainUrl) {
		JTaxonomy jTaxonomy = DomainSpecificInfoHolder.INSTANCE
				.getDefaultTaxonomyByDomainUrl(domainUrl);
		if (jTaxonomy == null) { // TODO -- Temporary work around for now.
			jTaxonomy = new JTaxonomy();
			jTaxonomy.setId("11000000");
			jTaxonomy.setName("politics");
			return jTaxonomy;
			//return null;
		}
		JRssFeedType feedType = SubjectCodeRegistry.INSTANCE
				.resolveSquillFeedType(jTaxonomy);
		if (feedType == null)
			return null;
		if (!isSupportedFeedType(feedType))
			return null;
		return jTaxonomy;
	}

	private boolean isSupportedFeedType(JRssFeedType feedType) {
		return feedType == JRssFeedType.NEWS_SPORTS
				|| feedType == JRssFeedType.NEWS_SCIENCE_TECHNOLOGY
				|| feedType == JRssFeedType.ARTICLE
				|| feedType == JRssFeedType.REFRESHMENT;
	}
}