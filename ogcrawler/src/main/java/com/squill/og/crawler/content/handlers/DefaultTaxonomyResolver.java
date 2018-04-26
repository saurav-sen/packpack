package com.squill.og.crawler.content.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.og.crawler.article.taxonomy.AylienTaxonomyClassifier;
import com.squill.og.crawler.article.taxonomy.AylienTaxonomyLink;
import com.squill.og.crawler.article.taxonomy.AylienTaxonomyResponse;
import com.squill.og.crawler.article.taxonomy.AylienTaxonomyType;
import com.squill.og.crawler.hooks.ITaxonomyResolver;
import com.squill.og.crawler.model.web.JTaxonomy;

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
				taxonoy.setId(category.getId());
				taxonoy.setName(category.getLabel());
				List<AylienTaxonomyLink> links = category.getLinks();
				for(AylienTaxonomyLink link : links) {
					String relationship = link.getRel();
					if("self".equals(relationship)) {
						taxonoy.setRefUri(link.getLink());
					} else if("parent".equals(relationship)) {
						taxonoy.setParentRefUrl(link.getLink());
					}
					taxonomies.add(taxonoy);
				}
			}
		}
		return taxonomies.toArray(new JTaxonomy[taxonomies.size()]);
	}

}
