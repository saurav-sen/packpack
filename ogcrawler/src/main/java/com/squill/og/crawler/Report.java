package com.squill.og.crawler;

/**
 *
 * @author Saurav
 * @since 15-Mar-2015
 *
 */
public class Report {

	private String fileName;
	
	private String userId;
	
	public Report(String fileName, String userId) {
		setFileName(fileName);
		setUserId(userId);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}