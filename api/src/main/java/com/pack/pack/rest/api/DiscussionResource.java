package com.pack.pack.rest.api;

import static com.pack.pack.model.web.Constants.PACK_DISCUSSION;
import static com.pack.pack.model.web.Constants.TOPIC_DISCUSSION;
import static com.pack.pack.util.SystemPropertyUtil.TOPIC;
import static com.pack.pack.util.SystemPropertyUtil.URL_SEPARATOR;

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
import com.pack.pack.model.web.EntityType;
import com.pack.pack.model.web.JComment;
import com.pack.pack.model.web.JDiscussion;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.model.web.dto.CommentDTO;
import com.pack.pack.model.web.dto.DiscussionDTO;
import com.pack.pack.model.web.dto.LikeDTO;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@Path("/discussion")
public class DiscussionResource {

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public JDiscussion getDiscussionById(@PathParam("id") String id)
			throws PackPackException {
		IMiscService service = ServiceRegistry.INSTANCE
				.findCompositeService(IMiscService.class);
		return service.getDiscussionBasedOnId(id);
	}

	@POST
	@Path("favourite/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus addLikeToDiscussion(@PathParam("id") String id, LikeDTO dto)
			throws PackPackException {
		JStatus status = new JStatus();
		try {
			IMiscService service = ServiceRegistry.INSTANCE
					.findCompositeService(IMiscService.class);
			service.addLike(dto.getUserId(), id, EntityType.DISCUSSION);
		} catch (PackPackException e) {
			status.setStatus(StatusType.ERROR);
			status.setInfo("Failed submitted reply to discussion");
			throw e;
		} catch (Exception e) {
			status.setStatus(StatusType.ERROR);
			status.setInfo("Failed submitted reply to discussion");
			throw new PackPackException(ErrorCodes.PACK_ERR_61, e.getMessage());
		}
		return status;
	}

	@POST
	@Path("favourite/reply/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus addLikeToReply(@PathParam("id") String id, LikeDTO dto)
			throws PackPackException {
		JStatus status = new JStatus();
		try {
			IMiscService service = ServiceRegistry.INSTANCE
					.findCompositeService(IMiscService.class);
			service.addLike(dto.getUserId(), id, EntityType.REPLY);
		} catch (PackPackException e) {
			status.setStatus(StatusType.ERROR);
			status.setInfo("Failed submitted reply to discussion");
			throw e;
		} catch (Exception e) {
			status.setStatus(StatusType.ERROR);
			status.setInfo("Failed submitted reply to discussion");
			throw new PackPackException(ErrorCodes.PACK_ERR_61, e.getMessage());
		}
		return status;
	}

	@PUT
	@PathParam("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus addReplyToDiscussion(@PathParam("id") String id,
			CommentDTO dto) throws PackPackException {
		JStatus status = new JStatus();
		try {
			JComment jComment = new JComment();
			jComment.setFromUserId(dto.getFromUserId());
			jComment.setDateTime(System.currentTimeMillis());
			jComment.setComment(dto.getComment());
			IMiscService service = ServiceRegistry.INSTANCE
					.findCompositeService(IMiscService.class);
			service.addComment(dto.getFromUserId(), id, EntityType.DISCUSSION,
					jComment);
			status.setStatus(StatusType.OK);
			status.setInfo("Successfully submitted reply to discussion");
		} catch (PackPackException e) {
			status.setStatus(StatusType.ERROR);
			status.setInfo("Failed submitted reply to discussion");
			throw e;
		} catch (Exception e) {
			status.setStatus(StatusType.ERROR);
			status.setInfo("Failed submitted reply to discussion");
			throw new PackPackException(ErrorCodes.PACK_ERR_61, e.getMessage());
		}
		return status;
	}

	@GET
	@Path(TOPIC + URL_SEPARATOR + "{topicId}" + URL_SEPARATOR
			+ "usr/{userId}/page/{pageLink}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JDiscussion> getAllDiscussionsForTopic(
			@PathParam("topicId") String topicId,
			@PathParam("userId") String userId,
			@PathParam("pageLink") String pageLink) throws PackPackException {
		IMiscService service = ServiceRegistry.INSTANCE
				.findCompositeService(IMiscService.class);
		return service.loadDiscussions(userId, topicId, TOPIC_DISCUSSION,
				pageLink);
	}

	@GET
	@Path("pack/{packId}/usr/{userId}/page/{pageLink}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JDiscussion> getAllDiscussionsForPack(
			@PathParam("packId") String packId,
			@PathParam("userId") String userId,
			@PathParam("pageLink") String pageLink) throws PackPackException {
		IMiscService service = ServiceRegistry.INSTANCE
				.findCompositeService(IMiscService.class);
		return service.loadDiscussions(userId, packId, PACK_DISCUSSION,
				pageLink);
	}

	@PUT
	@Path("topic/{topicId}/user/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JDiscussion startDiscussionOnTopic(
			@PathParam("topicId") String topicId,
			@PathParam("userId") String userId, DiscussionDTO dto)
			throws PackPackException {
		JDiscussion discussion = new JDiscussion();
		discussion.setDateTime(System.currentTimeMillis());
		discussion.setContent(dto.getContent());
		discussion.setTitle(dto.getTitle());
		discussion.setFromUserId(userId);
		discussion.setParentId(topicId);
		discussion.setParentType(EntityType.TOPIC.name());
		IMiscService service = ServiceRegistry.INSTANCE
				.findCompositeService(IMiscService.class);
		return service.startDiscussion(discussion);
	}

	@PUT
	@Path("pack/{packId}/usr/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JDiscussion startDiscussionOnPack(
			@PathParam("packId") String packId,
			@PathParam("userId") String userId, DiscussionDTO dto)
			throws PackPackException {
		JDiscussion discussion = new JDiscussion();
		discussion.setDateTime(System.currentTimeMillis());
		discussion.setContent(dto.getContent());
		discussion.setTitle(dto.getTitle());
		discussion.setFromUserId(userId);
		discussion.setParentId(packId);
		discussion.setParentType(EntityType.PACK.name());
		IMiscService service = ServiceRegistry.INSTANCE
				.findCompositeService(IMiscService.class);
		return service.startDiscussion(discussion);
	}
}