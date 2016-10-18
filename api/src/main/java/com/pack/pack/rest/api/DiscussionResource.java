package com.pack.pack.rest.api;

import static com.pack.pack.util.SystemPropertyUtil.TOPIC;
import static com.pack.pack.util.SystemPropertyUtil.URL_SEPARATOR;

import java.util.Collections;
import java.util.List;

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
import com.pack.pack.IUserService;
import com.pack.pack.common.util.CommonConstants;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.EntityType;
import com.pack.pack.model.web.JComment;
import com.pack.pack.model.web.JDiscussion;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.model.web.dto.CommentDTO;
import com.pack.pack.model.web.dto.DiscussionDTO;
import com.pack.pack.model.web.dto.LikeDTO;
import com.pack.pack.rest.api.security.interceptors.CompressRead;
import com.pack.pack.rest.api.security.interceptors.CompressWrite;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path("/discussion")
public class DiscussionResource {

	@GET
	@CompressWrite
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public JDiscussion getDiscussionById(@PathParam("id") String id)
			throws PackPackException {
		IMiscService service = ServiceRegistry.INSTANCE
				.findCompositeService(IMiscService.class);
		JDiscussion jDiscussion = service.getDiscussionBasedOnId(id);
		IUserService userService = ServiceRegistry.INSTANCE.findService(IUserService.class);
		JUser user = userService.findUserById(jDiscussion.getFromUserId());
		jDiscussion.setFromUser(user);
		return jDiscussion;
	}

