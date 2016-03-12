package com.pack.pack.model.web;

/**
 * 
 * @author Saurav
 *
 */
public class JStatus {

	private StatusType status;
	
	private String info;

	public StatusType getStatus() {
		return status;
	}

	public void setStatus(StatusType status) {
		this.status = status;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}