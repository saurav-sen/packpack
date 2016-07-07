package com.pack.pack.services;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.IRssFeedService;
import com.pack.pack.model.RSSFeed;
import com.pack.pack.model.UserLocation;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.couchdb.RssFeedRepositoryService;
import com.pack.pack.services.couchdb.UserLocationRepositoryService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.ModelConverter;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class RssFeedServiceImpl implements IRssFeedService {

	@Override
	public Pagination<JRssFeed> getAllRssFeeds(String userId)
			throws PackPackException {
		UserLocationRepositoryService locationService = ServiceRegistry.INSTANCE
				.findService(UserLocationRepositoryService.class);
		UserLocation location = locationService.findUserLocationById(userId);

		long now = System.currentTimeMillis();
		long startTime = now - 2 * 60 * 60 * 1000; // 2 hours before
		long endTime = now + 2 * 60 * 60 * 1000;

		/* **************************************************************************************************************************** */
		// Here we need to load from different place if it is already loaded for
		// the day (or couple-of-hours) once &
		// have B+-Tree implementation for our own purpose to minimize
		// comparison (with user-location retrieved as above) to make it
		// as much responsive as possible.
		RssFeedRepositoryService repositoryService = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		List<RSSFeed> feeds = repositoryService.getAllPromotionalFeeds(
				startTime, endTime);
		/* **************************************************************************************************************************** */

		// For now (for demo purpose lets just return all the feeds).
		List<RSSFeed> result = feeds;
		List<JRssFeed> rows = ModelConverter.convertAllRssFeeds(result);
		Pagination<JRssFeed> page = new Pagination<JRssFeed>();
		page.setNextLink(null);
		page.setPreviousLink(null);
		page.setResult(rows);
		return page;
	}
}