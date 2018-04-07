package com.squill.og.crawler.entity.extraction;

import java.util.ArrayList;
import java.util.List;

public class Concept {

	private String id;
	
	private String spot;
	
	private String parentContent;
	
	private List<String> ontologyTypes;
	
	private String dbpediaRef;

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
}
