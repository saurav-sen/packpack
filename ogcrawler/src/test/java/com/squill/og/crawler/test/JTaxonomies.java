package com.squill.og.crawler.test;

import java.util.LinkedList;
import java.util.List;

import com.squill.feed.web.model.JTaxonomy;

public class JTaxonomies {

	private List<JTaxonomy> taxonomies;

	public List<JTaxonomy> getTaxonomies() {
		if(taxonomies == null) {
			taxonomies = new LinkedList<JTaxonomy>();
		}
		return taxonomies;
	}

	public void setTaxonomies(List<JTaxonomy> taxonomies) {
		this.taxonomies = taxonomies;
	}
}
