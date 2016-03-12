package com.pack.pack.model.web.dto;

/**
 * 
 * @author Saurav
 *
 */
public class LikeDTO {

	private String userId;
	
	private String packId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPackId() {
		return packId;
	}

	public void setPackId(String packId) {
		this.packId = packId;
	}
}