package com.pack.pack.services.couchdb;

import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE;
import static com.pack.pack.common.util.CommonConstants.NULL_PAGE_LINK;
import static com.pack.pack.common.util.CommonConstants.STANDARD_PAGE_SIZE;
import static com.pack.pack.util.SystemPropertyUtil.HIGH_UNICODE_CHARACTER;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	@View(name="allForUser", map="function(doc) { if(doc.userId && doc._id && doc.topicId && doc.topicCategory) { emit(doc.userId + doc.topicCategory, doc.topicId); }}"),
	@View(name="all", map="function(doc) { if(doc.userId && doc._id && doc.topicId && doc.topicCategory) { emit(null, doc._id); }}"),
	@View(name="allUserForTopic", map="function(doc) { if(doc.userId && doc._id && doc.topicId) { emit(doc.topicId, doc.userId); }}"),
	@View(name="usrVsTopicMap", map="function(doc) { if(doc.userId && doc._id && doc.topicId) { emit(doc.topicId + doc.userId); }}")
	
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
		PageRequest pr = (pageLink != null && !NULL_PAGE_LINK.equals(pageLink))? 
				PageRequest.fromLink(pageLink) : PageRequest.firstPage(STANDARD_PAGE_SIZE);
		/*List<String> keys = new ArrayList<String>();
		keys.add(userId);
		ViewQuery query = createQuery("allForUser").keys(keys);*/
		ViewQuery query = createQuery("allForUser").startKey(userId).endKey(
				userId + HIGH_UNICODE_CHARACTER);
		Page<String> page = db.queryForPage(query, pr, String.class);
		if(page == null) {
			return null;
		}
		List<String> topicIds = page.getRows();
		if(topicIds == null || topicIds.isEmpty()) {
			return null;
		}
		List<Topic> result = topicRepoService.getAllTopicsById(topicIds);
		String nextLink = page.isHasNext() ? page.getNextPageRequest().asLink() : END_OF_PAGE;
		String previousLink = page.isHasPrevious() ? page.getPreviousPageRequest().asLink() : END_OF_PAGE;
		return new Pagination<Topic>(previousLink, nextLink, result);
	}
	
	public Pagination<Topic> getAllTopicsFollowedByUserAndCategory(
			String userId, String topicCategory, String pageLink) {
		logger.debug("Loading All Topic information followed by user having userId="
				+ userId + " in paginated API with page-link=" + pageLink);
		PageRequest pr = (pageLink != null && !NULL_PAGE_LINK.equals(pageLink)) ? PageRequest
				.fromLink(pageLink) : PageRequest.firstPage(STANDARD_PAGE_SIZE);
		// ComplexKey key = ComplexKey.of(userId, topicCategory);
		String key = userId + topicCategory;
		ViewQuery query = createQuery("allForUser").key(key);
		Page<String> page = db.queryForPage(query, pr, String.class);
		if (page == null) {
			return null;
		}
		List<String> topicIds = page.getRows();
		if (topicIds == null || topicIds.isEmpty()) {
			return null;
		}
		List<Topic> result = topicRepoService.getAllTopicsById(topicIds);
		String nextLink = page.isHasNext() ? page.getNextPageRequest().asLink() : END_OF_PAGE;
		String previousLink = page.isHasPrevious() ? page.getPreviousPageRequest().asLink()
				: END_OF_PAGE;
		return new Pagination<Topic>(previousLink, nextLink, result);
	}
	
	public Pagination<Topic> getAllTopicsNotFollowedByUserAndCategory(
			String userId, String topicCategory, String pageLink) {
		logger.debug("Loading All Topic information NOT followed by user having userId="
				+ userId + " & in topicCategoy of " + topicCategory + "in paginated API "
						+ "with page-link=" + pageLink);
		PageRequest pr = (pageLink != null && !NULL_PAGE_LINK.equals(pageLink)) ? PageRequest
				.fromLink(pageLink) : PageRequest.firstPage(STANDARD_PAGE_SIZE);
		Pagination<Topic> page = topicRepoService.getAllTopicsByCategoryName(
				topicCategory, pageLink);
		if (page == null) {
			return null;
		}
		List<Topic> newResult = new LinkedList<Topic>();
		List<Topic> result = page.getResult();
		if (result == null || result.isEmpty()) {
			return page;
		}
		Map<String, Object> topicIdsMap = new HashMap<String, Object>();
		String key = userId + topicCategory;
		ViewQuery query = createQuery("allForUser").key(key);
		Page<String> pg = db.queryForPage(query, pr, String.class);
		if (pg != null) {
			List<String> topicIds = pg.getRows();
			if (topicIds != null && !topicIds.isEmpty()) {
				for (String topicId : topicIds) {
					topicIdsMap.put(topicId, new Object());
				}
			}
		}
		for (Topic topic : result) {
			String id = topic.getId();
			if (id == null)
				continue;
			if (topicIdsMap.get(id) != null)
				continue;
			newResult.add(topic);
		}
		return new Pagination<Topic>(page.getPreviousLink(),
				page.getNextLink(), newResult);
		/*if (newResult.isEmpty()) {
			if (page.getNextLink() == null
					|| END_OF_PAGE.equals(page.getNextLink()))
				return page;
			return getAllTopicsNotFollowedByUserAndCategory(userId,
					topicCategory, page.getNextLink());
		}
		return new Pagination<Topic>(page.getPreviousLink(),
				page.getNextLink(), result);*/
	}
	
	public UserTopicMap findUserTopicMapById(String userId, String topicId) {
		//ViewQuery query = createQuery("usrVsTopicMap").key(userId).key(topicId);
		ViewQuery query = createQuery("usrVsTopicMap").key(topicId + userId)
				.includeDocs(true);
		List<UserTopicMap> list = db.queryView(query, UserTopicMap.class);
		if(list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	
	public List<String> getAllTopicIDsFollowedByUser(String userId) {
		ViewQuery query = createQuery("allForUser").startKey(userId)
				.endKey(userId + HIGH_UNICODE_CHARACTER);
		List<String> IDs = db.queryView(query, String.class);
		return IDs;
	}
}