package com.pack.pack.model;

import java.util.List;

import org.ektorp.support.CouchDbDocument;

/**
 * 
 * @author Saurav
 *
 */
public class Group extends CouchDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7450277536068876051L;

	private String name;
	
	private List<String> userIds;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}
}