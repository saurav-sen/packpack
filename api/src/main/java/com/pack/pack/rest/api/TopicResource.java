package com.pack.pack.rest.api;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.pack.pack.ITopicService;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.rest.api.security.interceptors.Compress;
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
	@Compress
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
	@Compress
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
	@Compress
	@Path("{pageLink}/user/{userId}")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Pagination<JTopic> getAllTopics(@PathParam("userId") String userId,
			@PathParam("pageLink") String pageLink) throws PackPackException {
		ITopicService service = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		return service.getAllTopicListing(userId, pageLink);
	}
	
	@GET
	@Compress
	@Path("{pageLink}/category/{category}/user/{userId}")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Pagination<JTopic> getAllTopicsFilteredByCategory(
			@PathParam("userId") String userId,
			@PathParam("pageLink") String pageLink,
			@PathParam("category") String category) throws PackPackException {
		ITopicService service = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		return service.getUserFollowedTopicsFilteredByCategory(userId, category, pageLink);
	}

	@POST
	@Compress
	@Produces(value = MediaType.APPLICATION_JSON)
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	public JTopic createNewTopic(
			@FormDataParam("name") String name,
			@FormDataParam("description") String description,
			@FormDataParam("ownerId") String ownerId,
			@FormDataParam("category") String category,
			@FormDataParam("wallpaper") InputStream wallpaper,
			@FormDataParam("wallpaper") FormDataContentDisposition aboutWallpaper)
			throws PackPackException {
		JTopic topic = new JTopic();
		topic.setName(name);
		topic.setDescription(description);
		topic.setFollowers(1);
		topic.setOwnerId(ownerId);
		topic.setCategory(category);
		ITopicService service = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		return service.createNewTopic(topic, wallpaper,
				aboutWallpaper.getFileName());
	}
}