package com.pack.pack.rest.api;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.dto.RssFeedSourceType;
import com.pack.pack.rest.api.security.interceptors.CompressWrite;
import com.pack.pack.rss.IRefreshmentFeedService;
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
@Path("/home")
public class DefaultTopicResource {

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
				|| RssFeedSourceType.SQUILL_TEAM.equals(source)
				|| JRssFeedType.REFRESHMENT.name().equals(source)) {
			IRefreshmentFeedService service = ServiceRegistry.INSTANCE
					.findCompositeService(IRefreshmentFeedService.class);
			return service.getAllRssFeeds(userId, pageLink);
		}
		return null;
	}
}
