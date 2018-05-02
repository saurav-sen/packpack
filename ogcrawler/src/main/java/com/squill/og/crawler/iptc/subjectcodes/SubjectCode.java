package com.squill.og.crawler.iptc.subjectcodes;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SubjectCode {

	private String id;
	
	private String label;
	
	private List<SubjectCodeLink> links;
	
	@JsonIgnore
	private String parentLink;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<SubjectCodeLink> getLinks() {
		if(links == null) {
			links = new ArrayList<SubjectCodeLink>(4);
		}
		return links;
	}

	public void setLinks(List<SubjectCodeLink> links) {
		this.links = links;
	}

	public String getParentLink() {
		return parentLink;
	}

	public void setParentLink(String parentLink) {
		this.parentLink = parentLink;
	}
}
