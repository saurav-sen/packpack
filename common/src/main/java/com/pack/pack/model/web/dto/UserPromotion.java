package com.pack.pack.model.web.dto;

/**
 * 
 * @author Saurav
 *
 */
public class UserPromotion {
	
	public static final String TOPIC_TYPE = "topic";
	public static final String PACK_TYPE = "pack";

	private String userId;
	
	private String objectId;
	
	private String objectType;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
}
