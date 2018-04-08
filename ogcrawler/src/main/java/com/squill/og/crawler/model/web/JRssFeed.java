package com.squill.og.crawler.model.web;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.squill.og.crawler.entity.extraction.Concept;

/**
 * 
 * @author Saurav
 *
 */
public class JRssFeed {

	private String ogTitle;
	
	private String ogDescription;
	
	private String ogType;
	
	private String ogImage;
	
	private String ogUrl;
	
	private String hrefSource;
	
	private String id;
	
	@JsonIgnore
	private String preClassifiedType;
	
	private String articleSummaryText;
	
	private String fullArticleText;
	
	private List<JGeoTag> geoTags;
	
	private List<Concept> concepts;
	
	private List<JTaxonomy> taxonomies;

	public String getOgTitle() {
		return ogTitle;
	}

	public void setOgTitle(String ogTitle) {
		this.ogTitle = ogTitle;
	}

	public String getOgDescription() {
		return ogDescription;
	}

	public void setOgDescription(String ogDescription) {
		this.ogDescription = ogDescription;
	}

	public String getOgType() {
		return ogType;
	}

	public void setOgType(String ogType) {
		this.ogType = ogType;
	}

	public String getOgImage() {
		return ogImage;
	}

	public void setOgImage(String ogImage) {
		this.ogImage = ogImage;
	}

	public String getOgUrl() {
		return ogUrl;
	}

	public void setOgUrl(String ogUrl) {
		this.ogUrl = ogUrl;
	}

	public String getHrefSource() {
		return hrefSource;
	}

	public void setHrefSource(String hrefSource) {
		this.hrefSource = hrefSource;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPreClassifiedType() {
		return preClassifiedType;
	}

	public void setPreClassifiedType(String preClassifiedType) {
		this.preClassifiedType = preClassifiedType;
	}

	public String getArticleSummaryText() {
		return articleSummaryText;
	}

	public void setArticleSummaryText(String articleSummaryText) {
		this.articleSummaryText = articleSummaryText;
	}

	public String getFullArticleText() {
		return fullArticleText;
	}

	public void setFullArticleText(String fullArticleText) {
		this.fullArticleText = fullArticleText;
	}

	public List<JGeoTag> getGeoTags() {
		if(geoTags == null) {
			geoTags = new LinkedList<JGeoTag>();
		}
		return geoTags;
	}

	public void setGeoTags(List<JGeoTag> geoTags) {
		this.geoTags = geoTags;
	}

	public List<Concept> getConcepts() {
		if(concepts == null) {
			concepts = new ArrayList<Concept>();
		}
		return concepts;
	}

	public void setConcepts(List<Concept> concepts) {
		this.concepts = concepts;
	}

	public List<JTaxonomy> getTaxonomies() {
		if(taxonomies == null) {
			taxonomies = new ArrayList<JTaxonomy>();
		}
		return taxonomies;
	}

	public void setTaxonomies(List<JTaxonomy> taxonomies) {
		this.taxonomies = taxonomies;
	}
}