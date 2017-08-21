package com.pack.pack.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ektorp.support.CouchDbDocument;

/**
 * 
 * @author Saurav
 *
 */
public class Topic extends CouchDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5516874502198452029L;

	private String ownerId;
	
	private String name;
	
	private String description;
	
	private long followers;
	
	private List<String> packIds;
	
	private String category;
	
	private String wallpaperUrl;
	
	private double longitude;
	
	private double latitude;
	
	private String address;
	
	private List<TopicProperty> propeties;
	
	private String subCategory;
	
	private boolean active;
	
	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
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

	public List<String> getPackIds() {
		if(packIds == null) {
			packIds = new LinkedList<String>();
		}
		return packIds;
	}

	public void setPackIds(List<String> packIds) {
		this.packIds = packIds;
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

	public List<TopicProperty> getPropeties() {
		if(propeties == null) {
			propeties = new ArrayList<TopicProperty>(5);
		}
		return propeties;
	}

	public void setPropeties(List<TopicProperty> propeties) {
		this.propeties = propeties;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}