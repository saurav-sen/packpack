package com.pack.pack.services.couchdb;

import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE;
import static com.pack.pack.common.util.CommonConstants.NULL_PAGE_LINK;
import static com.pack.pack.common.util.CommonConstants.STANDARD_PAGE_SIZE;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

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
	@View(name = "findTopicByOwner", map = "function(doc) {if(doc.ownerId && doc._id) { emit(doc.ownerId, doc); }}"),
	@View(name = "findTopicByID", map = "function(doc) {if(doc.ownerId && doc._id) { emit(doc._id, doc); }}") 
})
public class TopicRepositoryService extends CouchDbRepositorySupport<Topic> {
	
	private static Logger logger = LoggerFactory.getLogger(TopicRepositoryService.class);

	@Autowired
	private PackRepositoryService packRepoService;

	@Autowired
	public TopicRepositoryService(@Qualifier("packDB") CouchDbConnector db) {
		super(Topic.class, db);
	}
	
	@PostConstruct
	public void doInit() {
		initStandardDesignDocument();
	}

	public Pagination<Pack> getAllPacks(String topicId, String pageLink) {
		logger.debug("getAllPacks()");
		logger.debug("TopicID = " + topicId);
		logger.debug("PageNo = " + pageLink);
		/*if (topic == null) {
			return Collections.emptyList();
		}*/
		//List<String> packIds = topic.getPackIds();
		/*if (packIds == null || packIds.isEmpty()) {
			return Collections.emptyList();
		}*/
		return packRepoService.getAllPacks(topicId, pageLink);
	}

	public Topic getTopicById(String topicId) {
		logger.debug("getTopicById(...)");
		logger.debug("TopicID = " + topicId);
		ViewQuery query = createQuery("findTopicByID").key(topicId);
		List<Topic> topics = db.queryView(query, Topic.class);
		if (topics == null || topics.isEmpty()) {
			return null;
		}
		return topics.get(0);
	}

	public List<Topic> getAllTopicsById(List<String> topicIds) {
		logger.debug("getAllTopicsById(...) for a specified range of topicIDs");
		ViewQuery query = createQuery("findTopicByID").keys(topicIds);
		return db.queryView(query, Topic.class);
	}

	public Pagination<Topic> getAllTopics(String userId, String pageLink) {
		logger.debug("getAllTopics(userId=" + userId + ", pageLink=" + pageLink);
		ViewQuery query = createQuery("findTopicByOwner").key(userId);
		if(END_OF_PAGE.equals(pageLink)) {
			return new Pagination<Topic>(END_OF_PAGE, END_OF_PAGE, Collections.emptyList());
		}
		PageRequest pr = (pageLink != null && !NULL_PAGE_LINK.equals(pageLink)) ? PageRequest.fromLink(pageLink)
				: PageRequest.firstPage(STANDARD_PAGE_SIZE);
		Page<Topic> page = db.queryForPage(query, pr, Topic.class);
		String nextLink = page.isHasNext() ? page.getNextLink() : END_OF_PAGE;
		String previousLink = page.isHasPrevious() ? page.getPreviousLink() : END_OF_PAGE;
		List<Topic> topics = page.getRows();
		topics = db.queryView(query, Topic.class);
		return new Pagination<Topic>(previousLink, nextLink, topics);
	}
}