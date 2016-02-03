package com.pack.pack.model.web.dto;

/**
 * 
 * @author Saurav
 *
 */
public class BroadcastPackDTO {

	private String packId;
	
	private String city;

	private String state;

	private String country;

	public String getPackId() {
		return packId;
	}

	public void setPackId(String packId) {
		this.packId = packId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}