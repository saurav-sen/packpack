package com.pack.pack.services.redis;

import java.util.LinkedList;
import java.util.List;

import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;

/**
 * 
 * @author Saurav
 *
 */
public class TopicPage {

	private String previousLink;
	private String nextLink;
	
	private List<JTopic> topics;

	public String getPreviousLink() {
		return previousLink;
	}

	public void setPreviousLink(String previousLink) {
		this.previousLink = previousLink;
	}

	public String getNextLink() {
		return nextLink;
	}

	public void setNextLink(String nextLink) {
		this.nextLink = nextLink;
	}

	public List<JTopic> getTopics() {
		if(topics == null) {
			topics = new LinkedList<JTopic>();
		}
		return topics;
	}

	public void setTopics(List<JTopic> packs) {
		this.topics = packs;
	}
	
	public Pagination<JTopic> convert() {
		Pagination<JTopic> page = new Pagination<JTopic>();
		page.setNextLink(nextLink);
		page.setPreviousLink(previousLink);
		page.setResult(getTopics());
		return page;
	}
	
	public static TopicPage build(Pagination<JTopic> page) {
		TopicPage r = new TopicPage();
		r.setNextLink(page.getNextLink());
		r.setPreviousLink(page.getPreviousLink());
		r.setTopics(page.getResult());
		return r;
	}
}