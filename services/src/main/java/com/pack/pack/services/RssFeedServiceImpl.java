package com.pack.pack.services;

import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE;
import static com.pack.pack.common.util.CommonConstants.NULL_PAGE_LINK;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.IRssFeedService;
import com.pack.pack.model.RSSFeed;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.TTL;
import com.pack.pack.model.web.dto.RssFeedSourceType;
import com.pack.pack.services.couchdb.RssFeedRepositoryService;
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
	
	private static final Logger LOG = LoggerFactory.getLogger(RssFeedServiceImpl.class);
	
	@Override
	public Pagination<JRssFeed> getAllRssFeeds(String userId, String pageLink, String source, String apiVersion)
			throws PackPackException {
		if (pageLink == null || END_OF_PAGE.equals(pageLink.trim())) {
			Pagination<JRssFeed> page = new Pagination<JRssFeed>();
			page.setNextLink(END_OF_PAGE);
			page.setPreviousLink(END_OF_PAGE);
			page.setResult(Collections.emptyList());
			return page;
		}
		/*UserLocationRepositoryService locationService = ServiceRegistry.INSTANCE
				.findService(UserLocationRepositoryService.class);
		UserLocation location = locationService.findUserLocationById(userId);*/

		/* **************************************************************************************************************************** */
		// Here we need to load from different place if it is already loaded for
		// the day (or couple-of-hours) once &
		// have B+-Tree implementation for our own purpose to minimize
		// comparison (with user-location retrieved as above) to make it
		// as much responsive as possible.
		RssFeedRepositoryService repositoryService = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		List<RSSFeed> feeds = Collections.emptyList();
		LOG.debug("source = " + source);
		if(source == null || source.trim().isEmpty() || "default".equals(source) || RssFeedSourceType.SQUILL_TEAM.equals(source)) {
			feeds = repositoryService.getAllPromotionalFeeds();
			LOG.debug("Promotional Feeds Count = " + feeds.size());
		} else if(RssFeedSourceType.NEWS_API.equals(source)) {
			feeds = repositoryService.getAllNewsFeeds();
			LOG.debug("News Feeds Count = " + feeds.size());
		}
		/* **************************************************************************************************************************** */

		boolean ignoreVideoFeeds = true;
		boolean ignoreSlideShows = true;
		if(apiVersion != null && apiVersion.equalsIgnoreCase("v2")) {
			ignoreVideoFeeds = false;
			ignoreSlideShows = false;
		}
		// For now (for demo purpose lets just return all the feeds).
		List<RSSFeed> result = feeds;
		List<JRssFeed> rows = ModelConverter.convertAllRssFeeds(result, ignoreVideoFeeds, ignoreSlideShows);
		Collections.sort(rows, new Comparator<JRssFeed>() {
			@Override
			public int compare(JRssFeed o1, JRssFeed o2) {
				try {
					long l = Long.parseLong(o2.getId().trim()) - Long.parseLong(o1.getId().trim());
					if(l == 0) {
						return 0;
					}
					if(l > 0) {
						return 1;
					}
					return -1;
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					return 0;
				}
			}
		});
		Pagination<JRssFeed> page = new Pagination<JRssFeed>();
		page.setNextLink(END_OF_PAGE);
		page.setPreviousLink(NULL_PAGE_LINK);
		page.setResult(rows);
		return page;
	}
	
	@Override
	public boolean upload(JRssFeed feed, TTL ttl) throws PackPackException {
		//RSSFeed rssFeed = ModelConverter.convert(feed, String.valueOf(System.nanoTime()));
		//feed.setId(String.valueOf(System.nanoTime()));
		RSSFeed rssFeed = ModelConverter.convert(feed);
		RssFeedRepositoryService service = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		boolean checkFeedExists = service.checkFeedExists(feed);
		if(!checkFeedExists) {
			service.uploadPromotionalFeed(rssFeed, ttl);
		}
		return !checkFeedExists;
		//return ModelConverter.convert(rssFeed);
	}
	
	public static void main(String[] args) {
		System.out.println("http://www.photodestination.co.za/images/peter_blog_post/Great%20Seascapes%20Tips/8_Tsitsikamma%20sea%20and%20rocks%20by%20wildlife%20and%20conservation%20photographer%20peter%20chadwick.jpg".hashCode());
	}
	
	/*@Override
	public Pagination<JRssFeed> getAllRssFeeds(String userId, String pageLink)
			throws PackPackException {
		if (pageLink == null || END_OF_PAGE.equals(pageLink.trim())) {
			Pagination<JRssFeed> page = new Pagination<JRssFeed>();
			page.setNextLink(END_OF_PAGE);
			page.setPreviousLink(END_OF_PAGE);
			page.setResult(Collections.emptyList());
			return page;
		}
		UserLocationRepositoryService locationService = ServiceRegistry.INSTANCE
				.findService(UserLocationRepositoryService.class);
		UserLocation location = locationService.findUserLocationById(userId);

		long now = System.currentTimeMillis();
		long startTime = now - 2 * 60 * 60 * 1000; // 2 hours before
		long endTime = now + 2 * 60 * 60 * 1000;*/

		/* **************************************************************************************************************************** */
		// Here we need to load from different place if it is already loaded for
		// the day (or couple-of-hours) once &
		// have B+-Tree implementation for our own purpose to minimize
		// comparison (with user-location retrieved as above) to make it
		// as much responsive as possible.
		/*RssFeedRepositoryService repositoryService = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		List<RSSFeed> feeds = repositoryService.getAllPromotionalFeeds(
				startTime, endTime);*/
		/* **************************************************************************************************************************** */

		// For now (for demo purpose lets just return all the feeds).
		/*List<RSSFeed> result = feeds;
		List<JRssFeed> rows = ModelConverter.convertAllRssFeeds(result);
		Pagination<JRssFeed> page = new Pagination<JRssFeed>();
		page.setNextLink(END_OF_PAGE);
		page.setPreviousLink(NULL_PAGE_LINK);
		page.setResult(rows);
		return page;*/
	//}

	/*@Override
	public JRssFeed upload(JRssFeed feed, TTL ttl) {
		RSSFeed rssFeed = ModelConverter.convert(feed);
		long now = System.currentTimeMillis();
		rssFeed.setPromoStartTimestamp(String.valueOf(now));
		long expiry = ttl.getTime();
		TimeUnit unit = ttl.getUnit();
		switch(unit) {
		case NANOSECONDS:
			expiry = expiry / (1000*1000);
			break;
		case MICROSECONDS:
			expiry = expiry / 1000;
			break;
		case MILLISECONDS:
			break;
		case SECONDS:
			expiry = expiry * 1000;
			break;
		case MINUTES:
			expiry = expiry * 60 * 1000;
			break;
		case HOURS:
			expiry = expiry * 60 * 60 * 1000;
			break;
		case DAYS:
			expiry = expiry * 24 * 60 * 60 * 1000;
			break;
		}
		expiry = now + expiry;
		rssFeed.setPromoExpiryTimestamp(String.valueOf(expiry));
		RssFeedRepositoryService service = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		service.add(rssFeed);
		return ModelConverter.convert(rssFeed);
	}*/

	@Override
	public JRssFeed generateRssFeedForTopic(String topicId) {
		// TODO Auto-generated method stub
		return null;
	}
}