package com.pack.pack.services;

import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.IPackService;
import com.pack.pack.common.util.CommonConstants;
import com.pack.pack.message.FwdPack;
import com.pack.pack.model.Comment;
import com.pack.pack.model.Pack;
import com.pack.pack.model.PackAttachment;
import com.pack.pack.model.PackAttachmentType;
import com.pack.pack.model.Topic;
import com.pack.pack.model.TopicPackMap;
import com.pack.pack.model.User;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.dto.PackReceipent;
import com.pack.pack.model.web.dto.PackReceipentType;
import com.pack.pack.services.couchdb.PackAttachmentRepositoryService;
import com.pack.pack.services.couchdb.PackRepositoryService;
import com.pack.pack.services.couchdb.TopicPackMapRepositoryService;
import com.pack.pack.services.couchdb.TopicRepositoryService;
import com.pack.pack.services.couchdb.UserRepositoryService;
import com.pack.pack.services.couchdb.UserTopicMapRepositoryService;
import com.pack.pack.services.email.EmailSender;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.rabbitmq.MessagePublisher;
import com.pack.pack.services.rabbitmq.objects.BroadcastCriteria;
import com.pack.pack.services.rabbitmq.objects.BroadcastPack;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.services.sms.SMSSender;
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
		Pack pack = findPackById(id);
		return ModelConverter.convert(pack);
	}

	private Pack findPackById(String id) throws PackPackException {
		PackRepositoryService service = ServiceRegistry.INSTANCE
				.findService(PackRepositoryService.class);
		return service.get(id);
	}

	@Override
	public JPackAttachment getPackAttachmentById(String id)
			throws PackPackException {
		PackAttachment attachment = findPackAttachmentById(id);
		if (attachment == null)
			return null;
		return ModelConverter.convert(attachment);
	}

	private PackAttachment findPackAttachmentById(String id)
			throws PackPackException {
		if (id == null || id.trim().isEmpty())
			return null;
		PackAttachmentRepositoryService service = ServiceRegistry.INSTANCE
				.findService(PackAttachmentRepositoryService.class);
		return service.get(id);
	}

	@Override
	public void forwardPack(String packId, String fromUserId,
			PackReceipent... receipents) throws PackPackException {
		if (receipents == null || receipents.length == 0)
			return;
		PackRepositoryService repoService = ServiceRegistry.INSTANCE
				.findService(PackRepositoryService.class);
		Pack pack = repoService.get(packId);
		FwdPack fwdPack = new FwdPack();
		PackAttachmentRepositoryService repoService2 = ServiceRegistry.INSTANCE
				.findService(PackAttachmentRepositoryService.class);
		Pagination<PackAttachment> page = repoService2.getAllPackAttachment(
				packId, null);
		List<PackAttachment> packAttachments = page.getResult();
		if (packAttachments != null && !packAttachments.isEmpty()) {
			for (PackAttachment packAttachment : packAttachments) {
				JPackAttachment jPackAttachment = ModelConverter
						.convert(packAttachment);
				fwdPack.getAttachments().add(jPackAttachment);
			}
		}
		List<Comment> comments = pack.getComments();
		if (comments != null && !comments.isEmpty()) {
			for (Comment comment : comments) {
				fwdPack.getComments().add(ModelConverter.convert(comment));
			}
		}
		fwdPack.setFromUserId(fromUserId);
		UserRepositoryService userRepoService = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		User user = userRepoService.get(fromUserId);
		fwdPack.setFromUserName(user.getName());
		fwdPack.setFromUserProfilePicUrl(null); // TODO
		fwdPack.setLikes(pack.getLikes());
		fwdPack.setViews(pack.getViews());
		fwdPack.setMessage("Pack :: " + pack.getTitle()
				+ " :: has been sent to you by " + user.getName());
		for (PackReceipent receipent : receipents) {
			String userId = receipent.getToUserId();
			PackReceipentType type = receipent.getType();
			if (type == null) {
				type = PackReceipentType.USER;
			}
			switch (type) {
			case USER:
				User toUser = userRepoService.get(userId);
				MessagePublisher messagingService = ServiceRegistry.INSTANCE
						.findService(MessagePublisher.class);
				messagingService.forwardPack(fwdPack, toUser);
				break;
			case EMAIL:
				EmailSender emailService = ServiceRegistry.INSTANCE
						.findService(EmailSender.class);
				emailService.forwardPack(fwdPack, userId);
				break;
			case SMS:
				SMSSender smsService = ServiceRegistry.INSTANCE
						.findService(SMSSender.class);
				smsService.forwardPack(fwdPack, userId);
				break;
			}
		}
	}

	@Override
	public Pagination<JPack> loadLatestPack(String userId, String topicId,
			String pageLink) throws PackPackException {
		UserTopicMapRepositoryService mapRepositoryService = ServiceRegistry.INSTANCE
				.findService(UserTopicMapRepositoryService.class);
		List<String> IDs = mapRepositoryService
				.getAllTopicIDsFollowedByUser(userId);
		if ((!IDs.isEmpty() && IDs.contains(topicId))
				|| CommonConstants.DEFAULT_TOPIC_ID.equals(topicId)
				|| CommonConstants.DEFAULT_EGIFT_TOPIC_ID.equals(topicId)) {
			PackRepositoryService packRepositoryService = ServiceRegistry.INSTANCE
					.findService(PackRepositoryService.class);
			Pagination<Pack> page = packRepositoryService.getAllPacks(topicId,
					pageLink);
			List<JPack> jPacks = ModelConverter.convertAll(page.getResult());
			return new Pagination<JPack>(page.getPreviousLink(),
					page.getNextLink(), jPacks);
		}
		return new Pagination<JPack>(END_OF_PAGE, END_OF_PAGE,
				Collections.emptyList());
	}

	@Override
	public Pagination<JPackAttachment> loadPackAttachments(String userId,
			String topicId, String packId, String pageLink)
			throws PackPackException {
		UserTopicMapRepositoryService mapRepositoryService = ServiceRegistry.INSTANCE
				.findService(UserTopicMapRepositoryService.class);
		List<String> IDs = mapRepositoryService
				.getAllTopicIDsFollowedByUser(userId);
		if ((!IDs.isEmpty() && IDs.contains(topicId))
				|| CommonConstants.DEFAULT_TOPIC_ID.equals(topicId)
				|| CommonConstants.DEFAULT_EGIFT_TOPIC_ID.equals(topicId)) {
			List<JPackAttachment> result = new LinkedList<JPackAttachment>();
			PackAttachmentRepositoryService packAttachmentRepositoryService = ServiceRegistry.INSTANCE
					.findService(PackAttachmentRepositoryService.class);
			Pagination<PackAttachment> page = packAttachmentRepositoryService
					.getAllPackAttachment(packId, pageLink);
			if (page != null) {
				List<PackAttachment> attachments = page.getResult();
				if (attachments != null && !attachments.isEmpty()) {
					result = new ArrayList<JPackAttachment>();
					for (PackAttachment attachment : attachments) {
						result.add(ModelConverter.convert(attachment));
					}
				}
				return new Pagination<JPackAttachment>(page.getPreviousLink(),
						page.getNextLink(), result);
			}
		}
		return new Pagination<JPackAttachment>(END_OF_PAGE, END_OF_PAGE,
				Collections.emptyList());
	}

	@Override
	public JPack uploadPack(InputStream file, String fileName, String title,
			String description, String story, String topicId, String userId,
			String mimeType, PackAttachmentType type, boolean publish)
			throws PackPackException {
		Pack pack = addNewPack(story, title, userId, topicId);
		addPackAttachment(pack, topicId, type, fileName, file);
		TopicRepositoryService service2 = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Topic topic = service2.get(topicId);
		topic.getPackIds().add(pack.getId());
		service2.update(topic);
		JPack jPack = ModelConverter.convert(pack);
		if (publish) {
			FwdPack fwdPack = new FwdPack();
			// TODO fwdPack.setAccessUrl(null);
			fwdPack.setFromUserId(userId);
			UserRepositoryService userService = ServiceRegistry.INSTANCE
					.findService(UserRepositoryService.class);
			User user = userService.get(userId);
			fwdPack.setFromUserName(user.getUsername());
			fwdPack.setFromUserProfilePicUrl(null);
			fwdPack.setLikes(pack.getLikes());
			fwdPack.setPackId(pack.getId());
			fwdPack.setViews(pack.getViews());
			fwdPack.setMessage("Pack :: " + pack.getTitle()
					+ " :: has been uploaded by " + user.getName());
			MessagePublisher messagePublisher = ServiceRegistry.INSTANCE
					.findService(MessagePublisher.class);
			messagePublisher.notifyPackModify(fwdPack, topic, user);
		}
		return jPack;
	}

	private Pack addNewPack(String story, String title, String userId,
			String topicId) {
		Pack pack = new Pack();
		long dateTime = new DateTime(DateTimeZone.getDefault()).getMillis();
		pack.setCreationTime(dateTime);
		pack.setStory(story);
		pack.setTitle(title);
		pack.setCreatorId(userId);
		pack.setPackParentTopicId(topicId);
		PackRepositoryService service = ServiceRegistry.INSTANCE
				.findService(PackRepositoryService.class);
		service.add(pack);
		TopicPackMap topicPackMap = new TopicPackMap();
		topicPackMap.setPackId(pack.getId());
		topicPackMap.setTopicId(topicId);
		topicPackMap.setDateTime(dateTime);
		TopicPackMapRepositoryService service2 = ServiceRegistry.INSTANCE
				.findService(TopicPackMapRepositoryService.class);
		service2.add(topicPackMap);
		return pack;
	}

	private void addPackAttachment(Pack pack, String topicId,
			PackAttachmentType type, String fileName, InputStream file)
			throws PackPackException {
		String home = (type == PackAttachmentType.IMAGE ? SystemPropertyUtil
				.getImageHome() : SystemPropertyUtil.getVideoHome());
		String location = home + File.separator + topicId;
		File f = new File(location);
		if (!f.exists()) {
			f.mkdir();
		}
		location = location + File.separator + pack.getId();
		f = new File(location);
		if (!f.exists()) {
			f.mkdir();
		}
		location = location + File.separator + fileName;
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
		packAttachment.setAttachmentParentPackId(pack.getId());
		PackAttachmentRepositoryService service = ServiceRegistry.INSTANCE
				.findService(PackAttachmentRepositoryService.class);
		service.add(packAttachment);
	}

	@Override
	public JPack updatePack(InputStream file, String fileName,
			PackAttachmentType type, String packId, String topicId,
			String userId) throws PackPackException {
		Pack pack = findPackById(packId);
		addPackAttachment(pack, topicId, type, fileName, file);
		TopicRepositoryService topicService = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Topic topic = topicService.get(topicId);
		FwdPack fwdPack = new FwdPack();
		// TODO fwdPack.setAccessUrl(null);
		fwdPack.setFromUserId(userId);
		UserRepositoryService userService = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		User user = userService.get(userId);
		fwdPack.setFromUserName(user.getUsername());
		fwdPack.setFromUserProfilePicUrl(null);
		fwdPack.setLikes(pack.getLikes());
		fwdPack.setPackId(pack.getId());
		fwdPack.setViews(pack.getViews());
		fwdPack.setMessage("Pack :: " + pack.getTitle()
				+ " :: has been updated by " + user.getName());
		MessagePublisher messagePublisher = ServiceRegistry.INSTANCE
				.findService(MessagePublisher.class);
		messagePublisher.notifyPackModify(fwdPack, topic, user);
		return ModelConverter.convert(pack);
	}

	@Override
	public void broadcastPack(BroadcastCriteria criteria, String packId,
			String userId) throws PackPackException {
		PackRepositoryService service = ServiceRegistry.INSTANCE
				.findService(PackRepositoryService.class);
		Pack pack = service.get(packId);
		BroadcastPack bPack = new BroadcastPack();
		FwdPack fwdPack = new FwdPack();
		PackAttachmentRepositoryService service2 = ServiceRegistry.INSTANCE
				.findService(PackAttachmentRepositoryService.class);
		Pagination<PackAttachment> page = service2.getAllPackAttachment(packId,
				null);
		List<PackAttachment> packAttachments = page.getResult();
		if (packAttachments != null && !packAttachments.isEmpty()) {
			for (PackAttachment packAttachment : packAttachments) {
				JPackAttachment jPackAttachment = ModelConverter
						.convert(packAttachment);
				fwdPack.getAttachments().add(jPackAttachment);
			}
		}
		List<Comment> comments = pack.getComments();
		if (comments != null && !comments.isEmpty()) {
			for (Comment comment : comments) {
				fwdPack.getComments().add(ModelConverter.convert(comment));
			}
		}
		if (userId == null) {
			fwdPack.setFromUserName("System");
		} else {
			fwdPack.setFromUserId(userId);
			UserRepositoryService userRepositoryService = ServiceRegistry.INSTANCE
					.findService(UserRepositoryService.class);
			User user = userRepositoryService.get(userId);
			fwdPack.setFromUserName(user.getUsername());
		}
		fwdPack.setLikes(pack.getLikes());
		fwdPack.setPackId(pack.getId());
		fwdPack.setViews(pack.getViews());
		bPack.setFwdPack(fwdPack);
		MessagePublisher publisher = ServiceRegistry.INSTANCE
				.findService(MessagePublisher.class);
		publisher.broadcast(bPack);
	}

	@Override
	public void broadcastSystemPack(BroadcastCriteria criteria, String packId)
			throws PackPackException {
		broadcastPack(criteria, packId, null);
	}
}