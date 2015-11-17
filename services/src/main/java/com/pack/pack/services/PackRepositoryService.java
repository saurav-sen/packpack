package com.pack.pack.services;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.Page;
import org.ektorp.PageRequest;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.Comment;
import com.pack.pack.model.Pack;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class PackRepositoryService extends CouchDbRepositorySupport<Pack>{

	@Autowired
	public PackRepositoryService(@Qualifier("packpackDB") CouchDbConnector db) {
		super(Pack.class, db);
	}
	
	public void addComment(Comment comment, String packId) {
		comment.setPackId(packId);
		db.create(comment);
	}
	
	@Override
	@GenerateView
	public List<Pack> getAll() {
		ViewQuery query = createQuery("all").descending(true).includeDocs(true);
		return db.queryView(query, Pack.class);
	}
	
	public Page<Pack> getAll(PageRequest page) {
		ViewQuery query = createQuery("all").descending(true).includeDocs(true);
		return db.queryForPage(query, page, Pack.class);
	}
}