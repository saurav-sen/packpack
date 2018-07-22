package com.pack.pack.rest.api;

import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE;
import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE_TIMESTAMP;

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
import com.pack.pack.model.web.dto.RssFeedSourceType;
import com.pack.pack.rest.api.security.interceptors.CompressWrite;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.redis.IRefreshmentFeedService;
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
@Path("/refreshment")
public class RefreshmentResource {

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
		return emptyResponse();
	}
	
	private Pagination<JRssFeed> emptyResponse() {
		Pagination<JRssFeed> page = new Pagination<JRssFeed>(END_OF_PAGE_TIMESTAMP);
		page.setNextLink(END_OF_PAGE);
		page.setPreviousLink(END_OF_PAGE);
		page.setResult(Collections.emptyList());
		return page;
	}
}