	@POST
	@CompressRead
	@Path("favourite/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus addLikeToDiscussion(@PathParam("id") String id, String json)
			throws PackPackException {
		JStatus status = new JStatus();
		try {
			LikeDTO dto = JSONUtil.deserialize(json, LikeDTO.class, true);
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
	@CompressRead
	@Path("favourite/reply/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus addLikeToReply(@PathParam("id") String id, String json)
			throws PackPackException {
		JStatus status = new JStatus();
		try {
			LikeDTO dto = JSONUtil.deserialize(json, LikeDTO.class, true);
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
	@CompressRead
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus addReplyToDiscussion(@PathParam("id") String id,
			String json) throws PackPackException {
		JStatus status = new JStatus();
		try {
			CommentDTO dto = JSONUtil.deserialize(json, CommentDTO.class, true);
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
	@CompressWrite
	@Path(TOPIC + URL_SEPARATOR + "{topicId}" + URL_SEPARATOR
			+ "usr/{userId}/page/{pageLink}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JDiscussion> getAllDiscussionsForTopic(
			@PathParam("topicId") String topicId,
			@PathParam("userId") String userId,
			@PathParam("pageLink") String pageLink) throws PackPackException {
		IMiscService service = ServiceRegistry.INSTANCE
				.findCompositeService(IMiscService.class);
		Pagination<JDiscussion> page = service.loadDiscussions(userId, topicId, EntityType.TOPIC.name(),
				pageLink);
		IUserService userService = ServiceRegistry.INSTANCE.findService(IUserService.class);
		List<JDiscussion> result = page != null ? page.getResult() : Collections.emptyList();
		for(JDiscussion r : result) {
			JUser user = userService.findUserById(r.getFromUserId());
			r.setFromUser(user);
		}
		Pagination<JDiscussion> pr = new Pagination<JDiscussion>();
		pr.setNextLink(page != null ? page.getNextLink() : CommonConstants.END_OF_PAGE);
		pr.setPreviousLink(page != null ? page.getPreviousLink() : CommonConstants.END_OF_PAGE);
		pr.setResult(result);
		return pr;
	}

	@GET
	@CompressWrite
	@Path("pack/{packId}/usr/{userId}/page/{pageLink}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JDiscussion> getAllDiscussionsForPack(
			@PathParam("packId") String packId,
			@PathParam("userId") String userId,
			@PathParam("pageLink") String pageLink) throws PackPackException {
		IMiscService service = ServiceRegistry.INSTANCE
				.findCompositeService(IMiscService.class);
		Pagination<JDiscussion> page = service.loadDiscussions(userId, packId, EntityType.PACK.name(),
				pageLink);
		IUserService userService = ServiceRegistry.INSTANCE.findService(IUserService.class);
		List<JDiscussion> result = page != null ? page.getResult() : Collections.emptyList();
		for(JDiscussion r : result) {
			JUser user = userService.findUserById(r.getFromUserId());
			r.setFromUser(user);
		}
		Pagination<JDiscussion> pr = new Pagination<JDiscussion>();
		pr.setNextLink(page != null ? page.getNextLink() : CommonConstants.END_OF_PAGE);
		pr.setPreviousLink(page != null ? page.getPreviousLink() : CommonConstants.END_OF_PAGE);
		pr.setResult(result);
		return pr;
	}
	
	@GET
	@CompressWrite
	@Path("discussion/{discussionId}/usr/{userId}/page/{pageLink}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JDiscussion> getAllRepliesForDiscussion(
			@PathParam("discussionId") String discussionId,
			@PathParam("userId") String userId,
			@PathParam("pageLink") String pageLink) throws PackPackException {
		IMiscService service = ServiceRegistry.INSTANCE
				.findCompositeService(IMiscService.class);
		Pagination<JDiscussion> page = service.loadDiscussions(userId,
				discussionId, EntityType.DISCUSSION.name(), pageLink);
		IUserService userService = ServiceRegistry.INSTANCE
				.findService(IUserService.class);
		List<JDiscussion> result = page != null ? page.getResult()
				: Collections.emptyList();
		for (JDiscussion r : result) {
			JUser user = userService.findUserById(r.getFromUserId());
			r.setFromUser(user);
		}
		Pagination<JDiscussion> pr = new Pagination<JDiscussion>();
		pr.setNextLink(page != null ? page.getNextLink()
				: CommonConstants.END_OF_PAGE);
		pr.setPreviousLink(page != null ? page.getPreviousLink()
				: CommonConstants.END_OF_PAGE);
		pr.setResult(result);
		return pr;
	}

	@PUT
	@CompressRead
	@CompressWrite
	@Path("topic/{topicId}/usr/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JDiscussion startDiscussionOnTopic(
			@PathParam("topicId") String topicId,
			@PathParam("userId") String userId, String json)
			throws PackPackException {
		DiscussionDTO dto = JSONUtil.deserialize(json, DiscussionDTO.class, true);
		JDiscussion discussion = new JDiscussion();
		discussion.setDateTime(System.currentTimeMillis());
		discussion.setContent(dto.getContent());
		discussion.setTitle(dto.getTitle());
		discussion.setFromUserId(userId);
		discussion.setParentId(topicId);
		discussion.setParentType(EntityType.TOPIC.name());
		IMiscService service = ServiceRegistry.INSTANCE
				.findCompositeService(IMiscService.class);
		JDiscussion result = service.startDiscussion(discussion);
		IUserService userService = ServiceRegistry.INSTANCE.findService(IUserService.class);
		JUser user = userService.findUserById(result.getFromUserId());
		result.setFromUser(user);
		return result;
	}

	@PUT
	@CompressRead
	@CompressWrite
	@Path("pack/{packId}/usr/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JDiscussion startDiscussionOnPack(
			@PathParam("packId") String packId,
			@PathParam("userId") String userId, String json)
			throws PackPackException {
		DiscussionDTO dto = JSONUtil.deserialize(json, DiscussionDTO.class, true);
		JDiscussion discussion = new JDiscussion();
		discussion.setDateTime(System.currentTimeMillis());
		discussion.setContent(dto.getContent());
		discussion.setTitle(dto.getTitle());
		discussion.setFromUserId(userId);
		discussion.setParentId(packId);
		discussion.setParentType(EntityType.PACK.name());
		IMiscService service = ServiceRegistry.INSTANCE
				.findCompositeService(IMiscService.class);
		JDiscussion result = service.startDiscussion(discussion);	
		IUserService userService = ServiceRegistry.INSTANCE.findService(IUserService.class);
		JUser user = userService.findUserById(result.getFromUserId());
		result.setFromUser(user);
		return result;
	}
}