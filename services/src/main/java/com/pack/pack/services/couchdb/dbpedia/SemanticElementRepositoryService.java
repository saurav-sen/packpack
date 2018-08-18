package com.pack.pack.services.couchdb.dbpedia;

import java.util.List;

import javax.annotation.PostConstruct;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.ektorp.support.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.SemanticElement;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
@Views({
		@View(name = "findByConceptId", map = "function(doc) { if(doc.id) { emit(doc.id); } }"),
		@View(name = "findDbpediaRefLink", map = "function(doc) { if(doc.dbpediaRef) { emit(doc.dbpediaRef); } }") })
public class SemanticElementRepositoryService extends
		CouchDbRepositorySupport<SemanticElement> {

	@Autowired
	public SemanticElementRepositoryService(
			@Qualifier("packDB") CouchDbConnector db) {
		super(SemanticElement.class, db);
	}
	
	@PostConstruct
	public void doInit() {
		initStandardDesignDocument();
	}

	public SemanticElement findByConceptId(String conceptId) {
		ViewQuery query = createQuery("findConceptById").key(conceptId);
		List<SemanticElement> result = db.queryView(query,
				SemanticElement.class);
		if (result == null || result.isEmpty())
			return null;
		return result.get(0);
	}

	public SemanticElement findDbpediaRefLink(String dbpediaRef) {
		ViewQuery query = createQuery("findDbpediaRefLink").key(dbpediaRef);
		List<SemanticElement> result = db.queryView(query,
				SemanticElement.class);
		if (result == null || result.isEmpty())
			return null;
		return result.get(0);
	}
}
