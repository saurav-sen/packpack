package com.squill.feed.web.model;

public enum UploadType {

	AUTOMATIC,
	MANUAL;
	
	public static UploadType resolve(String type) {
		if(type == null)
			return AUTOMATIC;
		try {
			UploadType uploadType = UploadType.valueOf(type.toUpperCase());
			return uploadType;
		} catch (Exception e) {
			return AUTOMATIC;
		}
	}
}
