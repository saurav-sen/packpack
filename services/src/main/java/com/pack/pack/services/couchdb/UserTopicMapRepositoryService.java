package com.pack.pack.services.couchdb;

import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE;
import static com.pack.pack.common.util.CommonConstants.NULL_PAGE_LINK;
import static com.pack.pack.common.util.CommonConstants.STANDARD_PAGE_SIZE;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.ektorp.ComplexKey;
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
import com.pack.pack.model.web.Pagination;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
@Views({
	@View(name="allForUser", map="function(doc) { if(doc.userId && doc._id && doc.topicId && doc.topicCategory) { emit([doc.userId, doc.topicCategory], doc.topicId); }}"),
	@View(name="allUserForTopic", map="function(doc) { if(doc.userId && doc._id && doc.topicId) { emit(doc.topicId, doc.userId); }}"),
	@View(name="usrVsTopicMap", map="function(doc) { if(doc.userId && doc._id && doc.topicId) { emit([doc.topicId, doc.userId], doc); }}")
	
})
public class UserTopicMapRepositoryService extends CouchDbRepositorySupport<UserTopicMap> {
	
	private static Logger logger = LoggerFactory.getLogger(UserTopicMapRepositoryService.class);
	
	@Autowired
	private TopicRepositoryService topicRepoService;

	@Autowired
	public UserTopicMapRepositoryService(@Qualifier("packDB") CouchDbConnector db) {
		super(UserTopicMap.class, db);
	}
	
	@PostConstruct
	public void doInit() {
		initStandardDesignDocument();
	}
	
	public Pagination<Topic> getAllTopicsFollowedByUser(String userId, String pageLink) {
		logger.debug("Loading All Topic information followed by user having userId="
				+ userId + " in paginated API with page-link=" + pageLink);
		PageRequest pr = (pageLink != null && !NULL_PAGE_LINK.equals(pageLink))? PageRequest.fromLink(pageLink) : PageRequest.firstPage(STANDARD_PAGE_SIZE);
		List<String> keys = new ArrayList<String>();
		keys.add(userId);
		ViewQuery query = createQuery("allForUser").keys(keys);
		Page<String> page = db.queryForPage(query, pr, String.class);
		if(page == null) {
			return null;
		}
		List<String> topicIds = page.getRows();
		if(topicIds == null || topicIds.isEmpty()) {
			return null;
		}
		List<Topic> result = topicRepoService.getAllTopicsById(topicIds);
		String nextLink = page.isHasNext() ? page.getNextLink() : END_OF_PAGE;
		String previousLink = page.isHasPrevious() ? page.getPreviousLink() : END_OF_PAGE;
		return new Pagination<Topic>(previousLink, nextLink, result);
	}
	
	public Pagination<Topic> getAllTopicsFollowedByUserAndCategory(String userId, String topicCategory, String pageLink) {
		logger.debug("Loading All Topic information followed by user having userId="
				+ userId + " in paginated API with page-link=" + pageLink);
		PageRequest pr = (pageLink != null && !NULL_PAGE_LINK.equals(pageLink))? PageRequest.fromLink(pageLink) : PageRequest.firstPage(STANDARD_PAGE_SIZE);
		ComplexKey key = ComplexKey.of(userId, topicCategory);
		ViewQuery query = createQuery("allForUser").key(key);
		Page<String> page = db.queryForPage(query, pr, String.class);
		if(page == null) {
			return null;
		}
		List<String> topicIds = page.getRows();
		if(topicIds == null || topicIds.isEmpty()) {
			return null;
		}
		List<Topic> result = topicRepoService.getAllTopicsById(topicIds);
		String nextLink = page.isHasNext() ? page.getNextLink() : END_OF_PAGE;
		String previousLink = page.isHasPrevious() ? page.getPreviousLink() : END_OF_PAGE;
		return new Pagination<Topic>(previousLink, nextLink, result);
	}
	
	public UserTopicMap findUserTopicMapById(String userId, String topicId) {
		ViewQuery query = createQuery("usrVsTopicMap").key(userId).key(topicId);
		List<UserTopicMap> list = db.queryView(query, UserTopicMap.class);
		if(list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	
	public List<String> getAllTopicIDsFollowedByUser(String userId) {
		ViewQuery query = createQuery("allForUser").startKey(userId);
		List<String> IDs = db.queryView(query, String.class);
		return IDs;
	}
}