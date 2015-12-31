package com.pack.pack.rest.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.ITopicService;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.model.web.dto.TopicFollowDTO;
import com.pack.pack.services.couchdb.Pagination;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@Path("activity/topic")
public class TopicActivityResource {
	
	@GET
	@Path("{pageLink}/user/{userId}")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Pagination<JTopic> getAllTopicsFollowedByUser(
			@PathParam("userId") String userId,
			@PathParam("pageLink") String pageLink) throws PackPackException {
		ITopicService service = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		return service.getUserFollowedTopics(userId, pageLink);
	}
	
	@POST
	@Produces(value = MediaType.APPLICATION_JSON)
	@Consumes(value = MediaType.APPLICATION_JSON)
	public JStatus followTopic(TopicFollowDTO dto) throws PackPackException {
		String topicId = dto.getTopicId();
		String userId = dto.getUserId();
		ITopicService service = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		service.followTopic(userId, topicId);
		JStatus status = new JStatus();
		status.setStatus(StatusType.OK);
		status.setInfo("OK");
		return status;
	}

	@DELETE
	@Path("topic/{topicId}/user/{userId}")
	public JStatus neglectTopic(@PathParam("userId") String userId,
			@PathParam("topicId") String topicId) throws PackPackException {
		ITopicService service = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		service.neglectTopic(userId, topicId);
		JStatus status = new JStatus();
		status.setStatus(StatusType.OK);
		status.setInfo("OK");
		return status;
	}
}