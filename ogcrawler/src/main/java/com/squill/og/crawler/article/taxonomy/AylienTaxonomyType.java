package com.squill.og.crawler.article.taxonomy;

import java.util.ArrayList;
import java.util.List;

public class AylienTaxonomyType {
	
	private boolean confident;
	
	private double score;
	
	private String label;
	
	private List<AylienTaxonomyLink> links;
	
	private String id;

	public boolean isConfident() {
		return confident;
	}

	public void setConfident(boolean confident) {
		this.confident = confident;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<AylienTaxonomyLink> getLinks() {
		if(links == null) {
			links = new ArrayList<AylienTaxonomyLink>();
		}
		return links;
	}

	public void setLinks(List<AylienTaxonomyLink> links) {
		this.links = links;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
