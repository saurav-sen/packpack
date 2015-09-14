package com.pack.pack.model;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

/**
 * 
 * @author Saurav
 *
 */
@Entity
public class Group extends IdentifiableObject {

	@Property("name")
	private String name;
	
	@Property("description")
	private String description;
	
	private List<User> members;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<User> getMembers() {
		if(members == null) {
			members = new ArrayList<User>(1);
		}
		return members;
	}

	public void setMembers(List<User> members) {
		this.members = members;
	}
}