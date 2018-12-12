package com.squill.feed.web.model;

public enum UploadType {

	AUTOMATIC,
	MANUAL;
	
	public static UploadType resolve(String ogType) {
		if(ogType == null)
			return AUTOMATIC;
		try {
			UploadType uploadType = UploadType.valueOf(ogType.toUpperCase());
			return uploadType;
		} catch (Exception e) {
			return AUTOMATIC;
		}
	}
}
