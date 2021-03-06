package com.pack.pack.services.couchdb;

import java.util.List;

import javax.annotation.PostConstruct;

import org.ektorp.ComplexKey;
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

import com.pack.pack.model.User;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
@Views({
	@View(name="basedOnLocation", map="function(doc) { if(doc.address && doc.address.city && doc.address.state "
			+ "&& doc.address.country) { emit([doc.address.city, doc.address.state, doc.address.country], doc); } }"),
	@View(name="basedOnAddress", map="function(doc) { if(doc.address) { emit([doc.address.city, doc.address.state, doc.address.country], doc); } }"),
	@View(name="basedOnUsername", map="function(doc) { if(doc.username) { emit(doc.username); } }")
})
public class UserRepositoryService extends CouchDbRepositorySupport<User> {
	
	private static Logger logger = LoggerFactory.getLogger(UserRepositoryService.class);

	@Autowired
	public UserRepositoryService(@Qualifier("packDB") CouchDbConnector db) {
		super(User.class, db);
	}
	
	@PostConstruct
	public void doInit() {
		initStandardDesignDocument();
	}
	
	public Page<User> getBasedOnCity(String city, PageRequest page) {
		logger.debug("getBasedOnCity(city=" + city + ", page-link=" + page.asLink());
		ComplexKey key = ComplexKey.of(city, ComplexKey.emptyObject());
		ViewQuery query = createQuery("basedOnAddress").key(key);
		return db.queryForPage(query, page, User.class);
	}
	
	public Page<User> getBasedOnState(String state, PageRequest page) {
		logger.debug("getBasedOnState(state=" + state + ", page-link=" + page.asLink());
		ComplexKey key = ComplexKey.of(state, ComplexKey.emptyObject());
		ViewQuery query = createQuery("basedOnAddress").key(key);
		return db.queryForPage(query, page, User.class);
	}
	
	public Page<User> getBasedOnCountry(String country, PageRequest page) {
		logger.debug("getBasedOnCountry(country=" + country + ", page-link=" + page.asLink());
		ComplexKey key = ComplexKey.of(country, ComplexKey.emptyObject());
		ViewQuery query = createQuery("basedOnAddress").key(key);
		return db.queryForPage(query, page, User.class);
	}
	
	public List<User> getBasedOnUsername(String username) {
		logger.debug("getBasedOnUsername(username=" + username);
		ViewQuery query = createQuery("basedOnUsername").key(username)
				.includeDocs(true);
		return db.queryView(query, User.class);
	}
	
	public boolean validateCredential(String username, String password) {
		logger.debug("validateCredential(username=" + username);
		ViewQuery query = createQuery("basedOnUsername").key(username)
				.includeDocs(true);
		List<User> list = db.queryView(query, User.class);
		if(list == null || list.isEmpty()) {
			logger.debug("Not valid credential");
			return false;
		}
		if(list.size() > 1) {
			logger.debug("Not valid credential");
			return false;
		}
		User user = list.get(0);
		if(password != null && password.equals(user.getPassword())) {
			logger.debug("Valid credential");
			return true;
		}
		logger.debug("Not valid credential");
		return false;
	}
}