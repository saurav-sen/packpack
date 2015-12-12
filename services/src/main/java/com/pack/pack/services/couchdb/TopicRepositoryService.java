package com.pack.pack.services.couchdb;

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
	@View(name="getByID", map="function(doc) {if(doc.id) { emit(doc.id, doc);}}")
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
		ViewQuery query = createQuery("getByID");
		List<Topic> topics = db.queryView(query, Topic.class);
		Topic topic = topics.get(0);
		List<String> packIds = topic.getPackIds();
		int start = pageNo*STANDARD_PAGE_SIZE;
		int end = start + STANDARD_PAGE_SIZE - 1;
		packIds = packIds.subList(start, end);
		return packRepoService.getAllPacks(packIds);
	}
}