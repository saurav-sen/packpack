package com.pack.pack.rest.api;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.IRssFeedService;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.JRssFeeds;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.TTL;
import com.pack.pack.rest.api.security.interceptors.CompressRead;
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
			@PathParam("pageLink") String pageLink) throws PackPackException {
		IRssFeedService service = ServiceRegistry.INSTANCE
				.findCompositeService(IRssFeedService.class);
		return service.getAllRssFeeds(userId, pageLink);
	}
	
	/**
	 * 
	 * Should be protected by API key
	 * 
	 * @param bulk
	 * @return
	 * @throws PackPackException
	 */
	@PUT
	@CompressRead
	@CompressWrite
	@Path("bulk_upload")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JRssFeeds bulkUpload(String json) throws PackPackException {
		JRssFeeds bulk = JSONUtil.deserialize(json, JRssFeeds.class);
		List<JRssFeed> list = new LinkedList<JRssFeed>();
		List<JRssFeed> feeds = bulk.getFeeds();
		if(feeds == null || feeds.isEmpty())
			return bulk;
		TTL ttl = new TTL();
		ttl.setTime((short) 1);
		ttl.setUnit(TimeUnit.DAYS);
		IRssFeedService service = ServiceRegistry.INSTANCE
				.findCompositeService(IRssFeedService.class);
		for(JRssFeed feed : feeds) {
			JRssFeed f = service.upload(feed, ttl);
			list.add(f);
		}
		JRssFeeds result = new JRssFeeds();
		result.setFeeds(list);
		return result;
	}
}
