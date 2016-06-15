package com.pack.pack.services.couchdb;

import java.util.List;

import javax.annotation.PostConstruct;

import org.ektorp.CouchDbConnector;
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

import com.pack.pack.model.PersistedUserToken;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
@Views({
	@View(name="findByRefreshToken", map="function(doc) { if(doc.refreshToken) { emit(doc.refreshToken); } }")
})
public class PersistedUserTokenRepositoryService extends
		CouchDbRepositorySupport<PersistedUserToken> {
	
	private static Logger logger = LoggerFactory.getLogger(PersistedUserTokenRepositoryService.class);

	@Autowired
	public PersistedUserTokenRepositoryService(
			@Qualifier("packDB") CouchDbConnector db) {
		super(PersistedUserToken.class, db);
	}
	
	@PostConstruct
	public void doInit() {
		initStandardDesignDocument();
	}
	
	public PersistedUserToken findByRefreshToken(String refreshToken) {
		logger.info("Querying based upon refresh token: " + refreshToken);
		ViewQuery query = createQuery("findByRefreshToken").key(refreshToken)
				.includeDocs(true);
		List<PersistedUserToken> list = db.queryView(query, PersistedUserToken.class);
		if(list == null || list.isEmpty())
			return null;
		return list.get(0);
	}
}