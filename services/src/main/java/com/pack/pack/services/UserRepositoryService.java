package com.pack.pack.services;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
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
public class UserRepositoryService extends CouchDbRepositorySupport<User> {

	@Autowired
	public UserRepositoryService(@Qualifier("packpackDB") CouchDbConnector db) {
		super(User.class, db);
	}
}