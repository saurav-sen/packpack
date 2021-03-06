package com.pack.pack.model.web;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author Saurav
 *
 */
public class JTopic {

	private String ownerId;
	
	private String ownerName;
	
	private String name;
	
	private String description;
	
	private long followers;
	
	private String id;
	
	private String category;
	
	private String wallpaperUrl;
	
	private String ownerProfilePicture;
	
	private boolean isFollowing;
	
	private double longitude;
	
	private double latitude;
	
	private String address;
	
	private Map<String, String> properties;
	
	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

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

	public long getFollowers() {
		return followers;
	}

	public void setFollowers(long followers) {
		this.followers = followers;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getWallpaperUrl() {
		return wallpaperUrl;
	}

	public void setWallpaperUrl(String wallpaperUrl) {
		this.wallpaperUrl = wallpaperUrl;
	}

	public String getOwnerProfilePicture() {
		return ownerProfilePicture;
	}

	public void setOwnerProfilePicture(String ownerProfilePicture) {
		this.ownerProfilePicture = ownerProfilePicture;
	}

	public boolean isFollowing() {
		return isFollowing;
	}

	public void setFollowing(boolean isFollowing) {
		this.isFollowing = isFollowing;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Map<String, String> getProperties() {
		if(properties == null) {
			properties = new HashMap<String, String>();
		}
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
}