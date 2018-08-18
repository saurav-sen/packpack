package com.pack.pack.model;

import java.util.ArrayList;
import java.util.List;

import org.ektorp.support.CouchDbDocument;

/**
 * 
 * @author Saurav
 *
 */
public class SemanticElement extends CouchDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3382106426078468016L;

	private String conceptId;

	private String spot;

	private String parentContent;

	private List<String> ontologyTypes;

	private String dbpediaRef;

	private int startIndex;

	private int endIndex;

	private String content;

	private double confidence;
	
	private List<GeoTag> geoTagSet;

	public String getConceptId() {
		return conceptId;
	}

	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
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
		if (ontologyTypes == null) {
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

	public List<GeoTag> getGeoTagSet() {
		if(geoTagSet == null) {
			geoTagSet = new ArrayList<GeoTag>();
		}
		return geoTagSet;
	}

	public void setGeoTagSet(List<GeoTag> geoTagSet) {
		this.geoTagSet = geoTagSet;
	}
}
