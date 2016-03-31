package com.pack.pack.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.model.Comment;
import com.pack.pack.model.EGift;
import com.pack.pack.model.Pack;
import com.pack.pack.model.PackAttachment;
import com.pack.pack.model.PackAttachmentType;
import com.pack.pack.model.Topic;
import com.pack.pack.model.User;
import com.pack.pack.model.es.UserDetail;
import com.pack.pack.model.web.JComment;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JPacks;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.JeGift;
import com.pack.pack.services.couchdb.UserRepositoryService;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
public class ModelConverter {

	private static Logger logger = LoggerFactory
			.getLogger(ModelConverter.class);

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
		thumbnailUrl = thumbnailUrl.replaceAll(File.separator,
				SystemPropertyUtil.URL_SEPARATOR);
		if (!thumbnailUrl.startsWith(SystemPropertyUtil.URL_SEPARATOR)
				&& !baseURL.endsWith(SystemPropertyUtil.URL_SEPARATOR)) {
			thumbnailUrl = baseURL + SystemPropertyUtil.URL_SEPARATOR
					+ thumbnailUrl;
		} else if (baseURL.endsWith(SystemPropertyUtil.URL_SEPARATOR)
				&& thumbnailUrl.startsWith(SystemPropertyUtil.URL_SEPARATOR)) {
			thumbnailUrl = baseURL.substring(0, baseURL.length() - 1)
					+ thumbnailUrl;
		} else {
			thumbnailUrl = baseURL + thumbnailUrl;
		}
		jAttachment.setAttachmentThumbnailUrl(thumbnailUrl);
		jAttachment.setAttachmentType(type.name());
		String url = attachment.getAttachmentUrl();
		url = url.replaceAll(File.separator, SystemPropertyUtil.URL_SEPARATOR);
		if (!url.startsWith(SystemPropertyUtil.URL_SEPARATOR)
				&& !baseURL.endsWith(SystemPropertyUtil.URL_SEPARATOR)) {
			url = baseURL + SystemPropertyUtil.URL_SEPARATOR + url;
		} else if (baseURL.endsWith(SystemPropertyUtil.URL_SEPARATOR)
				&& url.startsWith(SystemPropertyUtil.URL_SEPARATOR)) {
			url = baseURL.substring(0, baseURL.length() - 1) + url;
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
	
	public static List<JPack> convertAll(List<Pack> packs) {
		if (packs == null)
			return Collections.emptyList();
		List<JPack> jPacks = new ArrayList<JPack>();
		for (Pack pack : packs) {
			JPack jPack = convert(pack);
			if (jPack != null) {
				jPacks.add(jPack);
			}
		}
		return jPacks;
	}

	public static JUser convert(User user) {
		JUser jUser = new JUser();
		jUser.setId(user.getId());
		jUser.setDob(user.getDob());
		jUser.setName(user.getName());
		jUser.setUsername(user.getUsername());
		jUser.setProfilePictureUrl(resolveProfilePictureUrl(user
				.getProfilePicture()));
		return jUser;
	}
	
	public static JUser convert(UserDetail user) {
		JUser jUser = new JUser();
		jUser.setId(user.getUserId());
		//jUser.setDob(user.getDob());
		jUser.setName(user.getName());
		jUser.setUsername(user.getUserName());
		jUser.setProfilePictureUrl(resolveProfilePictureUrl(user.getProfilePictureUrl()));
		return jUser;
	}

	public static String resolveProfilePictureUrl(String profilePicture) {
		if (profilePicture == null) {
			return null;
		}
		String baseURL = SystemPropertyUtil.getProfilePictureBaseURL();
		String profilePictureUrl = profilePicture;
		profilePictureUrl = profilePictureUrl.replaceAll(File.separator,
				SystemPropertyUtil.URL_SEPARATOR);
		if (!profilePictureUrl.startsWith(SystemPropertyUtil.URL_SEPARATOR)
				&& !baseURL.endsWith(SystemPropertyUtil.URL_SEPARATOR)) {
			profilePictureUrl = baseURL + SystemPropertyUtil.URL_SEPARATOR
					+ profilePictureUrl;
		} else if (baseURL.endsWith(SystemPropertyUtil.URL_SEPARATOR)
				&& profilePictureUrl
						.startsWith(SystemPropertyUtil.URL_SEPARATOR)) {
			profilePictureUrl = baseURL.substring(0, baseURL.length() - 1)
					+ profilePictureUrl;
		} else {
			profilePictureUrl = baseURL + profilePictureUrl;
		}
		logger.trace("Resolved URL for profile picture = " + profilePictureUrl);
		return profilePictureUrl;
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
		topic.setCategory(jTopic.getCategory());
		return topic;
	}

	public static JeGift convert(EGift eGift) {
		JeGift jeGift = new JeGift();
		jeGift.setBrandId(eGift.getBrandId());
		jeGift.setBrandInfo(eGift.getBrandInfo());
		jeGift.setCategory(eGift.getCategory());
		jeGift.setId(eGift.getId());
		jeGift.setImageThumbnailUrl(resolveEGiftUrl(eGift
				.getImageThumbnailUrl()));
		jeGift.setImageUrl(resolveEGiftUrl(eGift.getImageUrl()));
		jeGift.setTitle(eGift.getTitle());
		return jeGift;
	}

	private static String resolveEGiftUrl(String url) {
		String baseURL = SystemPropertyUtil.getImageAttachmentBaseURL();
		String resolvedUrl = url.replaceAll(File.separator,
				SystemPropertyUtil.URL_SEPARATOR);
		if (!resolvedUrl.startsWith(SystemPropertyUtil.URL_SEPARATOR)
				&& !baseURL.endsWith(SystemPropertyUtil.URL_SEPARATOR)) {
			resolvedUrl = baseURL + SystemPropertyUtil.URL_SEPARATOR
					+ resolvedUrl;
		} else {
			resolvedUrl = baseURL + resolvedUrl;
		}
		return resolvedUrl;
	}
}