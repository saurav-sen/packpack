package com.pack.pack.model.web;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class JUser {
	
	private String id;

	private String name;
	
	private String username;
	
	private String dob;
	
	private String profilePictureUrl;
	
	private List<String> followedCategories;
	
	private String city;
	
	private String country;
	
	private String displayName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

	public void setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getFollowedCategories() {
		if(followedCategories == null) {
			followedCategories = new ArrayList<String>(10);
		}
		return followedCategories;
	}

	public void setFollowedCategories(List<String> followedCategories) {
		this.followedCategories = followedCategories;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}