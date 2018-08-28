package com.squill.feed.web.model;

import java.util.ArrayList;
import java.util.List;

public class JConcept {

	private String id;
	
	private String spot;
	
	private String parentContent;
	
	private List<String> ontologyTypes;
	
	private String dbpediaRef;
	
	private int startIndex;
	
	private int endIndex;
	
	private String content;
	
	private double confidence;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSpot() {
		return spot;
	}

	public void setSpot(String spot) {
		this.spot = spot;
	}

	public String getParentContent() {
		return parentContent;
	}

	public void setParentContent(String parentContent) {
		this.parentContent = parentContent;
	}

	public List<String> getOntologyTypes() {
		if(ontologyTypes == null) {
			ontologyTypes = new ArrayList<String>();
		}
		return ontologyTypes;
	}

	public void setOntologyTypes(List<String> ontologyTypes) {
		this.ontologyTypes = ontologyTypes;
	}

	public String getDbpediaRef() {
		return dbpediaRef;
	}

	public void setDbpediaRef(String dbpediaRef) {
		this.dbpediaRef = dbpediaRef;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof JConcept) {
			return this.dbpediaRef.equals(((JConcept)obj).dbpediaRef);
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return (this.getClass().getName() + "_" + dbpediaRef).hashCode();
	}
}
