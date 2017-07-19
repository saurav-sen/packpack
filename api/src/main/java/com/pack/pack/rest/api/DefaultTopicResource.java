package com.pack.pack.rest.api;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.IRssFeedService;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.rest.api.security.interceptors.CompressWrite;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;

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
			@PathParam("pageLink") String pageLink)
			throws PackPackException {
		IRssFeedService service = ServiceRegistry.INSTANCE
				.findCompositeService(IRssFeedService.class);
		return service.getAllRssFeeds(userId, pageLink, null, null);
	}
	
	@GET
	@CompressWrite
	@Path("usr/{userId}/page/{pageLink}/version/{version}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JRssFeed> getRssFeeds(@PathParam("userId") String userId,
			@PathParam("pageLink") String pageLink,
			@PathParam("version") String apiVersion,
			@QueryParam("source") String source)
			throws PackPackException {
		IRssFeedService service = ServiceRegistry.INSTANCE
				.findCompositeService(IRssFeedService.class);
		return service.getAllRssFeeds(userId, pageLink, source, apiVersion);
	}
}
