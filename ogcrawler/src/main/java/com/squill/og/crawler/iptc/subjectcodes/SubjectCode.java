package com.squill.og.crawler.iptc.subjectcodes;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SubjectCode {

	private String id;
	
	private String label;
	
	private List<SubjectCodeRelationship> relationships;
	
	@JsonIgnore
	private String parentId;
	
	private String squillType;
	
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

	public List<SubjectCodeRelationship> getRelationships() {
		if(relationships == null) {
			relationships = new ArrayList<SubjectCodeRelationship>(4);
		}
		return relationships;
	}

	public void setRelationships(List<SubjectCodeRelationship> relationships) {
		this.relationships = relationships;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getSquillType() {
		return squillType;
	}

	public void setSquillType(String squillType) {
		this.squillType = squillType;
	}
}
