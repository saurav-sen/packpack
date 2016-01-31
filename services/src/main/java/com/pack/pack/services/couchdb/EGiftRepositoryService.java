package com.pack.pack.services.couchdb;

import static com.pack.pack.services.rabbitmq.Constants.NULL_PAGE_LINK;
import static com.pack.pack.services.rabbitmq.Constants.STANDARD_PAGE_SIZE;

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

import com.pack.pack.model.EGift;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
@Views({
		@View(name = "basedOnCategory", map = "function(doc) { if(doc.brandInfo && doc.brandId) { emit(doc.category, [doc.id, doc.title, doc.imageUrl, doc.imageThumbnailUrl, doc.brandInfo, doc.brandId]); } }"),
		@View(name = "basedOnTitle", map = "function(doc) { if(doc.title && doc.brandId) { emit(doc.title, [doc.id, doc.title, doc.category, doc.imageUrl, doc.imageThumbnailUrl, doc.brandInfo, doc.brandId]); } }"),
		@View(name = "basedOnBrand", map = "function(doc) { if(doc.brandId) { emit(doc.brandId, [doc.id, doc.title, doc.category, doc.imageUrl, doc.imageThumbnailUrl, doc.brandInfo, doc.brandId]); } }") })
public class EGiftRepositoryService extends CouchDbRepositorySupport<EGift> {

	private static Logger logger = LoggerFactory
			.getLogger(EGiftRepositoryService.class);

	@Autowired
	public EGiftRepositoryService(@Qualifier("packpackDB") CouchDbConnector db) {
		super(EGift.class, db);
	}

	@Override
	public List<EGift> getAll() {
		logger.debug("getAll()");
		ViewQuery viewQuery = createQuery("basedOnCategory").group(true);
		return db.queryView(viewQuery, EGift.class);
	}

	public Page<EGift> getAll(PageRequest pageRequest) {
		logger.debug("getAll()");
		ViewQuery viewQuery = createQuery("basedOnCategory").group(true);
		return db.queryForPage(viewQuery, pageRequest, EGift.class);
	}

	public Pagination<EGift> getBasedOnTitle(String title, String pageLink) {
		PageRequest pr = (pageLink != null && !NULL_PAGE_LINK.equals(pageLink)) ? PageRequest
				.fromLink(pageLink) : PageRequest.firstPage(STANDARD_PAGE_SIZE);
		logger.debug("getBasedOnTitle(" + title + ", " + pr.asLink() + ")");
		ViewQuery viewQuery = createQuery("basedOnTitle").key(title)
				.descending(false);
		Page<EGift> page = db.queryForPage(viewQuery, pr, EGift.class);
		String nextLink = page.getNextLink();
		String previousLink = page.getPreviousLink();
		List<EGift> eGifts = page.getRows();
		return new Pagination<EGift>(previousLink, nextLink, eGifts);
	}

	public Pagination<EGift> getBasedOnCategory(String category, String pageLink) {
		PageRequest pr = (pageLink != null && !NULL_PAGE_LINK.equals(pageLink)) ? PageRequest
				.fromLink(pageLink) : PageRequest.firstPage(STANDARD_PAGE_SIZE);
		ViewQuery query = createQuery("basedOnCategory").key(category)
				.descending(false);
		Page<EGift> page = db.queryForPage(query, pr, EGift.class);
		String nextLink = page.getNextLink();
		String previousLink = page.getPreviousLink();
		List<EGift> eGifts = page.getRows();
		return new Pagination<EGift>(previousLink, nextLink, eGifts);
	}

	public Pagination<EGift> getBasedOnBrand(String brandId, String pageLink) {
		PageRequest pr = (pageLink != null && !NULL_PAGE_LINK.equals(pageLink)) ? PageRequest
				.fromLink(pageLink) : PageRequest.firstPage(STANDARD_PAGE_SIZE);
		ViewQuery query = createQuery("basedOnBrand").key(brandId).descending(
				false);
		Page<EGift> page = db.queryForPage(query, pr, EGift.class);
		String nextLink = page.getNextLink();
		String previousLink = page.getPreviousLink();
		List<EGift> eGifts = page.getRows();
		return new Pagination<EGift>(previousLink, nextLink, eGifts);
	}
}