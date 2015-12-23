package com.pack.pack.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.pack.pack.model.Comment;
import com.pack.pack.model.Pack;
import com.pack.pack.model.PackAttachment;
import com.pack.pack.model.PackAttachmentType;
import com.pack.pack.model.Topic;
import com.pack.pack.model.User;
import com.pack.pack.model.web.JComment;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JPacks;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.JUser;
import com.pack.pack.services.couchdb.UserRepositoryService;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
public class ModelConverter {

	public static JPack convert(Pack pack) {
		JPack jPack = new JPack();
		jPack.setId(pack.getId());
		jPack.setCreationTime(pack.getCreationTime());
		jPack.setLikes(pack.getLikes());
		jPack.setStory(pack.getStory());
		jPack.setTitle(pack.getTitle());
		jPack.setViews(pack.getViews());
		String userId = pack.getCreatorId();
		UserRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		User user = service.get(userId);
		jPack.setCreatorName(user.getName());
		List<PackAttachment> attachments = pack.getPackAttachments();
		for (PackAttachment attachment : attachments) {
			JPackAttachment jAttachment = convert(attachment);
			jPack.getAttachments().add(jAttachment);
		}
		return jPack;
	}

	public static JPackAttachment convert(PackAttachment attachment) {
		JPackAttachment jAttachment = new JPackAttachment();
		PackAttachmentType type = attachment.getType();
		String baseURL = (type == PackAttachmentType.IMAGE ? SystemPropertyUtil
				.getImageAttachmentBaseURL() : SystemPropertyUtil
				.getVideoAttachmentBaseURL());
		String thumbnailUrl = attachment.getAttachmentThumbnailUrl();
		thumbnailUrl = thumbnailUrl.replaceAll(File.separator, "\\/");
		if (!thumbnailUrl.startsWith("/") && !baseURL.endsWith("/")) {
			thumbnailUrl = baseURL + "/" + thumbnailUrl;
		} else {
			thumbnailUrl = baseURL + thumbnailUrl;
		}
		jAttachment.setAttachmentThumbnailUrl(thumbnailUrl);
		jAttachment.setAttachmentType(type.name());
		String url = attachment.getAttachmentUrl();
		url = url.replaceAll(File.separator, "\\/");
		if (!url.startsWith("/") && !baseURL.endsWith("/")) {
			url = baseURL + "/" + url;
		} else {
			url = baseURL + url;
		}
		jAttachment.setAttachmentUrl(url);
		jAttachment.setMimeType(attachment.getMimeType());
		return jAttachment;
	}

	public static JPacks convert(List<Pack> packs) {
		if (packs == null)
			return null;
		JPacks jPacks = new JPacks();
		for (Pack pack : packs) {
			JPack jPack = convert(pack);
			if (jPack != null) {
				jPacks.getPacks().add(jPack);
			}
		}
		return jPacks;
	}

	public static JUser convert(User user, String profilePictureUrl) {
		JUser jUser = new JUser();
		jUser.setId(user.getId());
		jUser.setDob(user.getDob());
		jUser.setName(user.getName());
		jUser.setUsername(user.getUsername());
		jUser.setProfilePictureUrl(profilePictureUrl);
		return jUser;
	}

	public static Comment convert(JComment jComment) {
		Comment comment = new Comment();
		comment.setComment(jComment.getComment());
		comment.setDateTime(jComment.getDateTime());
		comment.setFromUser(jComment.getFromUserName());
		comment.setPackId(jComment.getPackId());
		return comment;
	}

	public static JTopic convert(Topic topic) {
		JTopic jTopic = new JTopic();
		jTopic.setDescription(topic.getDescription());
		jTopic.setFollowers(topic.getFollowers());
		jTopic.setName(topic.getName());
		String userId = topic.getOwnerId();
		jTopic.setOwnerId(userId);
		jTopic.setId(topic.getId());
		User user = getUserInfo(userId);
		if (user != null) {
			jTopic.setOwnerName(user.getName());
		}
		return jTopic;
	}

	public static List<JTopic> convertTopicList(List<Topic> topics) {
		List<JTopic> jTopics = new ArrayList<JTopic>();
		for (Topic topic : topics) {
			JTopic jTopic = convert(topic);
			jTopics.add(jTopic);
		}
		return jTopics;
	}

	private static User getUserInfo(String userId) {
		UserRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		return service.get(userId);
	}

	public static Topic convert(JTopic jTopic) {
		Topic topic = new Topic();
		topic.setDescription(jTopic.getDescription());
		topic.setName(jTopic.getName());
		topic.setOwnerId(jTopic.getOwnerId());
		return topic;
	}
}