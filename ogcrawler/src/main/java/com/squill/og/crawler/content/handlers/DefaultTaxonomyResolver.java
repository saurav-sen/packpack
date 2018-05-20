package com.squill.og.crawler.content.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.feed.web.model.JTaxonomy;
import com.squill.og.crawler.article.taxonomy.AylienTaxonomyClassifier;
import com.squill.og.crawler.article.taxonomy.AylienTaxonomyResponse;
import com.squill.og.crawler.article.taxonomy.AylienTaxonomyType;
import com.squill.og.crawler.hooks.ITaxonomyResolver;

@Component("defaultTaxonomyResolver")
@Scope("prototype")
public class DefaultTaxonomyResolver implements ITaxonomyResolver {

	@Override
	public JTaxonomy[] resolveTaxonomies(String text, String linkUrl) throws Exception {
		List<JTaxonomy> taxonomies = new ArrayList<JTaxonomy>();
		AylienTaxonomyResponse taxonomyResponse = new AylienTaxonomyClassifier().classifyUrl(linkUrl);//.classifyText(text);
		if(taxonomyResponse != null) {
			List<AylienTaxonomyType> categories = taxonomyResponse.getCategories();
			for(AylienTaxonomyType category : categories) {
				if(!category.isConfident())
					continue;
				JTaxonomy taxonoy = new JTaxonomy();
				taxonoy.setId(category.getCode());
				taxonoy.setName(category.getLabel());
				taxonomies.add(taxonoy);
			}
		}
		return taxonomies.toArray(new JTaxonomy[taxonomies.size()]);
	}
}
