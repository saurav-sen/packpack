package com.pack.pack.services;

import static com.pack.pack.common.util.CommonConstants.NULL_PAGE_LINK;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.IMiscService;
import com.pack.pack.model.Comment;
import com.pack.pack.model.Discussion;
import com.pack.pack.model.Pack;
import com.pack.pack.model.PackAttachment;
import com.pack.pack.model.web.EntityType;
import com.pack.pack.model.web.JComment;
import com.pack.pack.model.web.JDiscussion;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.couchdb.DiscussionRepositoryService;
import com.pack.pack.services.couchdb.PackAttachmentRepositoryService;
import com.pack.pack.services.couchdb.PackRepositoryService;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.ModelConverter;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class MiscServiceImpl implements IMiscService {

	@Override
	public void addLike(String userId, String entityId, EntityType type)
			throws PackPackException {
		switch (type) {
		case TOPIC:
			break;
		case PACK: {
			PackRepositoryService service = ServiceRegistry.INSTANCE
					.findService(PackRepositoryService.class);
			Pack pack = service.get(entityId);
			if (pack != null) {
				pack.setLikes(pack.getLikes() + 1);
				service.update(pack);
			}
		}
			break;
		case PACK_ATTACHMENT: {
			PackAttachmentRepositoryService service = ServiceRegistry.INSTANCE
					.findService(PackAttachmentRepositoryService.class);
			PackAttachment packAttachment = service.get(entityId);
			if (packAttachment != null) {
				packAttachment.setLikes(packAttachment.getLikes() + 1);
				service.update(packAttachment);
			}
		}
			break;
		case COMMENT:
		case DISCUSSION:
		case REPLY: {
			DiscussionRepositoryService service = ServiceRegistry.INSTANCE
					.findService(DiscussionRepositoryService.class);
			Discussion discussion = service.get(entityId);
			if (discussion != null) {
				discussion.getLikeUsers().add(userId);
				discussion.setLikes(discussion.getLikeUsers().size());
				service.update(discussion);
			}
		}
			break;
		}
	}

	@Override
	public void addComment(String userId, String entityId, EntityType type,
			JComment comment) throws PackPackException {
		switch (type) {
		case TOPIC:
			break;
		case PACK: {
			PackRepositoryService service = ServiceRegistry.INSTANCE
					.findService(PackRepositoryService.class);
			Pack pack = service.get(entityId);
			if (pack == null) {
				throw new PackPackException(ErrorCodes.PACK_ERR_01,
						"Can't find pack with ID = " + entityId);
			}
			Comment c = ModelConverter.convert(comment);
			pack.getComments().add(c);
			service.update(pack);
		}
			break;
		case PACK_ATTACHMENT: {
			PackAttachmentRepositoryService service = ServiceRegistry.INSTANCE
					.findService(PackAttachmentRepositoryService.class);
			PackAttachment packAttachment = service.get(entityId);
			if (packAttachment == null) {
				throw new PackPackException(ErrorCodes.PACK_ERR_01,
						"Can't find attachment with ID = " + entityId);
			}
			Comment c = ModelConverter.convert(comment);
			packAttachment.getRecentComments().add(c);
			packAttachment.setComments(packAttachment.getComments() + 1);
			service.update(packAttachment);
		}
			break;
		case COMMENT:
		case DISCUSSION: {
			DiscussionRepositoryService service = ServiceRegistry.INSTANCE
					.findService(DiscussionRepositoryService.class);
			Discussion discussion = service.get(entityId);
			Discussion reply = new Discussion();
			reply.setDateTime(System.currentTimeMillis());
			reply.setStartedByUserId(userId);
			reply.setContent(comment.getComment());
			reply.setLikes(0);
			reply.setParentEntityId(discussion.getId());
			reply.setParentEntityType(EntityType.DISCUSSION.name());
			reply.setTag(EntityType.REPLY.name());
			service.add(reply);
		}
			break;
		case REPLY:
			break;
		}
	}

	@Override
	public JDiscussion startDiscussion(JDiscussion jDiscussion)
			throws PackPackException {
		return startDiscussion0(jDiscussion, EntityType.DISCUSSION.name());
	}

	private JDiscussion startDiscussion0(JDiscussion jDiscussion, String tag)
			throws PackPackException {
		DiscussionRepositoryService service = ServiceRegistry.INSTANCE
				.findService(DiscussionRepositoryService.class);
		Discussion discussion = ModelConverter.convert(jDiscussion);
		discussion.setTag(tag);
		discussion.setLikes(0);
		service.add(discussion);
		return ModelConverter.convert(discussion);
	}

	@Override
	public Pagination<JDiscussion> loadDiscussions(String userId,
			String entityId, String entityType, String pageLink)
			throws PackPackException {
		DiscussionRepositoryService service = ServiceRegistry.INSTANCE
				.findService(DiscussionRepositoryService.class);
		
		Pagination<Discussion> page = service.getAllDiscussions(entityId,
				entityType, pageLink);
		if (page == null) {
			return null;
		}
		List<Discussion> discussions = page.getResult();
		List<JDiscussion> jDiscussions = new LinkedList<JDiscussion>();
		if (EntityType.DISCUSSION.name().equals(entityType)
				&& (pageLink == null || NULL_PAGE_LINK.equals(pageLink) 
				|| "FIRST_PAGE".equals(pageLink))) {
			JDiscussion parentDiscussion = getDiscussionBasedOnId(entityId);
			jDiscussions.add(parentDiscussion);
		}
		
		if (discussions != null && !discussions.isEmpty()) {
			for (Discussion discussion : discussions) {
				JDiscussion jDiscussion = ModelConverter.convert(discussion);
				if (jDiscussion != null) {
					jDiscussions.add(jDiscussion);
				}
			}
		}
		Pagination<JDiscussion> result = new Pagination<JDiscussion>();
		result.setPreviousLink(page.getPreviousLink());
		result.setNextLink(page.getNextLink());
		result.setResult(jDiscussions);
		return result;
	}
	
	@Override
	public Pagination<JDiscussion> loadReplies(String discussionId,
			String pageLink) throws PackPackException {
		DiscussionRepositoryService service = ServiceRegistry.INSTANCE
				.findService(DiscussionRepositoryService.class);
		Pagination<Discussion> page = service.getAllReplies(discussionId, pageLink);
		if (page == null) {
			return null;
		}
		List<Discussion> discussions = page.getResult();
		if (discussions == null || discussions.isEmpty())
			return null;
		List<JDiscussion> jDiscussions = new LinkedList<JDiscussion>();
		for (Discussion discussion : discussions) {
			JDiscussion jDiscussion = ModelConverter.convert(discussion);
			if (jDiscussion != null) {
				jDiscussions.add(jDiscussion);
			}
		}
		Pagination<JDiscussion> result = new Pagination<JDiscussion>();
		result.setPreviousLink(page.getPreviousLink());
		result.setNextLink(page.getNextLink());
		result.setResult(jDiscussions);
		return result;
	}

	@Override
	public List<JComment> loadComments(String userId, String entityId,
			EntityType type) throws PackPackException {
		List<JComment> result = null;
		switch (type) {
		case TOPIC:
			break;
		case PACK: {
			PackRepositoryService service = ServiceRegistry.INSTANCE
					.findService(PackRepositoryService.class);
			Pack pack = service.get(entityId);
			if (pack == null) {
				throw new PackPackException(ErrorCodes.PACK_ERR_01,
						"Can't find pack with ID = " + entityId);
			}
			List<Comment> comments = pack.getComments();
			result = ModelConverter.convertComments(comments);
		}
			break;
		case PACK_ATTACHMENT: {
			PackAttachmentRepositoryService service = ServiceRegistry.INSTANCE
					.findService(PackAttachmentRepositoryService.class);
			PackAttachment packAttachment = service.get(entityId);
			if (packAttachment == null) {
				throw new PackPackException(ErrorCodes.PACK_ERR_01,
						"Can't find attachment with ID = " + entityId);
			}
			Collection<Comment> comments = packAttachment.getRecentComments();
			result = ModelConverter.convertComments(comments);
		}
			break;
		case COMMENT:
			break;
		case DISCUSSION: 
			break;
		case REPLY: 
			break;
		}
		return result;
	}

	@Override
	public JDiscussion getDiscussionBasedOnId(String id)
			throws PackPackException {
		DiscussionRepositoryService service = ServiceRegistry.INSTANCE
				.findService(DiscussionRepositoryService.class);
		Discussion discussion = service.get(id);
		if (discussion != null) {
			return ModelConverter.convert(discussion);
		}
		return null;
	}
}
