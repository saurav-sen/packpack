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
public class User extends IdentifiableObject {
	
	@Property("name")
	private String name;
	
	@Property("packImg")
	private String packImage;
	
	@Property("profilePciture")
	private String profilePicture;
	
	@Property("username")
	private String username;

	@Property("pwd")
	private String password;
	
	private List<Group> groups;
	
	@Property(value="origin", concreteClass=UserOrigin.class)
	private UserOrigin origin;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackImage() {
		return packImage;
	}

	public void setPackImage(String packImage) {
		this.packImage = packImage;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Group> getGroups() {
		if(groups == null) {
			groups = new ArrayList<Group>(1);
		}
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public UserOrigin getOrigin() {
		return origin;
	}

	public void setOrigin(UserOrigin origin) {
		this.origin = origin;
	}
}