package com.pack.pack.services.couchdb;

import java.util.ArrayList;
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

import com.pack.pack.model.Topic;
import com.pack.pack.model.UserTopicMap;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
@Views({
	@View(name="allForUser", map="function(doc) { if(doc.userId && doc.id && doc.topicId) { emit([doc.userId, doc.topicId], doc); }}")
})
public class UserTopicMapRepositoryService extends CouchDbRepositorySupport<UserTopicMap> {
	
	@Autowired
	private TopicRepositoryService topicRepoService;

	@Autowired
	public UserTopicMapRepositoryService(@Qualifier("packpackDB") CouchDbConnector db) {
		super(UserTopicMap.class, db);
	}
	
	public List<Topic> getAllTopicsFollowedByUser(String userId) {
		ViewQuery query = createQuery("allForUser").key(userId);
		List<UserTopicMap> list = db.queryView(query, UserTopicMap.class);
		if(list == null || list.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> topicIds = new ArrayList<String>();
		for(UserTopicMap l : list) {
			topicIds.add(l.getId());
		}
		return topicRepoService.getAllTopicsById(topicIds);
	}
	
	public UserTopicMap findUserTopicMapById(String userId, String topicId) {
		ViewQuery query = createQuery("allForUser").key(userId).key(topicId);
		List<UserTopicMap> list = db.queryView(query, UserTopicMap.class);
		if(list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
}