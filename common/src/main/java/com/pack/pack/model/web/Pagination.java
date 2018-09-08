package com.pack.pack.model.web;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class Pagination<T> {
	
	private String previousLink;
	private String nextLink;

	private List<T> result;
	
	private long timestamp;
	
	public Pagination() {
	}
	
	public Pagination(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public Pagination(String previousLink, String nextLink, List<T> result, long timestamp) {
		this.previousLink = previousLink;
		this.nextLink = nextLink;
		this.result = result;
		this.timestamp = timestamp;
	}

	public String getPreviousLink() {
		return previousLink;
	}

	public String getNextLink() {
		return nextLink;
	}

	public List<T> getResult() {
		if(result == null) {
			result = new ArrayList<T>();
		}
		return result;
	}

	public void setPreviousLink(String previousLink) {
		this.previousLink = previousLink;
	}

	public void setNextLink(String nextLink) {
		this.nextLink = nextLink;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}