package com.squill.og.crawler.named.entities;

import java.util.LinkedList;
import java.util.List;

public class AylienEntities {

	private List<String> organization;
	
	private List<String> location;
	
	private List<String> keyword;
	
	private List<String> date;
	
	private List<String> person;

	public List<String> getOrganization() {
		if(organization == null) {
			organization = new LinkedList<String>();
		}
		return organization;
	}

	public void setOrganization(List<String> organization) {
		this.organization = organization;
	}

	public List<String> getLocation() {
		if(location == null) {
			location = new LinkedList<String>();
		}
		return location;
	}

	public void setLocation(List<String> location) {
		this.location = location;
	}

	public List<String> getKeyword() {
		if(keyword == null) {
			keyword = new LinkedList<String>();
		}
		return keyword;
	}

	public void setKeyword(List<String> keyword) {
		this.keyword = keyword;
	}

	public List<String> getDate() {
		if(date == null) {
			date = new LinkedList<String>();
		}
		return date;
	}

	public void setDate(List<String> date) {
		this.date = date;
	}

	public List<String> getPerson() {
		if(person == null) {
			person = new LinkedList<String>();
		}
		return person;
	}

	public void setPerson(List<String> person) {
		this.person = person;
	}
}
