package com.pack.pack.services.couchdb;

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
	
	public Pagination(String previousLink, String nextLink, List<T> result) {
		this.previousLink = previousLink;
		this.nextLink = nextLink;
		this.result = result;
	}

	public String getPreviousLink() {
		return previousLink;
	}

	public String getNextLink() {
		return nextLink;
	}

	public List<T> getResult() {
		return result;
	}
}