package com.pack.pack.rest.api;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.pack.pack.IRssFeedService;
import com.pack.pack.ITopicService;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.JTopics;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.model.web.dto.UserPromotion;
import com.pack.pack.rest.api.security.interceptors.Compress;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.GeoLocationUtil;
import com.pack.pack.util.GeoLocationUtil.GeoLocation;

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
	@Path("owner/{ownerId}")
	@Produces(value = MediaType.APPLICATION_JSON)
	public JTopics getAllTopicsOwnedByUser(@PathParam("ownerId") String userId)
			throws PackPackException {
		ITopicService service = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		return service.getAllTopicsOwnedByUser(userId);
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
			@FormDataParam("city") String city,
			@FormDataParam("country") String country,
			@FormDataParam("locality") String localityAddr,
			@FormDataParam("wallpaper") FormDataContentDisposition aboutWallpaper)
			throws PackPackException {
		JTopic topic = new JTopic();
		topic.setName(name);
		topic.setDescription(description);
		topic.setFollowers(1);
		topic.setOwnerId(ownerId);
		topic.setCategory(category);
		if(localityAddr != null && !localityAddr.trim().isEmpty()) {
			topic.setAddress(localityAddr + ", " + city + ", " + country);
		} else {
			topic.setAddress(city + ", " + country);
		}
		GeoLocation geoLocation = GeoLocationUtil.resolveGeoLocation(localityAddr, city, country);
		if(geoLocation != null) {
			topic.setLatitude(geoLocation.getLatitude());
			topic.setLongitude(geoLocation.getLongitude());
		}
		ITopicService service = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		return service.createNewTopic(topic, wallpaper,
				aboutWallpaper.getFileName());
	}
	
	@PUT
	@Path("promote")
	@Produces(value = MediaType.APPLICATION_JSON)
	@Consumes(value = MediaType.APPLICATION_JSON)
	public JStatus promoteTopic(String json) throws PackPackException {
		UserPromotion userPromotion = JSONUtil.deserialize(json, UserPromotion.class);
		String topicId = userPromotion.getObjectId();
		IRssFeedService feedService = ServiceRegistry.INSTANCE.findCompositeService(IRssFeedService.class);
		JRssFeed feedForTopic = feedService.generateRssFeedForTopic(topicId);
		JStatus status = new JStatus();
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully promoted topic");
		return status;
	}
	
	@PUT
	@Compress
	@Path("{topicId}/settings/{key}/{value}/usr/{userId}")
	@Produces(value = MediaType.APPLICATION_JSON)
	public JTopic editTopicSettings(@PathParam("topicId") String topicId,
			@PathParam("userId") String userId, @PathParam("key") String key,
			@PathParam("value") String value) throws PackPackException {
		ITopicService service = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		return service.editTopicSettings(topicId, key, value, userId);
	}
}