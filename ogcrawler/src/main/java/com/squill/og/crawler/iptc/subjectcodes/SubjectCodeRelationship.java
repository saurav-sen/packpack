package com.squill.og.crawler.iptc.subjectcodes;

public class SubjectCodeRelationship {
	
	public static final String SELF = "self";
	public static final String PARENT = "parent";

	private String relationship;
	
	private String id;

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
