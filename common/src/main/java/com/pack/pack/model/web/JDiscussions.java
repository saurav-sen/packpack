package com.pack.pack.model.web;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class JDiscussions {

	private List<JDiscussion> discussions;

	public List<JDiscussion> getDiscussions() {
		if(discussions == null) {
			discussions = new LinkedList<JDiscussion>();
		}
		return discussions;
	}

	public void setDiscussions(List<JDiscussion> discussions) {
		this.discussions = discussions;
	}
}