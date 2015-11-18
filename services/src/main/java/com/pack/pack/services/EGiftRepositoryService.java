package com.pack.pack.services;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.Page;
import org.ektorp.PageRequest;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
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
@View(name="all", map="function(doc) { if(doc.brandInfo && doc.brandId) { emit(doc.category, [doc.title, doc.imageId, doc.brandInfo, doc.brandId]); }")
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
}