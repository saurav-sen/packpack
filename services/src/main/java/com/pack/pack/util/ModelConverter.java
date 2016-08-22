package com.pack.pack.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.IUserService;
import com.pack.pack.model.AttachmentType;
import com.pack.pack.model.Comment;
import com.pack.pack.model.Discussion;
import com.pack.pack.model.EGift;
import com.pack.pack.model.Pack;
import com.pack.pack.model.PackAttachment;
import com.pack.pack.model.RSSFeed;
import com.pack.pack.model.Topic;
import com.pack.pack.model.User;
import com.pack.pack.model.UserInfo;
import com.pack.pack.model.es.UserDetail;
import com.pack.pack.model.web.JComment;
import com.pack.pack.model.web.JDiscussion;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JPacks;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.JeGift;
import com.pack.pack.services.couchdb.UserRepositoryService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
public class ModelConverter {

	private static Logger logger = LoggerFactory
			.getLogger(ModelConverter.class);

	public static List<JRssFeed> convertAllRssFeeds(List<RSSFeed> feeds) {
		if (feeds == null || feeds.isEmpty())
			return Collections.emptyList();
		List<JRssFeed> results = new LinkedList<JRssFeed>();
		for (RSSFeed feed : feeds) {
			JRssFeed result = convert(feed);
			if (result == null)
				continue;
			results.add(result);
		}
		return results;
	}

	public static JRssFeed convert(RSSFeed feed) {
		if (feed == null)
			return null;
		JRssFeed rFeed = new JRssFeed();
		rFeed.setHrefSource(feed.getHrefSource());
		rFeed.setOgDescription(feed.getOgDescription());
		rFeed.setOgImage(feed.getOgImage());
		rFeed.setOgTitle(feed.getOgTitle());
		rFeed.setOgType(feed.getOgType());
		rFeed.setOgUrl(feed.getOgUrl());
		rFeed.setId(feed.getId());
		return rFeed;
	}
	
