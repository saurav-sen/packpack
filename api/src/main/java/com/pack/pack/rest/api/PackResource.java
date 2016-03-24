package com.pack.pack.rest.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.IPackService;
import com.pack.pack.common.util.CommonConstants;
import com.pack.pack.model.Pack;
import com.pack.pack.model.User;
import com.pack.pack.model.web.JComment;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.model.web.dto.ForwardDTO;
import com.pack.pack.model.web.dto.LikeDTO;
import com.pack.pack.model.web.dto.PackReceipent;
import com.pack.pack.model.web.dto.PackReceipentType;
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
@Provider
@Path("/pack")
public class PackResource {

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public JPack getById(@PathParam("id") String id) throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.getPackById(id);
	}

	@GET
	@Path("usr/{userId}/page/{pageLink}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JPack> getAll(@PathParam("userId") String userId,
			@PathParam("pageLink") String pageLink) throws PackPackException {
		return getAll(userId, CommonConstants.DEFAULT_TOPIC_ID, pageLink);
	}

	@GET
	@Path("usr/{userId}/topic/{topicId}/page/{pageLink}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JPack> getAll(@PathParam("userId") String userId,
			@PathParam("topicId") String topicId,
			@PathParam("pageLink") String pageLink) throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.loadLatestPack(userId, topicId, pageLink);
	}

	@PUT
	@Path("{id}")
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
	}

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
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(value = MediaType.APPLICATION_JSON)
	public JComment addComment(JComment comment) throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.addComment(comment);
	}

	@PUT
	@Path("favourite")
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(value = MediaType.APPLICATION_JSON)
	public JStatus addLike(LikeDTO likeDTO) throws PackPackException {
		String userId = likeDTO.getUserId();
		String packId = likeDTO.getPackId();
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		service.addLike(userId, packId);
		JStatus status = new JStatus();
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully Executed");
		return status;
	}
}