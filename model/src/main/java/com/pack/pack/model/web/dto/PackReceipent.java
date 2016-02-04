package com.pack.pack.model.web.dto;

/**
 * 
 * @author Saurav
 *
 */
public class PackReceipent {

	private String toUserId;
	
	private PackReceipentType type;

	public String getToUserId() {
		return toUserId;
	}

	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}

	public PackReceipentType getType() {
		return type;
	}

	public void setType(PackReceipentType type) {
		this.type = type;
	}
}