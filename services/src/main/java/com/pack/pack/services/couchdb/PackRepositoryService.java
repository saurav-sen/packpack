package com.pack.pack.services.couchdb;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.Page;
import org.ektorp.PageRequest;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
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
@View(name="all", map="function(doc) { if(doc.packImageId) { emit(doc.creationTime, doc); } }")
public class PackRepositoryService extends CouchDbRepositorySupport<Pack>{

	@Autowired
	public PackRepositoryService(@Qualifier("packpackDB") CouchDbConnector db) {
		super(Pack.class, db);
	}
	
	public void addComment(Comment comment, String packId) {
		comment.setPackId(packId);
		db.create(comment);
		Pack pack = findById(packId);
		if(pack != null) {
			List<Comment> comments = pack.getRecentComments();
			if(comments.size() >= 5) {
				Comment c = comments.get(4);
				c.setComment(comment.getComment());
				c.setDateTime(comment.getDateTime());
				c.setFromUser(comment.getFromUser());
			}
			else {
				comments.add(comment);
			}
			pack.setComments(pack.getComments() + 1);
			db.update(pack);
		}
	}
	
	public void addLike(String userId, String packId) {
		Pack pack = findById(packId);
		if(pack != null) {
			pack.setLikes(pack.getLikes() + 1);
			db.update(pack);
		}
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
	
	public Pack findById(String packId) {
		return null;
	}
	
	public List<Pack> getAllPacks(List<String> ids) {
		return null;
	}
}