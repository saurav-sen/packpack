package com.pack.pack.services.couchdb;

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

import com.pack.pack.model.Comment;
import com.pack.pack.model.Pack;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
@Views({
	@View(name="getAll", map="function(doc) { if(doc.packImageId) { emit(doc.creationTime, doc); } }"),
	@View(name="findPackById", map="function(doc) { if(doc._id && doc.creatorId) { emit(doc.id, doc); }}")
})
public class PackRepositoryService extends CouchDbRepositorySupport<Pack>{
	
	private static Logger logger = LoggerFactory.getLogger(PackRepositoryService.class);

	@Autowired
	public PackRepositoryService(@Qualifier("packDB") CouchDbConnector db) {
		super(Pack.class, db);
	}
	
	@PostConstruct
	public void doInit() {
		initStandardDesignDocument();
	}
	
	public void addComment(Comment comment, String packId) {
		logger.debug("addComment");
		logger.debug("Comment = " + comment);
		logger.debug("PackID = " + packId);
		comment.setPackId(packId);
		db.create(comment);
		Pack pack = findBasedOnId(packId);
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
		logger.debug("Successfully added comment");
	}
	
	public void addLike(String userId, String packId) {
		logger.debug("addLike");
		logger.debug("UserID = " + userId);
		logger.debug("PackID = " + packId);
		Pack pack = findBasedOnId(packId);
		if(pack != null) {
			pack.setLikes(pack.getLikes() + 1);
			db.update(pack);
		}
		logger.debug("Successfully added like to pack");
	}
	
	public List<Pack> getAllPacks() {
		logger.debug("getAll()");
		ViewQuery query = createQuery("getAll").descending(true).includeDocs(true);
		return db.queryView(query, Pack.class);
	}
	
	public Page<Pack> getAllPacks(PageRequest page) {
		logger.debug("getAll() with pagination");
		ViewQuery query = createQuery("getAll").descending(true).includeDocs(true);
		return db.queryForPage(query, page, Pack.class);
	}
	
	public Pack findBasedOnId(String packId) {
		logger.debug("findById(" + packId + ")");
		ViewQuery query = createQuery("findPackById").key(packId);
		List<Pack> list = db.queryView(query, Pack.class);
		if(list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	
	public List<Pack> getAllPacks(List<String> ids) {
		logger.debug("getAllPacks(...) for given set of IDs");
		ViewQuery query = createQuery("findPackById").keys(ids);
		return db.queryView(query, Pack.class);
	}
}