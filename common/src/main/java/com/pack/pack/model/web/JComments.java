package com.pack.pack.model.web;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class JComments {

	private List<JComment> comments;

	public List<JComment> getComments() {
		if(comments == null) {
			comments = new ArrayList<JComment>(20);
		}
		return comments;
	}

	public void setComments(List<JComment> comments) {
		this.comments = comments;
	}
}