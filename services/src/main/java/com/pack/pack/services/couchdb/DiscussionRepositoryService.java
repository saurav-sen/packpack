package com.pack.pack.services.couchdb;

import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE;
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

import com.pack.pack.model.Discussion;
import com.pack.pack.model.web.EntityType;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.exception.PackPackException;

@Component
@Scope("singleton")
@Views({
		@View(name = "findDiscussions", map = "function(doc){if(doc.startedByUserId && doc.content && doc.parentEntityId && doc.parentEntityType && doc.tag == 'DISCUSSION') { emit(doc.tag + \"::\" + doc.parentEntityId + doc.parentEntityType); }}"),
		@View(name = "findReplies", map = "function(doc){if(doc.startedByUserId && doc.content && doc.parentEntityId && doc.parentEntityType && doc.tag == 'REPLY' && doc.dateTime) { emit(doc.parentEntityId + doc.dateTime); }}") })
public class DiscussionRepositoryService extends
		CouchDbRepositorySupport<Discussion> {

	private static final String DISCUSSION_VIEW_KEY_PREFIX = EntityType.DISCUSSION + "::";

	@Autowired
	public DiscussionRepositoryService(@Qualifier("packDB") CouchDbConnector db) {
		super(Discussion.class, db);
	}

	@PostConstruct
	public void doInit() {
		initStandardDesignDocument();
	}

	public Pagination<Discussion> getAllDiscussions(String entityId,
			String entityType, String pageLink) throws PackPackException {
		PageRequest pr = (pageLink != null && !NULL_PAGE_LINK.equals(pageLink)) ? PageRequest
				.fromLink(pageLink) : PageRequest.firstPage(STANDARD_PAGE_SIZE);
		String key = DISCUSSION_VIEW_KEY_PREFIX + entityId + entityType;
		ViewQuery query = createQuery("findDiscussions").key(key).includeDocs(
				true);// .descending(false)
		Page<Discussion> page = db.queryForPage(query, pr, Discussion.class);
		String previousLink = page.isHasPrevious() ? page.getPreviousPageRequest().asLink()
				: END_OF_PAGE;
		List<Discussion> rows = page.getRows();
		/*
		 * if (rows != null && !rows.isEmpty()) { Collections.sort(rows, new
		 * Comparator<Discussion>() {
		 * 
		 * @Override public int compare(Discussion o1, Discussion o2) { long l =
		 * o1.get - o2.getCreationTime(); if (l >= 0) { return 1; } return -1; }
		 * }); }
		 */
		String nextLink = page.isHasNext() ? page.getNextPageRequest().asLink() : END_OF_PAGE;
		return new Pagination<Discussion>(previousLink, nextLink, rows);
	}

	public Pagination<Discussion> getAllReplies(String discussionId,
			String pageLink) throws PackPackException {
		PageRequest pr = (pageLink != null && !NULL_PAGE_LINK.equals(pageLink)) ? PageRequest
				.fromLink(pageLink) : PageRequest.firstPage(STANDARD_PAGE_SIZE);
		ViewQuery query = createQuery("findReplies").startKey(discussionId)
				.endKey(discussionId + HIGH_UNICODE_CHARACTER)
				.descending(false).includeDocs(true);
		Page<Discussion> page = db.queryForPage(query, pr, Discussion.class);
		String previousLink = page.isHasPrevious() ? page.getPreviousPageRequest().asLink()
				: END_OF_PAGE;
		List<Discussion> rows = page.getRows();
		String nextLink = page.isHasNext() ? page.getNextPageRequest().asLink() : END_OF_PAGE;
		return new Pagination<Discussion>(previousLink, nextLink, rows);
	}
}