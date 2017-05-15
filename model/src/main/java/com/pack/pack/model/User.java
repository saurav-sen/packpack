package com.pack.pack.model;

import java.util.ArrayList;
import java.util.List;

import org.ektorp.support.CouchDbDocument;

/**
 * 
 * @author Saurav
 *
 */
public class User extends CouchDbDocument {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1919865742847174343L;

	private String name;
	
	private String packImage;
	
	private String profilePicture;
	
	private String username;

	private String password;
	
	private String dob;
	
	private List<UserInfo> extraInfoMap;
	
	private String city;
	
	private String country;
	
	private String displayName;
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
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

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public List<UserInfo> getExtraInfoMap() {
		if(extraInfoMap == null) {
			extraInfoMap = new ArrayList<UserInfo>(1);
		}
		return extraInfoMap;
	}

	public void setExtraInfoMap(List<UserInfo> extraInfoMap) {
		this.extraInfoMap = extraInfoMap;
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