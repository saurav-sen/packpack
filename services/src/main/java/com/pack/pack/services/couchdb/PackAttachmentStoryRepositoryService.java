package com.pack.pack.services.couchdb;

import javax.annotation.PostConstruct;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.PackAttachmentStory;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class PackAttachmentStoryRepositoryService extends
		CouchDbRepositorySupport<PackAttachmentStory> {

	@Autowired
	public PackAttachmentStoryRepositoryService(@Qualifier("packDB") CouchDbConnector db) {
		super(PackAttachmentStory.class, db);
	}
	
	@PostConstruct
	public void doInit() {
		initStandardDesignDocument();
	}
}
