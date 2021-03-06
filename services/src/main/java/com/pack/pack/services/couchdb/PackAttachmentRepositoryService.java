package com.pack.pack.services.couchdb;

import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE;
import static com.pack.pack.common.util.CommonConstants.NULL_PAGE_LINK;
import static com.pack.pack.common.util.CommonConstants.STANDARD_PAGE_SIZE;
import static com.pack.pack.util.SystemPropertyUtil.HIGH_UNICODE_CHARACTER;

import java.util.Collections;
import java.util.Comparator;
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

import com.pack.pack.model.PackAttachment;
import com.pack.pack.model.web.Pagination;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
@Views({ @View(name = "findPackAttachmentsByPackID", map = "function(doc) { if(doc.attachmentParentPackId) { emit(doc.attachmentParentPackId + doc.creationTime); } }") })
public class PackAttachmentRepositoryService extends
		CouchDbRepositorySupport<PackAttachment> {
	
	private static final Logger LOG = LoggerFactory.getLogger(PackAttachmentRepositoryService.class);

	@Autowired
	public PackAttachmentRepositoryService(
			@Qualifier("packDB") CouchDbConnector db) {
		super(PackAttachment.class, db);
	}

	@PostConstruct
	public void doInit() {
		initStandardDesignDocument();
	}

	public Pagination<PackAttachment> getAllPackAttachment(String packId,
			String pageLink) {
		LOG.info(pageLink);
		PageRequest pr = (pageLink != null && !NULL_PAGE_LINK.equals(pageLink)) ? PageRequest
				.fromLink(pageLink) : PageRequest.firstPage(STANDARD_PAGE_SIZE);
		ViewQuery query = createQuery("findPackAttachmentsByPackID")
				.startKey(packId).endKey(packId + HIGH_UNICODE_CHARACTER)
				.descending(false).includeDocs(true);
		Page<PackAttachment> page = db.queryForPage(query, pr,
				PackAttachment.class);
		String previousLink = page.isHasPrevious() ? page.getPreviousPageRequest().asLink()
				: END_OF_PAGE;
		List<PackAttachment> rows = page.getRows();
		if (rows != null && !rows.isEmpty()) {
			Collections.sort(rows, new Comparator<PackAttachment>() {
				@Override
				public int compare(PackAttachment o1, PackAttachment o2) {
					long l = o1.getCreationTime() - o2.getCreationTime();
					if (l >= 0) {
						return 1;
					}
					return -1;
				}
			});
		}
		String nextLink = page.isHasNext() ? page.getNextPageRequest().asLink() : END_OF_PAGE;
		return new Pagination<PackAttachment>(previousLink, nextLink, rows);
	}

	public List<PackAttachment> getAllListOfPackAttachments(String packId) {
		ViewQuery query = createQuery("findPackAttachmentsByPackID")
				.startKey(packId).endKey(packId + HIGH_UNICODE_CHARACTER)
				.descending(false).includeDocs(true);
		List<PackAttachment> rows = db.queryView(query, PackAttachment.class);
		if (rows != null && !rows.isEmpty()) {
			Collections.sort(rows, new Comparator<PackAttachment>() {
				@Override
				public int compare(PackAttachment o1, PackAttachment o2) {
					long l = o1.getCreationTime() - o2.getCreationTime();
					if (l >= 0) {
						return 1;
					}
					return -1;
				}
			});
		}
		return rows;
	}
}