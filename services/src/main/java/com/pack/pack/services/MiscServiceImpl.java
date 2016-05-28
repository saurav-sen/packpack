package com.pack.pack.services;

import com.pack.pack.IMiscService;
import com.pack.pack.model.Comment;
import com.pack.pack.model.Pack;
import com.pack.pack.model.PackAttachment;
import com.pack.pack.model.web.JComment;
import com.pack.pack.services.couchdb.PackAttachmentRepositoryService;
import com.pack.pack.services.couchdb.PackRepositoryService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.ModelConverter;

/**
 * 
 * @author Saurav
 *
 */
public class MiscServiceImpl implements IMiscService {

	@Override
	public void addLike(String userId, String entityId, EntityType type)
			throws PackPackException {
		switch (type) {
		case PACK: {
			PackRepositoryService service = ServiceRegistry.INSTANCE
					.findService(PackRepositoryService.class);
			Pack pack = service.get(entityId);
			pack.setLikes(pack.getLikes() + 1);
			service.update(pack);
		}
			break;
		case PACK_ATTACHMENT: {
			PackAttachmentRepositoryService service = ServiceRegistry.INSTANCE
					.findService(PackAttachmentRepositoryService.class);
			PackAttachment packAttachment = service.get(entityId);
			packAttachment.setLikes(packAttachment.getLikes() + 1);
			service.update(packAttachment);
		}
			break;
		case COMMENT:
		case DISCUSSION:
			break;
		}
	}

	@Override
	public void addComment(String userId, String entityId, EntityType type,
			JComment comment) throws PackPackException {
		switch (type) {
		case PACK: {
			PackRepositoryService service = ServiceRegistry.INSTANCE
					.findService(PackRepositoryService.class);
			Pack pack = service.get(entityId);
			Comment c = ModelConverter.convert(comment);
			pack.getComments().add(c);
			service.update(pack);
		}
			break;
		case PACK_ATTACHMENT: {
			PackAttachmentRepositoryService service = ServiceRegistry.INSTANCE
					.findService(PackAttachmentRepositoryService.class);
			PackAttachment packAttachment = service.get(entityId);
			Comment c = ModelConverter.convert(comment);
			packAttachment.getRecentComments().add(c);
			packAttachment.setComments(packAttachment.getComments() + 1);
			service.update(packAttachment);
		}
			break;
		case COMMENT:
		case DISCUSSION:
			break;
		}
	}
}
