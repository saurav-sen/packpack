package com.pack.pack.model.web;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class JTopics {

	private List<JTopic> topics;

	public List<JTopic> getTopics() {
		if(topics == null) {
			topics = new ArrayList<JTopic>();
		}
		return topics;
	}

	public void setTopics(List<JTopic> topics) {
		this.topics = topics;
	}
}