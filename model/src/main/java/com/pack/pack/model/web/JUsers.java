package com.pack.pack.model.web;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author Saurav
 *
 */
public class JUsers {

	private List<JUser> users;

	public List<JUser> getUsers() {
		if(users == null) {
			users = new ArrayList<JUser>();
		}
		return users;
	}

	public void setUsers(List<JUser> users) {
		this.users = users;
	}
}