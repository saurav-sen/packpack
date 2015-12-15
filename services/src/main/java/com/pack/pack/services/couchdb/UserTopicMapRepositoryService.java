package com.pack.pack.services.couchdb;

import static com.pack.pack.services.rabbitmq.Constants.STANDARD_PAGE_SIZE;

import java.util.ArrayList;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.Page;
import org.ektorp.PageRequest;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.ektorp.support.Views;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static Logger logger = LoggerFactory.getLogger(UserTopicMapRepositoryService.class);
	
	@Autowired
	private TopicRepositoryService topicRepoService;

	@Autowired
	public UserTopicMapRepositoryService(@Qualifier("packpackDB") CouchDbConnector db) {
		super(UserTopicMap.class, db);
	}
	
	public Pagination<Topic> getAllTopicsFollowedByUser(String userId, String pageLink) {
		logger.debug("Loading All Topic information followed by user having userId="
				+ userId + " in paginated API with page-link=" + pageLink);
		PageRequest pr = pageLink != null ? PageRequest.fromLink(pageLink) : PageRequest.firstPage(STANDARD_PAGE_SIZE);
		ViewQuery query = createQuery("allForUser").key(userId);
		Page<UserTopicMap> page = db.queryForPage(query, pr, UserTopicMap.class);
		if(page == null) {
			return null;
		}
		List<UserTopicMap> list = page.getRows();
		if(list == null || list.isEmpty()) {
			return null;
		}
		List<String> topicIds = new ArrayList<String>();
		for(UserTopicMap l : list) {
			topicIds.add(l.getId());
		}
		List<Topic> result = topicRepoService.getAllTopicsById(topicIds);
		String nextLink = page.getNextLink();
		String previousLink = page.getPreviousLink();
		return new Pagination<Topic>(previousLink, nextLink, result);
	}
	
	/*public static void main(String[] args) {
		PageRequest pr = PageRequest.firstPage(20);
		System.out.println(pr.asLink());
		pr = pr.fromLink(pr.asLink());
		System.out.println(pr.asLink());
		pr.
	}*/
	
	public UserTopicMap findUserTopicMapById(String userId, String topicId) {
		ViewQuery query = createQuery("allForUser").key(userId).key(topicId);
		List<UserTopicMap> list = db.queryView(query, UserTopicMap.class);
		if(list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
}