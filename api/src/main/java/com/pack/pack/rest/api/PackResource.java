package com.pack.pack.rest.api;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.IMiscService;
import com.pack.pack.IPackService;
import com.pack.pack.common.util.CommonConstants;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.Pack;
import com.pack.pack.model.User;
import com.pack.pack.model.web.EntityType;
import com.pack.pack.model.web.JComment;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.model.web.dto.CommentDTO;
import com.pack.pack.model.web.dto.LikeDTO;
import com.pack.pack.model.web.dto.PackDTO;
import com.pack.pack.model.web.dto.PackReceipent;
import com.pack.pack.model.web.dto.PackReceipentType;
import com.pack.pack.rest.api.security.interceptors.CompressRead;
import com.pack.pack.rest.api.security.interceptors.CompressWrite;
import com.pack.pack.services.couchdb.PackRepositoryService;
import com.pack.pack.services.couchdb.UserRepositoryService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.ext.email.GmailMessageService;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path("/pack")
public class PackResource {

	@GET
	@CompressWrite
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public JPack getById(@PathParam("id") String id) throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.getPackById(id);
	}

	@POST
	@CompressRead
	@CompressWrite
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JPack createNewPack(String json) throws PackPackException {
		PackDTO dto = JSONUtil.deserialize(json, PackDTO.class, true);
		Pack pack = new Pack();
		pack.setCreatorId(dto.getUserId());
		pack.setTitle(dto.getTitle());
		pack.setStory(dto.getStory());
		pack.setPackParentTopicId(dto.getTopicId());
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.createNewPack(pack);
	}

	@GET
	@CompressWrite
	@Path("usr/{userId}/page/{pageLink}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JPack> getAll(@PathParam("userId") String userId,
			@PathParam("pageLink") String pageLink) throws PackPackException {
		return getAll(userId, CommonConstants.DEFAULT_TOPIC_ID, pageLink);
	}

	@GET
	@CompressWrite
	@Path("usr/{userId}/topic/{topicId}/page/{pageLink}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JPack> getAll(@PathParam("userId") String userId,
			@PathParam("topicId") String topicId,
			@PathParam("pageLink") String pageLink) throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.loadLatestPack(userId, topicId, pageLink);
	}

	@GET
	@CompressWrite
	@Path("items/usr/{userId}/topic/{topicId}/pack/{packId}/page/{pageLink}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JPackAttachment> getAllAttachments(
			@PathParam("userId") String userId,
			@PathParam("topicId") String topicId,
			@PathParam("packId") String packId,
			@PathParam("pageLink") String pageLink) throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.loadPackAttachments(userId, topicId, packId, pageLink);
	}

	@GET
	@CompressWrite
	@Path("items/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public JPackAttachment getPackAttachmentById(@PathParam("id") String id)
			throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.getPackAttachmentById(id);
	}

	/*@PUT	
	@Path("{id}")
	@CompressRead
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JStatus forwardPack(ForwardDTO dto, @PathParam("id") String id)
			throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		String fromUserId = dto.getFromUserId();
		List<PackReceipent> receipents = dto.getReceipents();
		service.forwardPack(id, fromUserId,
				receipents.toArray(new PackReceipent[receipents.size()]));
		JStatus status = new JStatus();
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully forwarded");
		return status;
	}*/

	@PUT
	@Path("{id}/email/{from}/{to}")
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus forwardPackOverEmail(@PathParam("id") String packId,
			@PathParam("from") String fromUserId,
			@PathParam("to") String toUserEmail) throws PackPackException {
		try {
			UserRepositoryService userRepositoryService = ServiceRegistry.INSTANCE
					.findService(UserRepositoryService.class);
			User fromUser = userRepositoryService.get(fromUserId);
			String fromUserEmail = fromUser.getUsername();
			PackReceipent packReceipent = new PackReceipent();
			packReceipent.setToUserId(toUserEmail);
			packReceipent.setType(PackReceipentType.EMAIL);
			PackRepositoryService packRepositoryService = ServiceRegistry.INSTANCE
					.findService(PackRepositoryService.class);
			Pack pack = packRepositoryService.get(packId);
			GmailMessageService emailService = ServiceRegistry.INSTANCE
					.findService(GmailMessageService.class);
			emailService.forwardPack(pack, packReceipent, fromUserEmail);
			JStatus status = new JStatus();
			status.setStatus(StatusType.OK);
			status.setInfo("Successfully forwarded");
			return status;
		} catch (Exception e) {
			throw new PackPackException("", "", e);
		}
	}

	@PUT
	@Path("comment")
	@CompressRead
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(value = MediaType.APPLICATION_JSON)
	public JStatus addComment(CommentDTO commentDTO) throws PackPackException {
		IMiscService service = ServiceRegistry.INSTANCE
				.findCompositeService(IMiscService.class);
		JComment comment = new JComment();
		comment.setComment(commentDTO.getComment());
		comment.setDateTime(System.currentTimeMillis());
		comment.setFromUserId(commentDTO.getFromUserId());
		String entityType = commentDTO.getEntityType();
		if (entityType == null || entityType.trim().isEmpty()) {
			JStatus status = new JStatus();
			status.setStatus(StatusType.ERROR);
			status.setInfo("Entity Type Not Valid");
			return status;
		}
		EntityType type = null;
		try {
			type = EntityType.valueOf(entityType.trim());
		} catch (Exception e) {
			JStatus status = new JStatus();
			status.setStatus(StatusType.ERROR);
			status.setInfo("Entity Type Not Valid");
			return status;
		}

		if (type == null) {
			JStatus status = new JStatus();
			status.setStatus(StatusType.ERROR);
			status.setInfo("Entity Type Not Valid");
			return status;
		}
		service.addComment(commentDTO.getFromUserId(),
				commentDTO.getEntityId(), type, comment);
		JStatus status = new JStatus();
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully Executed");
		return status;
	}

	@PUT
	@Path("favourite")
	@CompressRead
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(value = MediaType.APPLICATION_JSON)
	public JStatus addLike(LikeDTO likeDTO) throws PackPackException {
		String userId = likeDTO.getUserId();
		String entityId = likeDTO.getEntityId();
		String entityType = likeDTO.getEntityType();
		if (entityType == null || entityType.trim().isEmpty()) {
			JStatus status = new JStatus();
			status.setStatus(StatusType.ERROR);
			status.setInfo("Entity Type Not Valid");
			return status;
		}
		EntityType type = null;
		try {
			type = EntityType.valueOf(entityType.trim());
		} catch (Exception e) {
			JStatus status = new JStatus();
			status.setStatus(StatusType.ERROR);
			status.setInfo("Entity Type Not Valid");
			return status;
		}

		if (type == null) {
			JStatus status = new JStatus();
			status.setStatus(StatusType.ERROR);
			status.setInfo("Entity Type Not Valid");
			return status;
		}

		IMiscService service = ServiceRegistry.INSTANCE
				.findCompositeService(IMiscService.class);
		service.addLike(userId, entityId, type);
		JStatus status = new JStatus();
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully Executed");
		return status;
	}
}