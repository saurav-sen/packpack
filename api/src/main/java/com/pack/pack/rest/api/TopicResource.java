package com.pack.pack.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.ITopicService;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.services.couchdb.Pagination;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@Path("/topic")
public class TopicResource {

	@GET
	@Path("{id}")
	@Produces(value = MediaType.APPLICATION_JSON)
	public JTopic getTopicById(@PathParam("id") String topicId)
			throws PackPackException {
		ITopicService service = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		return service.getTopicById(topicId);
	}

	@GET
	@Path("{pageLink}/user/{userId}")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Pagination<JTopic> getAllTopicsFollowedByUser(@PathParam("userId") String userId,
			@PathParam("pageLink") String pageLink)
			throws PackPackException {
		ITopicService service = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		return service.getUserFollowedTopics(userId, pageLink);
	}
}