package com.pack.pack.services.couchdb;

import static com.pack.pack.common.util.CommonConstants.NULL_PAGE_LINK;
import static com.pack.pack.common.util.CommonConstants.STANDARD_PAGE_SIZE;
import static com.pack.pack.util.SystemPropertyUtil.HIGH_UNICODE_CHARACTER;

import java.util.List;

import javax.annotation.PostConstruct;

import org.ektorp.CouchDbConnector;
import org.ektorp.Page;
import org.ektorp.PageRequest;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.ektorp.support.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.TopicPackMap;
import com.pack.pack.model.web.Pagination;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
@Views({ @View(name = "topicVsPackId", map = "function(doc) { if(doc.topicId && doc.dateTime && doc._id) { emit(doc.topicId + doc.dateTime, doc.packId); } }") })
public class TopicPackMapRepositoryService extends
		CouchDbRepositorySupport<TopicPackMap> {

	@Autowired
	public TopicPackMapRepositoryService(
			@Qualifier("packDB") CouchDbConnector db) {
		super(TopicPackMap.class, db);
	}

	@PostConstruct
	public void doInit() {
		initStandardDesignDocument();
	}

	public Pagination<String> getAllTopicPackMap(String topicId, String pageLink) {
		ViewQuery query = createQuery("topicVsPackId").startKey(topicId)
				.endKey(topicId + HIGH_UNICODE_CHARACTER).descending(true);
		PageRequest pr = (pageLink != null && !NULL_PAGE_LINK.equals(pageLink)) ? PageRequest
				.fromLink(pageLink) : PageRequest.firstPage(STANDARD_PAGE_SIZE);
		Page<String> page = db.queryForPage(query, pr, String.class);
		if (page == null)
			return null;
		List<String> rows = page.getRows();
		Pagination<String> result = new Pagination<String>();
		if(page.isHasNext()) {
			result.setNextLink(page.getNextPageRequest().asLink());
		}
		if(page.isHasPrevious()) {
			result.setPreviousLink(page.getPreviousPageRequest().asLink());
		}
		result.setResult(rows);
		return result;
	}
}