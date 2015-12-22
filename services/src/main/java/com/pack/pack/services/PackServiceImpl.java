package com.pack.pack.services;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.IPackService;
import com.pack.pack.message.FwdPack;
import com.pack.pack.model.Comment;
import com.pack.pack.model.Pack;
import com.pack.pack.model.PackAttachment;
import com.pack.pack.model.PackAttachmentType;
import com.pack.pack.model.Topic;
import com.pack.pack.model.User;
import com.pack.pack.model.web.JComment;
import com.pack.pack.model.web.JPack;
import com.pack.pack.services.couchdb.PackRepositoryService;
import com.pack.pack.services.couchdb.TopicRepositoryService;
import com.pack.pack.services.couchdb.UserRepositoryService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.rabbitmq.MessagePublisher;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.AttachmentUtil;
import com.pack.pack.util.ModelConverter;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class PackServiceImpl implements IPackService {

	@Override
	public JPack getPackById(String id) throws PackPackException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @Override public void uploadPack(JPack jPack, InputStream attachment,
	 * String userId, PackAttachmentType type) throws PackPackException {
	 * PackRepositoryService repoService = ServiceRegistry.INSTANCE
	 * .findService(PackRepositoryService.class); Pack pack = new Pack();
	 * pack.setCreationTime(new DateTime()); pack.setCreatorId(userId);
	 * //pack.getPackImageUrls().add(null); // TODO -- save input stream in
	 * couch DB pack.setStory(jPack.getStory());
	 * pack.setTitle(jPack.getTitle()); repoService.add(pack); }
	 */

	@Override
	public void forwardPack(String packId, String fromUserId, String... userIds)
			throws PackPackException {
		// TODO Auto-generated method stub
		PackRepositoryService repoService = ServiceRegistry.INSTANCE
				.findService(PackRepositoryService.class);
		Pack pack = repoService.findById(packId);
		FwdPack fwdPack = new FwdPack();
		fwdPack.setAccessUrl(null);// TODO
		// fwdPack.setComments(pack.);
		fwdPack.setFromUserId(fromUserId);
		UserRepositoryService userRepoService = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		User user = userRepoService.get(fromUserId);
		fwdPack.setFromUserName(user.getName());
		fwdPack.setFromUserProfilePicUrl(null); // TODO
		fwdPack.setLikes(pack.getLikes());
		fwdPack.setViews(pack.getViews());
		MessagePublisher messagingService = ServiceRegistry.INSTANCE
				.findService(MessagePublisher.class);
		messagingService.forwardPack(fwdPack, user);
	}

	@Override
	public List<JPack> loadLatestPack(String userId, int pageNo)
			throws PackPackException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JComment addComment(JComment comment) throws PackPackException {
		Comment c = ModelConverter.convert(comment);
		PackRepositoryService packRepoService = ServiceRegistry.INSTANCE
				.findService(PackRepositoryService.class);
		packRepoService.addComment(c, comment.getPackId());
		return comment;
	}

	@Override
	public void addLike(String userId, String packId) throws PackPackException {
		PackRepositoryService packRepoService = ServiceRegistry.INSTANCE
				.findService(PackRepositoryService.class);
		packRepoService.addLike(userId, packId);
	}

	@Override
	public JPack uploadPack(InputStream file, String fileName, String title,
			String description, String story, String topicId, String userId,
			String mimeType, PackAttachmentType type) throws PackPackException {
		String home = (type == PackAttachmentType.IMAGE ? SystemPropertyUtil
				.getImageHome() : SystemPropertyUtil.getVideoHome());
		String location = home + File.separator + topicId;
		File f = new File(location);
		if (!f.exists()) {
			f.mkdir();
		}
		Pack pack = new Pack();
		pack.setCreationTime(new DateTime(DateTimeZone.getDefault()));
		pack.setStory(story);
		pack.setTitle(title);
		pack.setCreatorId(userId);
		PackRepositoryService service = ServiceRegistry.INSTANCE
				.findService(PackRepositoryService.class);
		service.add(pack);
		location = location + File.separator + pack.getId();
		f = new File(location);
		if (!f.exists()) {
			f.mkdir();
		}
		location = location + fileName;
		File originalFile = AttachmentUtil.storeUploadedAttachment(file,
				location);
		File thumbnailFile = (type == PackAttachmentType.IMAGE ? AttachmentUtil
				.createThumnailForImage(originalFile) : AttachmentUtil
				.createThumnailForVideo(originalFile));
		String thumbnailFileLocation = thumbnailFile.getAbsolutePath();
		PackAttachment packAttachment = new PackAttachment();
		packAttachment.setAttachmentUrl(location.substring(home.length()));
		packAttachment.setAttachmentThumbnailUrl(thumbnailFileLocation
				.substring(home.length()));
		packAttachment.setType(type);
		pack.getPackAttachments().add(packAttachment);
		service.update(pack);
		TopicRepositoryService service2 = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Topic topic = service2.get(topicId);
		topic.getPackIds().add(pack.getId());
		service2.update(topic);
		JPack jPack = ModelConverter.convert(pack);
		return jPack;
	}

	@Override
	public JPack updatePack(InputStream file, String fileName,
			PackAttachmentType type, String packId) throws PackPackException {
		// TODO Auto-generated method stub
		return null;
	}
}