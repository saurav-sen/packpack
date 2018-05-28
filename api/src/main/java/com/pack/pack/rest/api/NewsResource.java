package com.pack.pack.rest.api;

import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE;
import static com.pack.pack.common.util.CommonConstants.PAGELINK_DIRECTION_NEGATIVE;
import static com.pack.pack.common.util.CommonConstants.PAGELINK_DIRECTION_POSITIVE;

import java.util.Collections;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.model.web.Pagination;
import com.pack.pack.rest.api.security.interceptors.CompressWrite;
import com.pack.pack.rss.INewsFeedService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path("/news")
public class NewsResource {

	@GET
	@CompressWrite
	@Path("usr/{userId}/page/{pageLink}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JRssFeed> getRssFeeds(@PathParam("userId") String userId,
			@PathParam("pageLink") String pageLink,
			@QueryParam("source") String source)
			throws PackPackException {
		if(source == null || source.trim().isEmpty()
				|| "default".equals(source)
				|| JRssFeedType.NEWS.name().equals(source)) {
			INewsFeedService service = ServiceRegistry.INSTANCE
					.findCompositeService(INewsFeedService.class);
			return service.getAllNewsRssFeeds(userId, pageLink);
		} else if(JRssFeedType.NEWS_SPORTS.name().equals(source)) {
			INewsFeedService service = ServiceRegistry.INSTANCE
					.findCompositeService(INewsFeedService.class);
			return service.getAllSportsNewsRssFeeds(userId, pageLink);
		} else if(JRssFeedType.NEWS_SCIENCE_TECHNOLOGY.name().equals(source)) {
			INewsFeedService service = ServiceRegistry.INSTANCE
					.findCompositeService(INewsFeedService.class);
			return service.getAllScienceAndTechnologyNewsRssFeeds(userId, pageLink);
		} else if(JRssFeedType.ARTICLE.name().equals(source)) {
			INewsFeedService service = ServiceRegistry.INSTANCE
					.findCompositeService(INewsFeedService.class);
			return service.getArticleNewsRssFeeds(userId, pageLink);
		}
		return emptyResponse();
	}
	
	private Pagination<JRssFeed> emptyResponse() {
		Pagination<JRssFeed> page = new Pagination<JRssFeed>();
		page.setNextLink(END_OF_PAGE + PAGELINK_DIRECTION_POSITIVE);
		page.setPreviousLink(END_OF_PAGE + PAGELINK_DIRECTION_NEGATIVE);
		page.setResult(Collections.emptyList());
		return page;
	}
}
