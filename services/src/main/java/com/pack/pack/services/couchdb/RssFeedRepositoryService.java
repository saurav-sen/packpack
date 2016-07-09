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

import com.pack.pack.model.RSSFeed;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
@Views({ @View(name = "findFeedPromotions", map = "function(doc) { if(doc.promoStartTimestamp && doc.promoExpiryTimestamp) { emit(doc.promoStartTimestamp + doc.promoExpiryTimestamp); } }") })
public class RssFeedRepositoryService extends CouchDbRepositorySupport<RSSFeed> {

	@Autowired
	public RssFeedRepositoryService(@Qualifier("packDB") CouchDbConnector db) {
		super(RSSFeed.class, db);
	}
	
	@PostConstruct
	public void doInit() {
		initStandardDesignDocument();
	}

	public List<RSSFeed> getAllPromotionalFeeds(long startTime, long expiryTime)
			throws PackPackException {
		String startKey = String.valueOf(startTime);
		String endKey = startKey + String.valueOf(expiryTime);
		ViewQuery query = createQuery("findFeedPromotions").startKey(startKey)
				.endKey(endKey);
		return db.queryView(query, RSSFeed.class);
	}
}