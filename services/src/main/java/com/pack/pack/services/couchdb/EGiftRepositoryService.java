package com.pack.pack.services.couchdb;

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

import com.pack.pack.model.EGift;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
@Views({
	@View(name="all", map="function(doc) { if(doc.brandInfo && doc.brandId) { emit(doc.category, [doc.title, doc.imageId, doc.brandInfo, doc.brandId]); } }"),
	@View(name="basedOnTitle", map="function(doc) { if(doc.title && doc.brandId) { emit(doc.title, [doc.category, doc.imageId, doc.brandInfo, doc.brandId]); } }")
})
public class EGiftRepositoryService extends CouchDbRepositorySupport<EGift>{

	@Autowired
	public EGiftRepositoryService(@Qualifier("packpackDB") CouchDbConnector db) {
		super(EGift.class, db);
	}
	
	@Override
	public List<EGift> getAll() {
		ViewQuery viewQuery = createQuery("all").group(true);
		return db.queryView(viewQuery, EGift.class);
	}
	
	public Page<EGift> getAll(PageRequest pageRequest) {
		ViewQuery viewQuery = createQuery("all").group(true);
		return db.queryForPage(viewQuery, pageRequest, EGift.class);
	}
	
	public Page<EGift> getBasedOnTitle(String title, PageRequest page) {
		ViewQuery viewQuery = createQuery("basedOnTitle").key(title).descending(false);
		return db.queryForPage(viewQuery, page, EGift.class);
	}
}