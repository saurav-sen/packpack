package com.pack.pack.services.couchdb;

import static com.pack.pack.common.util.CommonConstants.NULL_PAGE_LINK;
import static com.pack.pack.common.util.CommonConstants.STANDARD_PAGE_SIZE;

import java.util.List;

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

import com.pack.pack.model.PackAttachment;
import com.pack.pack.model.web.Pagination;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
@Views({ @View(name = "findPackAttachmentsByPackID", map = "function(doc) { if(doc.attachmentParentPackId) { emit([doc.attachmentParentPackId, doc.creationTime], doc); } }") })
public class PackAttachmentRepositoryService extends
		CouchDbRepositorySupport<PackAttachment> {

	@Autowired
	public PackAttachmentRepositoryService(
			@Qualifier("packDB") CouchDbConnector db) {
		super(PackAttachment.class, db);
	}

	public Pagination<PackAttachment> getAllPackAttachment(String packId,
			String pageLink) {
		PageRequest pr = (pageLink != null && !NULL_PAGE_LINK.equals(pageLink)) ? PageRequest
				.fromLink(pageLink) : PageRequest.firstPage(STANDARD_PAGE_SIZE);
		ViewQuery query = createQuery("findPackAttachmentsByPackID").startKey(
				packId).descending(true);
		Page<PackAttachment> page = db.queryForPage(query, pr,
				PackAttachment.class);
		return new Pagination<PackAttachment>(page.getPreviousLink(),
				page.getNextLink(), page.getRows());
	}

	public List<PackAttachment> getAllListOfPackAttachments(String packId) {
		ViewQuery query = createQuery("findPackAttachmentsByPackID").startKey(
				packId).descending(true);
		return db.queryView(query, PackAttachment.class);
	}
}