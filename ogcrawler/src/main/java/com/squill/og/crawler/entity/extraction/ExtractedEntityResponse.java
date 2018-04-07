package com.squill.og.crawler.entity.extraction;

import java.util.LinkedList;
import java.util.List;

public class ExtractedEntityResponse {

	private int time;
	
	private List<EntityAnnotation> annotations;
	
	private List<TopEntity> topEntities;

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public List<EntityAnnotation> getAnnotations() {
		if(annotations == null) {
			annotations = new LinkedList<EntityAnnotation>();
		}
		return annotations;
	}

	public void setAnnotations(List<EntityAnnotation> annotations) {
		this.annotations = annotations;
	}

	public List<TopEntity> getTopEntities() {
		if(topEntities == null) {
			topEntities = new LinkedList<TopEntity>();
		}
		return topEntities;
	}

	public void setTopEntities(List<TopEntity> topEntities) {
		this.topEntities = topEntities;
	}
}
