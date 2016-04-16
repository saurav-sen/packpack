package com.pack.pack.model.web;

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
	
	public Pagination() {
	}
	
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

	public void setPreviousLink(String previousLink) {
		this.previousLink = previousLink;
	}

	public void setNextLink(String nextLink) {
		this.nextLink = nextLink;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}
}