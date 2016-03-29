package com.pack.pack.rest.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.ITopicService;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;
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

	/*@GET
	@Path("categories")
	@Produces(value = MediaType.APPLICATION_JSON)
	public JCategories getAllCategories() throws PackPackException {
		return null;
	}*/

	@GET
	@Path("{pageLink}/category/{categoryName}")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Pagination<JTopic> getTopicsByCategory(
			@PathParam("categoryName") String categoryName,
			@PathParam("pageLink") String pageLink) throws PackPackException {
		ITopicService service = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		return service.getAllTopicsByCategoryName(categoryName, pageLink);
	}

	@GET
	@Path("{pageLink}/user/{userId}")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Pagination<JTopic> getAllTopics(@PathParam("userId") String userId,
			@PathParam("pageLink") String pageLink) throws PackPackException {
		ITopicService service = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		return service.getAllTopicListing(userId, pageLink);
	}

	@POST
	@Produces(value = MediaType.APPLICATION_JSON)
	@Consumes(value = MediaType.APPLICATION_JSON)
	public JTopic add(JTopic topic) throws PackPackException {
		ITopicService service = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		return service.createNewTopic(topic);
	}
}