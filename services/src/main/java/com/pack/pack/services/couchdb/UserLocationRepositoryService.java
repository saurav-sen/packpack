package com.pack.pack.services.couchdb;

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

import com.pack.pack.model.UserLocation;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
@Views({ @View(name = "findUserLongLatById", map = "function(doc) { if(doc.userId && doc.longitude && doc.latitude) { emit(doc.userId); } }") })
public class UserLocationRepositoryService extends
		CouchDbRepositorySupport<UserLocation> {

	@Autowired
	public UserLocationRepositoryService(
			@Qualifier("packDB") CouchDbConnector db) {
		super(UserLocation.class, db);
	}
	
	@PostConstruct
	public void doInit() {
		initStandardDesignDocument();
	}

	public UserLocation findUserLocationById(String userId)
			throws PackPackException {
		ViewQuery query = createQuery("findUserLongLatById").key(userId);
		List<UserLocation> result = db.queryView(query, UserLocation.class);
		if (result == null || result.isEmpty())
			return null;
		return result.get(0);
	}
}