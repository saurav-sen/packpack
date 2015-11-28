package com.pack.pack.model.web;

import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class JGroup {

	private String id;
	
	private String name;
	
	private List<JUser> users;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<JUser> getUsers() {
		return users;
	}

	public void setUsers(List<JUser> users) {
		this.users = users;
	}
}