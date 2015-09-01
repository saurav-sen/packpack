package com.pack.pack.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 
 * @author Saurav
 *
 */
@Entity
@Table(name="USER")
public class User {

	@Column(name="NAME")
	private String name;
	
	private String packImage;
	
	private String profilePicture;
	
	private String username;
	
	private String password;

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
}