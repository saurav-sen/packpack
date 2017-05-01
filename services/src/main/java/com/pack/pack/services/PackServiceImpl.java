package com.pack.pack.services;

import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE;
import static com.pack.pack.common.util.CommonConstants.NULL_PAGE_LINK;

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
import com.pack.pack.model.AttachmentType;
import com.pack.pack.model.Pack;
import com.pack.pack.model.PackAttachment;
import com.pack.pack.model.PackAttachmentStory;
import com.pack.pack.model.Topic;
import com.pack.pack.model.TopicPackMap;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.PackAttachmentType;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.aws.S3Path;
import com.pack.pack.services.couchdb.PackAttachmentRepositoryService;
import com.pack.pack.services.couchdb.PackAttachmentStoryRepositoryService;
import com.pack.pack.services.couchdb.PackRepositoryService;
import com.pack.pack.services.couchdb.TopicPackMapRepositoryService;
import com.pack.pack.services.couchdb.TopicRepositoryService;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.redis.PackAttachmentPage;
import com.pack.pack.services.redis.PackPage;
import com.pack.pack.services.redis.RedisCacheService;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.AttachmentUtil;
import com.pack.pack.util.ModelConverter;
import com.pack.pack.util.StringUtils;
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
		String key = "pack:" + id;
		RedisCacheService cacheService = ServiceRegistry.INSTANCE
				.findService(RedisCacheService.class);
		if (SystemPropertyUtil.isCacheEnabled()) {
			JPack jPack = cacheService.getFromCache(key, JPack.class);
			if (jPack != null) {
				return jPack;
			}
		}
		JPack jPack = ModelConverter.convert(pack);
		if (SystemPropertyUtil.isCacheEnabled()) {
			cacheService.addToCache(key, jPack);
		}
		return jPack;
	}

	private Pack findPackById(String id) throws PackPackException {
		PackRepositoryService service = ServiceRegistry.INSTANCE
				.findService(PackRepositoryService.class);
		return service.get(id);
	}

	@Override
	public JPackAttachment getPackAttachmentById(String id)
			throws PackPackException {
		String key = "pack:attachment" + id;
		RedisCacheService cacheService = ServiceRegistry.INSTANCE
				.findService(RedisCacheService.class);
		if (SystemPropertyUtil.isCacheEnabled()) {
			JPackAttachment jPackAttachment = cacheService.getFromCache(key,
					JPackAttachment.class);
			if (jPackAttachment != null) {
				return jPackAttachment;
			}
		}
		PackAttachment attachment = findPackAttachmentById(id);
		if (attachment == null)
			return null;
		JPackAttachment jPackAttachment = ModelConverter.convert(attachment);
		if (SystemPropertyUtil.isCacheEnabled()) {
			cacheService.addToCache(key, jPackAttachment);
		}
		return jPackAttachment;
	}

	private PackAttachment findPackAttachmentById(String id)
			throws PackPackException {
		if (id == null || id.trim().isEmpty())
			return null;
		PackAttachmentRepositoryService service = ServiceRegistry.INSTANCE
				.findService(PackAttachmentRepositoryService.class);
		return service.get(id);
	}

	/*@Override
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
	}*/

	@Override
	public Pagination<JPack> loadLatestPack(String userId, String topicId,
			String pageLink) throws PackPackException {
		String key = "pack:topic:" + topicId + ":page:"
				+ (pageLink != null ? pageLink : NULL_PAGE_LINK);
		RedisCacheService cacheService = ServiceRegistry.INSTANCE
				.findService(RedisCacheService.class);
		if (SystemPropertyUtil.isCacheEnabled()) {
			PackPage packPage = cacheService.getFromCache(key, PackPage.class);
			if (packPage != null) {
				return packPage.convert();
			}
		}
		
		PackRepositoryService packRepositoryService = ServiceRegistry.INSTANCE
				.findService(PackRepositoryService.class);
		Pagination<Pack> page = packRepositoryService.getAllPacks(topicId,
				pageLink);
		List<JPack> jPacks = ModelConverter.convertAll(page.getResult());
		Pagination<JPack> result = new Pagination<JPack>(page.getPreviousLink(),
				page.getNextLink(), jPacks);
		if(SystemPropertyUtil.isCacheEnabled()) {
			PackPage packPage = PackPage.build(result);
			cacheService.addToCache(key, packPage);
		}
		return result;
		
		/*UserTopicMapRepositoryService mapRepositoryService = ServiceRegistry.INSTANCE
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
			Pagination<JPack> result = new Pagination<JPack>(page.getPreviousLink(),
					page.getNextLink(), jPacks);
			if(SystemPropertyUtil.isCacheEnabled()) {
				PackPage packPage = PackPage.build(result);
				cacheService.addToCache(key, packPage);
			}
			return result;
		}
		Pagination<JPack> result = new Pagination<JPack>(END_OF_PAGE, END_OF_PAGE,
				Collections.emptyList());
		if(SystemPropertyUtil.isCacheEnabled()) {
			PackPage packPage = PackPage.build(result);
			cacheService.addToCache(key, packPage);
		}
		return result;*/
	}

	@Override
	public Pagination<JPackAttachment> loadPackAttachments(String userId,
			String topicId, String packId, String pageLink)
			throws PackPackException {
		String key = "pack:topic:" + topicId + ":pack:" + packId
				+ ":attachment:page:" + (pageLink != null ? pageLink : NULL_PAGE_LINK);
		RedisCacheService cacheService = ServiceRegistry.INSTANCE
				.findService(RedisCacheService.class);
		if (SystemPropertyUtil.isCacheEnabled()) {
			PackAttachmentPage attachmentPage = cacheService.getFromCache(key,
					PackAttachmentPage.class);
			if (attachmentPage != null) {
				return attachmentPage.convert();
			}
		}
		
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
			Pagination<JPackAttachment> r = new Pagination<JPackAttachment>(
					page.getPreviousLink(), page.getNextLink(), result);
			if (SystemPropertyUtil.isCacheEnabled()) {
				cacheService.addToCache(key, PackAttachmentPage.build(r));
			}
			return r;
		}
		
		Pagination<JPackAttachment> r = new Pagination<JPackAttachment>(
				END_OF_PAGE, END_OF_PAGE, Collections.emptyList());
		if (SystemPropertyUtil.isCacheEnabled()) {
			cacheService.addToCache(key, PackAttachmentPage.build(r));
		}
		return r;
		
		/*UserTopicMapRepositoryService mapRepositoryService = ServiceRegistry.INSTANCE
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
				Pagination<JPackAttachment> r = new Pagination<JPackAttachment>(
						page.getPreviousLink(), page.getNextLink(), result);
				if (SystemPropertyUtil.isCacheEnabled()) {
					cacheService.addToCache(key, PackAttachmentPage.build(r));
				}
				return r;
			}
		}
		Pagination<JPackAttachment> r = new Pagination<JPackAttachment>(
				END_OF_PAGE, END_OF_PAGE, Collections.emptyList());
		if (SystemPropertyUtil.isCacheEnabled()) {
			cacheService.addToCache(key, PackAttachmentPage.build(r));
		}
		return r;*/
	}

	@Override
	public JPack uploadPack(InputStream file, String fileName, String title,
			String description, String story, String topicId, String userId,
			String mimeType, PackAttachmentType type, boolean publish)
			throws PackPackException {
		Pack pack = addNewPack(story, title, userId, topicId);
		addPackAttachment(pack, topicId, type, title, userId, description,
				fileName, file, true);
		if (SystemPropertyUtil.isCacheEnabled() && pack != null) {
			String keyPrefix = "pack:topic:" + topicId;
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			cacheService.removeAllFromCache(keyPrefix);
			cacheService.removeFromCache("pack:" + pack.getId());
		}
		TopicRepositoryService service2 = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Topic topic = service2.get(topicId);
		topic.getPackIds().add(pack.getId());
		service2.update(topic);
		JPack jPack = ModelConverter.convert(pack);
		/*if (publish) {
			FwdPack fwdPack = new FwdPack();
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
		}*/
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

	private PackAttachment addPackAttachment(Pack pack, String topicId,
			PackAttachmentType type, String title, String creatorId, 
			String description, String fileName, InputStream file, boolean isCompressed)
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
		
		S3Path fileS3 = new S3Path(topicId, false);
		fileS3.addChild(new S3Path(pack.getId(), false)).addChild(
				new S3Path(fileName, true));
		
		PackAttachment packAttachment = new PackAttachment();
		String relativeUrl = location.substring(home.length());
		packAttachment.setAttachmentUrl(relativeUrl);
		File originalFile = AttachmentUtil.storeUploadedAttachment(file,
				location, fileS3, relativeUrl, isCompressed,
				(type == PackAttachmentType.VIDEO));
		File thumbnailFile = null;
		/*File thumbnailFile = (type == PackAttachmentType.IMAGE ? null
				: AttachmentUtil.createThumnailForVideo(originalFile, fileS3));*/
		if(type == PackAttachmentType.VIDEO) {
			S3Path thumbnailFileS3 = new S3Path(topicId, false).addChild(
					new S3Path(pack.getId(), false)).addChild(
					new S3Path(fileName, true));
			thumbnailFile = AttachmentUtil.createThumnailForVideo(originalFile, thumbnailFileS3);
		}
		if (thumbnailFile != null) {
			String thumbnailFileLocation = thumbnailFile.getAbsolutePath();
			packAttachment.setAttachmentThumbnailUrl(thumbnailFileLocation
					.substring(home.length()));
		}
		packAttachment.setTitle(title);
		packAttachment.setDescription(description);
		packAttachment.setCreatorId(creatorId);
		packAttachment.setCreationTime(System.currentTimeMillis());
		packAttachment.setType(AttachmentType.valueOf(type.name()));
		packAttachment.setMimeType(type.name());
		packAttachment.setAttachmentParentPackId(pack.getId());
		PackAttachmentRepositoryService service = ServiceRegistry.INSTANCE
				.findService(PackAttachmentRepositoryService.class);
		service.add(packAttachment);
		return packAttachment;
	}

	@Override
	public JPackAttachment updatePack(InputStream file, String fileName,
			PackAttachmentType type, String packId, String topicId,
			String userId, String title, String description, boolean isCompressed)
			throws PackPackException {
		Pack pack = findPackById(packId);
		PackAttachment attachment = addPackAttachment(pack, topicId, type,
				title, userId, description, fileName, file, isCompressed);
		if (SystemPropertyUtil.isCacheEnabled()) {
			String keyPrefix = "pack:topic:" + topicId + ":pack:" + packId
					+ ":attachment";
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			cacheService.removeFromCache(keyPrefix);
		}
		/*TopicRepositoryService topicService = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Topic topic = topicService.get(topicId);
		FwdPack fwdPack = new FwdPack();
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
		messagePublisher.notifyPackModify(fwdPack, topic, user);*/
		return ModelConverter.convert(attachment);
	}

	/*@Override
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
	}*/

	/*@Override
	public void broadcastSystemPack(BroadcastCriteria criteria, String packId)
			throws PackPackException {
		broadcastPack(criteria, packId, null);
	}*/

	@Override
	public JPack createNewPack(Pack pack) throws PackPackException {
		PackRepositoryService service = ServiceRegistry.INSTANCE
				.findService(PackRepositoryService.class);
		pack.setCreationTime(System.currentTimeMillis());
		service.add(pack);
		return ModelConverter.convert(pack);
	}
	
	@Override
	public void deleteAttachment(String attachmentId, String packId,
			String topicId) throws PackPackException {
		PackAttachmentRepositoryService service = ServiceRegistry.INSTANCE
				.findService(PackAttachmentRepositoryService.class);
		PackAttachment packAttachment = service.get(attachmentId);
		service.remove(packAttachment);
		String storyId = packAttachment.getStoryId();
		if (storyId != null && !storyId.trim().isEmpty()) {
			PackAttachmentStoryRepositoryService storyStore = ServiceRegistry.INSTANCE
					.findService(PackAttachmentStoryRepositoryService.class);
			PackAttachmentStory story = storyStore.get(storyId);
			if (story != null) {
				storyStore.remove(story);
			}
		}
	}
	
	@Override
	public JPackAttachment updatePackFromExternalLink(PackAttachmentType type,
			String packId, String topicId, String userId, String title,
			String description, String attachmentUrl,
			String attachmentThumbnailUrl, boolean isCompressed)
			throws PackPackException {
		Pack pack = findPackById(packId);

		if (pack == null) {
			throw new PackPackException(ErrorCodes.PACK_ERR_01,
					"Pack with ID = " + packId + " NOT found.");
		}

		PackAttachment attachment = new PackAttachment();
		attachment.setTitle(title);
		attachment.setDescription(description);
		attachment.setCreatorId(userId);
		attachment.setCreationTime(System.currentTimeMillis());
		attachment.setType(AttachmentType.valueOf(type.name()));
		attachment.setMimeType(type.name());
		attachment.setAttachmentParentPackId(pack.getId());
		attachment.setAttachmentUrl(attachmentUrl);
		attachment.setAttachmentThumbnailUrl(attachmentThumbnailUrl);
		attachment.setIsExternalLink("true");
		if(attachmentUrl.contains("youtube.com")) {
			String[] split = attachmentUrl.split("watch=");
			if(split.length > 1) {
				String videoID = split[1].trim();
				attachment.getExtraMetaData().put("YOUTUBE_VIDEO_ID", videoID);
			}
		}
		PackAttachmentRepositoryService service = ServiceRegistry.INSTANCE
				.findService(PackAttachmentRepositoryService.class);
		service.add(attachment);

		if (SystemPropertyUtil.isCacheEnabled()) {
			String keyPrefix = "pack:topic:" + topicId + ":pack:" + packId
					+ ":attachment";
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			cacheService.removeFromCache(keyPrefix);
		}

		return ModelConverter.convert(attachment);
	}
	
	@Override
	public String addStoryToAttachment(String attachmentId, String story)
			throws PackPackException {
		PackAttachment packAttachment = findPackAttachmentById(attachmentId);
		if (packAttachment == null) {
			throw new PackPackException(ErrorCodes.PACK_ERR_01,
					"Attachment Not Found");
		}
		story = StringUtils.compress(story);
		boolean oldEntity = false;
		PackAttachmentStory storyObj = null;
		String storyId = packAttachment.getStoryId();
		if(storyId != null && !storyId.trim().isEmpty()) {
			PackAttachmentStoryRepositoryService storyStore = ServiceRegistry.INSTANCE
					.findService(PackAttachmentStoryRepositoryService.class);
			storyObj = storyStore.get(storyId);
			oldEntity = true;
		} else {
			storyObj = new PackAttachmentStory();
		}
		
		storyObj.setContent(story);
		storyObj.setParentAttachmentId(packAttachment.getId());
		PackAttachmentStoryRepositoryService storyStore = ServiceRegistry.INSTANCE
				.findService(PackAttachmentStoryRepositoryService.class);
		if(!oldEntity) {
			storyStore.add(storyObj);
		} else {
			storyStore.update(storyObj);
		}
		packAttachment.setStoryId(storyObj.getId());
		PackAttachmentRepositoryService service = ServiceRegistry.INSTANCE
				.findService(PackAttachmentRepositoryService.class);
		service.update(packAttachment);
		return storyObj.getId();
	}
	
	@Override
	public String loadAttachmentStory(String attachmentId, String userId)
			throws PackPackException {
		PackAttachment packAttachment = findPackAttachmentById(attachmentId);
		if (packAttachment == null) {
			throw new PackPackException(ErrorCodes.PACK_ERR_01,
					"Attachment Not Found");
		}
		String storyId = packAttachment.getStoryId();
		if(storyId == null || storyId.trim().isEmpty()) {
			return "";
		}
		PackAttachmentStoryRepositoryService storyStore = ServiceRegistry.INSTANCE
				.findService(PackAttachmentStoryRepositoryService.class);
		PackAttachmentStory story = storyStore.get(storyId);
		if(story == null) {
			return "";
		}
		String content = story.getContent();
		if(content == null) {
			return null;
		}
		content = StringUtils.decompress(content);
		return content;
	}
}