	public static RSSFeed convert(JRssFeed rFeed) {
		if (rFeed == null)
			return null;
		RSSFeed feed = new RSSFeed();
		feed.setHrefSource(rFeed.getHrefSource());
		feed.setOgDescription(rFeed.getOgDescription());
		feed.setOgImage(rFeed.getOgImage());
		feed.setOgTitle(rFeed.getOgTitle());
		feed.setOgType(rFeed.getOgType());
		feed.setOgUrl(rFeed.getOgUrl());
		return feed;
	}

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
		jPack.setParentTopicId(pack.getPackParentTopicId());
		jPack.setCreator(convert(user));
		return jPack;
	}

	public static JPackAttachment convert(PackAttachment attachment) {
		JPackAttachment jAttachment = new JPackAttachment();
		AttachmentType type = attachment.getType();
		String baseURL = (type == AttachmentType.IMAGE ? SystemPropertyUtil
				.getImageAttachmentBaseURL() : SystemPropertyUtil
				.getVideoAttachmentBaseURL());
/*		String thumbnailUrl = attachment.getAttachmentThumbnailUrl();
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
		jAttachment.setAttachmentThumbnailUrl(thumbnailUrl);*/
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
		jAttachment.setId(attachment.getId());
		jAttachment.setAttachmentUrl(url);
		jAttachment.setMimeType(attachment.getMimeType());
		jAttachment.setTitle(attachment.getTitle());
		jAttachment.setLikes(attachment.getLikes());
		jAttachment.setViews(attachment.getViews());
		jAttachment.setCreationTime(attachment.getCreationTime());
		List<Comment> recentComments = attachment.getRecentComments();
		if (recentComments != null && !recentComments.isEmpty()) {
			for (Comment comment : recentComments) {
				JComment jComment = ModelConverter.convert(comment);
				jAttachment.getComments().add(jComment);
			}
		}
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
		List<UserInfo> infos = user.getExtraInfoMap();
		if (infos != null && !infos.isEmpty()) {
			for (UserInfo info : infos) {
				if (UserInfo.FOLLOWED_CATEGORIES.equals(info.getKey())) {
					String followedCategories = info.getValue();
					String[] split = followedCategories
							.split(UserInfo.FOLLOWED_CATEGORIES_SEPARATOR);
					List<String> list = Arrays.asList(split);
					jUser.setFollowedCategories(list);
					break;
				}
			}
		}
		return jUser;
	}

	public static JUser convert(UserDetail user) {
		JUser jUser = new JUser();
		jUser.setId(user.getUserId());
		// jUser.setDob(user.getDob());
		jUser.setName(user.getName());
		jUser.setUsername(user.getUserName());
		jUser.setProfilePictureUrl(resolveProfilePictureUrl(user
				.getProfilePictureUrl()));
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

	public static String resolveTopicWallpaperUrl(String wallpaperLocation) {
		if (wallpaperLocation == null) {
			return null;
		}
		String baseURL = SystemPropertyUtil.getTopicWallpaperBaseURL();
		String topicWallpaperUrl = wallpaperLocation;
		topicWallpaperUrl = topicWallpaperUrl.replaceAll(File.separator,
				SystemPropertyUtil.URL_SEPARATOR);
		if (!topicWallpaperUrl.startsWith(SystemPropertyUtil.URL_SEPARATOR)
				&& !baseURL.endsWith(SystemPropertyUtil.URL_SEPARATOR)) {
			topicWallpaperUrl = baseURL + SystemPropertyUtil.URL_SEPARATOR
					+ topicWallpaperUrl;
		} else if (baseURL.endsWith(SystemPropertyUtil.URL_SEPARATOR)
				&& topicWallpaperUrl
						.startsWith(SystemPropertyUtil.URL_SEPARATOR)) {
			topicWallpaperUrl = baseURL.substring(0, baseURL.length() - 1)
					+ topicWallpaperUrl;
		} else {
			topicWallpaperUrl = baseURL + topicWallpaperUrl;
		}
		logger.trace("Resolved URL for topic wallpaper = " + topicWallpaperUrl);
		return topicWallpaperUrl;
	}

	public static Comment convert(JComment jComment) {
		Comment comment = new Comment();
		comment.setComment(jComment.getComment());
		comment.setDateTime(jComment.getDateTime());
		comment.setFromUser(jComment.getFromUserName());
		return comment;
	}

	public static JComment convert(Comment comment) {
		JComment jComment = new JComment();
		jComment.setComment(comment.getComment());
		jComment.setDateTime(comment.getDateTime());
		jComment.setFromUserId(comment.getFromUser());
		List<Comment> replies = comment.getReplies();
		if (replies != null) {
			for (Comment reply : replies) {
				JComment jReply = convert(reply);
				jComment.getReplies().add(jReply);
			}
		}
		return jComment;
	}

	public static List<JComment> convertComments(List<Comment> comments) {
		List<JComment> jComments = new LinkedList<JComment>();
		if (comments == null)
			return jComments;
		for (Comment comment : comments) {
			JComment jComment = convert(comment);
			if (jComment == null)
				continue;
			jComments.add(jComment);
		}
		return jComments;
	}
	
	public static JTopic convert(Topic topic) throws PackPackException {
		return convert(topic, false);
	}

	public static JTopic convert(Topic topic, boolean isFollowing) throws PackPackException {
		JTopic jTopic = new JTopic();
		jTopic.setDescription(topic.getDescription());
		jTopic.setFollowers(topic.getFollowers());
		jTopic.setName(topic.getName());
		String userId = topic.getOwnerId();
		jTopic.setOwnerId(userId);
		jTopic.setId(topic.getId());
		jTopic.setCategory(topic.getCategory());
		jTopic.setWallpaperUrl(resolveTopicWallpaperUrl(topic.getWallpaperUrl()));
		jTopic.setFollowing(isFollowing);
		jTopic.setLongitude(topic.getLongitude());
		jTopic.setLatitude(topic.getLatitude());
		JUser user = getUserInfo(userId);
		if (user != null) {
			jTopic.setOwnerName(user.getName());
			jTopic.setOwnerProfilePicture(user.getProfilePictureUrl());

		}
		return jTopic;
	}
	
	public static List<JTopic> convertTopicList(List<Topic> topics) throws PackPackException {
		return convertTopicList(topics, false);
	}

	public static List<JTopic> convertTopicList(List<Topic> topics, boolean isFollowing)
			throws PackPackException {
		List<JTopic> jTopics = new ArrayList<JTopic>();
		for (Topic topic : topics) {
			JTopic jTopic = convert(topic, isFollowing);
			jTopics.add(jTopic);
		}
		return jTopics;
	}

	private static JUser getUserInfo(String userId) throws PackPackException {
		IUserService service = ServiceRegistry.INSTANCE
				.findCompositeService(IUserService.class);
		return service.findUserById(userId);
	}

	public static Topic convert(JTopic jTopic) {
		Topic topic = new Topic();
		topic.setDescription(jTopic.getDescription());
		topic.setName(jTopic.getName());
		topic.setOwnerId(jTopic.getOwnerId());
		topic.setCategory(jTopic.getCategory());
		topic.setLongitude(jTopic.getLongitude());
		topic.setLatitude(jTopic.getLatitude());
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

	public static JDiscussion convert(Discussion discussion) {
		if (discussion == null)
			return null;
		JDiscussion jDiscussion = new JDiscussion();
		jDiscussion.setId(discussion.getId());
		jDiscussion.setContent(discussion.getContent());
		jDiscussion.setParentId(discussion.getParentEntityId());
		jDiscussion.setParentType(discussion.getParentEntityType());
		jDiscussion.setFromUserId(discussion.getStartedByUserId());
		jDiscussion.setTitle(discussion.getDiscussionTitle());
		jDiscussion.setLikes(discussion.getLikes());
		jDiscussion.setLikeUsers(discussion.getLikeUsers());
		return jDiscussion;
	}

	/*
	 * public static List<JReply> convertReplies(List<Reply> replies) {
	 * List<JReply> jReplies = new LinkedList<JReply>(); if(replies != null &&
	 * !replies.isEmpty()) { for(Reply reply : replies) { JReply jReply =
	 * convert(reply); if(jReply != null) { jReplies.add(jReply); } } } return
	 * jReplies; }
	 * 
	 * public static JReply convert(Reply reply) { if(reply == null) return
	 * null; JReply jReply = new JReply(); jReply.setId(reply.getId());
	 * jReply.setContent(reply.getReply());
	 * jReply.setFromUserId(reply.getFromUserId());
	 * jReply.setDateTime(reply.getDateTime());
	 * jReply.setLikes(reply.getLikes());
	 * jReply.setLikeUsers(reply.getLikeUsers()); return jReply; }
	 * 
	 * public static List<Reply> convertJReplies(List<JReply> jReplies) {
	 * if(jReplies == null) return null; List<Reply> replies = new
	 * LinkedList<Reply>(); for(JReply jReply : jReplies) { Reply reply =
	 * convert(jReply); if(reply != null) { replies.add(reply); } } return
	 * replies; }
	 */

	/*
	 * public static Reply convert(JReply jReply) { if(jReply == null) return
	 * null; Reply reply = new Reply(); reply.setReply(jReply.getContent());
	 * reply.setFromUserId(jReply.getFromUserId());
	 * reply.setLikes(jReply.getLikes());
	 * reply.setDateTime(jReply.getDateTime());
	 * reply.setLikeUsers(jReply.getLikeUsers()); List<JReply> jReplies =
	 * jReply.getReplies(); if(jReplies != null && !jReplies.isEmpty()) {
	 * List<Reply> replies = convertJReplies(jReplies);
	 * reply.setReplies(replies); } return reply; }
	 */

	public static Discussion convert(JDiscussion jDiscussion) {
		Discussion discussion = new Discussion();
		discussion.setContent(jDiscussion.getContent());
		discussion.setDiscussionTitle(jDiscussion.getTitle());
		discussion.setStartedByUserId(jDiscussion.getFromUserId());
		discussion.setLikes(jDiscussion.getLikes());
		discussion.setParentEntityId(jDiscussion.getParentId());
		discussion.setParentEntityType(jDiscussion.getParentType());
		discussion.setLikeUsers(jDiscussion.getLikeUsers());
		/*
		 * List<JReply> jReplies = jDiscussion.getReplies(); if(jReplies != null
		 * && !jReplies.isEmpty()) { for(JReply jReply : jReplies) { Reply reply
		 * = convert(jReply); discussion.getReplies().add(reply); } }
		 */
		return discussion;
	}
}