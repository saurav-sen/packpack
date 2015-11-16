package com.pack.pack.services;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
}