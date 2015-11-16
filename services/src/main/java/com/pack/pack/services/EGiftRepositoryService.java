package com.pack.pack.services;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.EGift;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class EGiftRepositoryService extends CouchDbRepositorySupport<EGift>{

	@Autowired
	public EGiftRepositoryService(@Qualifier("packpackDB") CouchDbConnector db) {
		super(EGift.class, db);
	}

}