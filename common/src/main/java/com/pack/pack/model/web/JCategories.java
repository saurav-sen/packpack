package com.pack.pack.model.web;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class JCategories {
	
	private List<JCategory> categories;

	public List<JCategory> getCategories() {
		if(categories == null) {
			categories = new LinkedList<JCategory>();
		}
		return categories;
	}

	public void setCategories(List<JCategory> categories) {
		this.categories = categories;
	}
}