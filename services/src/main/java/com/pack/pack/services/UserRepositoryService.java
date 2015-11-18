package com.pack.pack.services;

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

import com.pack.pack.model.User;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
@Views({
	@View(name="basedOnCity", map="function(doc) { if(doc.address && doc.address.city) { emit(doc.address.city, [user.name, user.username]) } )}"),
	@View(name="basedOnState", map="function(doc) { if(doc.address && doc.address.state) { emit(doc.address.state, [user.name, user.username]) } )}"),
	@View(name="basedOnCountry", map="function(doc) { if(doc.address && doc.address.country) { emit(doc.address.country, [user.name, user.username]) } )}")
})

public class UserRepositoryService extends CouchDbRepositorySupport<User> {

	@Autowired
	public UserRepositoryService(@Qualifier("packpackDB") CouchDbConnector db) {
		super(User.class, db);
	}
	
	public Page<User> getBasedOnCity(String city, PageRequest page) {
		ViewQuery query = createQuery("basedOnCity").key(city);
		return db.queryForPage(query, page, User.class);
	}
	
	public Page<User> getBasedOnState(String state, PageRequest page) {
		ViewQuery query = createQuery("basedOnState").key(state);
		return db.queryForPage(query, page, User.class);
	}
	
	public Page<User> getBasedOnCountry(String country, PageRequest page) {
		ViewQuery query = createQuery("basedOnCountry").key(country);
		return db.queryForPage(query, page, User.class);
	}
}