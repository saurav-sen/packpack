package com.pack.pack.model.web;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class Pagination<T> {
	
	private int nextPageNo;

	private List<T> result;
	
	public Pagination() {
	}
	
	public Pagination(int nextPageNo, List<T> result) {
		this.nextPageNo = nextPageNo;
		this.result = result;
	}

	public List<T> getResult() {
		if(result == null) {
			result = new ArrayList<T>();
		}
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	public int getNextPageNo() {
		return nextPageNo;
	}

	public void setNextPageNo(int nextPageNo) {
		this.nextPageNo = nextPageNo;
	}
}