package com.pack.pack.rest.api;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger $_LOG = LoggerFactory
			.getLogger(RefreshmentResource.class);

	@GET
	@CompressWrite
	@Path("usr/{userId}/page/{pageLink}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JRssFeed> getRssFeeds(@PathParam("userId") String userId,
			@PathParam("pageLink") String pageLink,
			@QueryParam("source") String source) throws PackPackException {
		int pageNo = -1;
		if (pageLink != null && !pageLink.trim().isEmpty()) {
			try {
				pageNo = Integer.parseInt(pageLink.trim());
			} catch (NumberFormatException e) {
				$_LOG.error(e.getMessage(), e);
			}
		}
		if (source == null || source.trim().isEmpty()
				|| "default".equals(source)
				|| RssFeedSourceType.SQUILL_TEAM.equals(source)
				|| JRssFeedType.REFRESHMENT.name().equals(source)) {
			IRefreshmentFeedService service = ServiceRegistry.INSTANCE
					.findCompositeService(IRefreshmentFeedService.class);
			return service.getAllRssFeeds(userId, pageNo);
		}
		return emptyResponse();
	}

	private Pagination<JRssFeed> emptyResponse() {
		Pagination<JRssFeed> page = new Pagination<JRssFeed>();
		page.setNextPageNo(-1);
		return page;
	}
}
