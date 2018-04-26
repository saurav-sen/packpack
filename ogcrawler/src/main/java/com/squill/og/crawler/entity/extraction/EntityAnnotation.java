package com.squill.og.crawler.entity.extraction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EntityAnnotation {

	private int start;
	
	private int end;
	
	private String spot;
	
	private String confidence;
	
	private long id;
	
	private String title;
	
	private String uri;
	
	private String label;
	
	private List<String> categories;
	
	private List<String> types;
	
	private Lod lod;

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getSpot() {
		return spot;
	}

	public void setSpot(String spot) {
		this.spot = spot;
	}

	public String getConfidence() {
		return confidence;
	}

	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<String> getCategories() {
		if(categories == null) {
			categories = new LinkedList<String>();
		}
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public List<String> getTypes() {
		if(types == null) {
			types = new ArrayList<String>();
		}
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public Lod getLod() {
		return lod;
	}

	public void setLod(Lod lod) {
		this.lod = lod;
	}
}
