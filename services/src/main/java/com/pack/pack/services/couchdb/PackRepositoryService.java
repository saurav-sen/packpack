package com.pack.pack.services.couchdb;

import static com.pack.pack.common.util.CommonConstants.NULL_PAGE_LINK;
import static com.pack.pack.common.util.CommonConstants.STANDARD_PAGE_SIZE;

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

import com.pack.pack.model.Pack;
import com.pack.pack.model.web.Pagination;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
@Views({ @View(name = "findPacksByTopicID", map = "function(doc) { if(doc.packParentTopicId) { emit([doc.packParentTopicId, doc.creationTime], doc); } }") })
public class PackRepositoryService extends CouchDbRepositorySupport<Pack> {

	@Autowired
	private TopicPackMapRepositoryService topicPackMapRepoService;

	@Autowired
	public PackRepositoryService(@Qualifier("packDB") CouchDbConnector db) {
		super(Pack.class, db);
	}

	@PostConstruct
	public void doInit() {
		initStandardDesignDocument();
	}

	public Pagination<Pack> getAllPacks(String topicId, String pageLink) {
		PageRequest pr = (pageLink != null && !NULL_PAGE_LINK.equals(pageLink)) ? PageRequest
				.fromLink(pageLink) : PageRequest.firstPage(STANDARD_PAGE_SIZE);
		ViewQuery query = createQuery("findPacksByTopicID").startKey(topicId)
				.descending(true);
		Page<Pack> page = db.queryForPage(query, pr, Pack.class);
		return new Pagination<Pack>(page.getPreviousLink(), page.getNextLink(),
				page.getRows());
	}
}