package com.pack.pack.services.couchdb;

import java.util.Collections;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.ektorp.support.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.Pack;
import com.pack.pack.model.Topic;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
@Views({
	@View(name="findTopicByID", map="function(doc) {if(doc.id) { emit(doc.id, doc); }}")
})
public class TopicRepositoryService extends CouchDbRepositorySupport<Topic> {
	
	private static final int STANDARD_PAGE_SIZE = 20;
	
	@Autowired
	private PackRepositoryService packRepoService;

	@Autowired
	public TopicRepositoryService(@Qualifier("packpackDB") CouchDbConnector db) {
		super(Topic.class, db);
	}
	
	public List<Pack> getAllPacks(String topicId, int pageNo) {
		Topic topic = getTopicById(topicId);
		if(topic == null) {
			return Collections.emptyList();
		}
		List<String> packIds = topic.getPackIds();
		if(packIds == null || packIds.isEmpty()) {
			return Collections.emptyList();
		}
		int start = pageNo*STANDARD_PAGE_SIZE;
		int end = start + STANDARD_PAGE_SIZE - 1;
		packIds = packIds.subList(start, end);
		return packRepoService.getAllPacks(packIds);
	}
	
	public Topic getTopicById(String topicId) {
		ViewQuery query = createQuery("findTopicByID").key(topicId);
		List<Topic> topics = db.queryView(query, Topic.class);
		if(topics == null || topics.isEmpty()) {
			return null;
		}
		return topics.get(0);
	}
	
	public List<Topic> getAllTopicsById(List<String> topicIds) {
		ViewQuery query = createQuery("findTopicByID").keys(topicIds);
		return db.queryView(query, Topic.class);
	}
}