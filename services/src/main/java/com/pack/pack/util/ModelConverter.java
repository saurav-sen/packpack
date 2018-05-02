package com.pack.pack.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.IUserService;
import com.pack.pack.common.util.CommonConstants;
import com.pack.pack.model.Concept;
import com.pack.pack.model.GeoTag;
import com.pack.pack.model.RSSFeed;
import com.pack.pack.model.RssSubFeed;
import com.pack.pack.model.Taxonomy;
import com.pack.pack.model.User;
import com.pack.pack.model.UserInfo;
import com.pack.pack.model.es.UserDetail;
import com.pack.pack.model.web.JSharedFeed;
import com.pack.pack.model.web.JUser;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.squill.feed.web.model.JConcept;
import com.squill.feed.web.model.JGeoTag;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssSubFeed;
import com.squill.feed.web.model.JTaxonomy;

/**
 * 
 * @author Saurav
 *
 */
public class ModelConverter {

	private static Logger logger = LoggerFactory
			.getLogger(ModelConverter.class);

	private static final String HTTP = "http://";
	private static final String HTTPS = "https://";

	public static List<JRssFeed> convertAllRssFeeds(List<RSSFeed> feeds, boolean ignoreVideoFeeds, boolean ignoreSlideShows) {
		if (feeds == null || feeds.isEmpty())
			return Collections.emptyList();
		List<JRssFeed> results = new LinkedList<JRssFeed>();
		for (RSSFeed feed : feeds) {
			if(feed.getVideoUrl() != null && !feed.getVideoUrl().trim().isEmpty()) {
				if(ignoreVideoFeeds) {
					continue;
				}
			}
			if(feed.getSiblings() != null && !feed.getSiblings().isEmpty()) {
				if(ignoreSlideShows) {
					continue;
				}
			}
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
		rFeed.setVideoUrl(feed.getVideoUrl());
		rFeed.setUploadTime(feed.getUploadTime());
		rFeed.setShareableUrl(feed.getShareableUrl());
		List<RssSubFeed> siblings = feed.getSiblings();
		if(siblings != null && !siblings.isEmpty()) {
			for(RssSubFeed sibling : siblings) {
				rFeed.getSiblings().add(convert(sibling));
			}
		}
		rFeed.setArticleSummaryText(feed.getArticleSummaryText());
		rFeed.setFullArticleText(feed.getFullArticleText());
		List<GeoTag> geoTags = feed.getGeoTags();
		if(geoTags != null && !geoTags.isEmpty()) {
			for(GeoTag geoTag : geoTags) {
				rFeed.getGeoTags().add(convert(geoTag));
			}
		}
		return rFeed;
	}
	
	private static JGeoTag convert(GeoTag geoTag) {
		JGeoTag jGeoTag = new JGeoTag();
		jGeoTag.setLatitude(geoTag.getLatitude());
		jGeoTag.setLongitude(geoTag.getLongitude());
		return jGeoTag;
	}
	
	private static GeoTag convert(JGeoTag jGeoTag) {
		GeoTag geoTag = new GeoTag();
		geoTag.setLatitude(jGeoTag.getLatitude());
		geoTag.setLongitude(jGeoTag.getLongitude());
		return geoTag;
	}
	
	private static JRssSubFeed convert(RssSubFeed sibling) {
		JRssSubFeed result = new JRssSubFeed();
		result.setHrefSource(sibling.getHrefSource());
		result.setOgDescription(sibling.getOgDescription());
		result.setOgImage(sibling.getOgImage());
		result.setOgTitle(sibling.getOgTitle());
		result.setVideoUrl(sibling.getVideoUrl());
		return result;
	}
	
	public static RSSFeed convert(JRssFeed rFeed) {
		return convert(rFeed, rFeed.getId());
	}

	private static RSSFeed convert(JRssFeed rFeed, String id) {
		if (rFeed == null)
			return null;
		RSSFeed feed = new RSSFeed();
		feed.setHrefSource(rFeed.getHrefSource());
		feed.setOgDescription(rFeed.getOgDescription());
		feed.setOgImage(rFeed.getOgImage());
		feed.setOgTitle(rFeed.getOgTitle());
		feed.setOgType(rFeed.getOgType());
		feed.setOgUrl(rFeed.getOgUrl());
		feed.setFeedType(rFeed.getFeedType());
		if(id == null) {
			id = rFeed.getId();
		}
		feed.setId(id);
		feed.setVideoUrl(rFeed.getVideoUrl());
		feed.setUploadTime(System.currentTimeMillis());
		List<JRssSubFeed> siblings = rFeed.getSiblings();
		if(siblings != null && !siblings.isEmpty()) {
			for(JRssSubFeed sibling : siblings) {
				feed.getSiblings().add(convert(sibling));
			}
		}
		feed.setShareableUrl(rFeed.getShareableUrl());
		feed.setArticleSummaryText(rFeed.getArticleSummaryText());
		feed.setFullArticleText(rFeed.getFullArticleText());
		List<JGeoTag> rGeoTags = rFeed.getGeoTags();
		if(rGeoTags != null && !rGeoTags.isEmpty()) {
			for(JGeoTag rGeoTag : rGeoTags) {
				feed.getGeoTags().add(convert(rGeoTag));
			}
		}
		List<JConcept> rConcepts = rFeed.getConcepts();
		if(rConcepts != null && !rConcepts.isEmpty()) {
			for(JConcept rConcept : rConcepts) {
				feed.getConcepts().add(convert(rConcept));
			}
		}
		List<JTaxonomy> rTaxonomies = rFeed.getTaxonomies();
		if(rTaxonomies != null && !rTaxonomies.isEmpty()) {
			for(JTaxonomy rTaxonomy : rTaxonomies) {
				feed.getTaxonomies().add(convert(rTaxonomy));
			}
		}
		return feed;
	}
	
	private static Taxonomy convert(JTaxonomy rTaxonomy) {
		Taxonomy taxonomy = new Taxonomy();
		taxonomy.setId(rTaxonomy.getId());
		taxonomy.setName(rTaxonomy.getName());
		taxonomy.setParentRefUrl(rTaxonomy.getParentRefUrl());
		taxonomy.setRefUri(rTaxonomy.getRefUri());
		return taxonomy;
	}
	
	private static Concept convert(JConcept rConcept) {
		Concept concept = new Concept();
		concept.setConfidence(rConcept.getConfidence());
		concept.setContent(rConcept.getContent());
		concept.setDbpediaRef(rConcept.getDbpediaRef());
		concept.setStartIndex(rConcept.getStartIndex());
		concept.setEndIndex(rConcept.getEndIndex());
		concept.setId(rConcept.getId());
		concept.setOntologyTypes(rConcept.getOntologyTypes());
		concept.setParentContent(rConcept.getParentContent());
		concept.setSpot(rConcept.getSpot());
		return concept;
	}
	
	private static RssSubFeed convert(JRssSubFeed sibling) {
		RssSubFeed result = new RssSubFeed();
		result.setHrefSource(sibling.getHrefSource());
		result.setOgDescription(sibling.getOgDescription());
		result.setOgImage(sibling.getOgImage());
		result.setOgTitle(sibling.getOgTitle());
		result.setVideoUrl(sibling.getVideoUrl());
		return result;
	}

	public static JUser convert(User user) {
		JUser jUser = new JUser();
		jUser.setId(user.getId());
		jUser.setDob(user.getDob());
		jUser.setName(user.getName());
		jUser.setUsername(user.getUsername());
		jUser.setCity(user.getCity());
		jUser.setCountry(user.getCountry());
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
		String baseURL = SystemPropertyUtil
				.getProfilePictureBaseURL(profilePicture);
		String profilePictureUrl = profilePicture;
		profilePictureUrl = profilePictureUrl.replaceAll(File.separator,
				SystemPropertyUtil.URL_SEPARATOR);
		if (profilePictureUrl.startsWith("http://")
				|| profilePictureUrl.startsWith("https://")) {
			logger.trace("Resolved URL for profile picture = "
					+ profilePictureUrl
					+ " it is already pointing to absloute location");
			return profilePictureUrl;
		}
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
		String baseURL = SystemPropertyUtil
				.getTopicWallpaperBaseURL(wallpaperLocation);
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

	private static JUser getUserInfo(String userId) throws PackPackException {
		IUserService service = ServiceRegistry.INSTANCE
				.findCompositeService(IUserService.class);
		return service.findUserById(userId);
	}

	public static String resolveTopicPrimaryCategory(String subCategory) {
		return CommonConstants.resolvePrimaryCategory(subCategory);
	}

	private static String resolveEGiftUrl(String url) {
		String baseURL = SystemPropertyUtil.getImageAttachmentBaseURL(url);
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

	public static JSharedFeed convertToShareableFeed(JRssFeed feed) {
		JSharedFeed sharedFeed = new JSharedFeed();
		sharedFeed.setActualUrl(feed.getHrefSource() != null ? feed
				.getHrefSource() : feed.getOgUrl());
		sharedFeed.setDescription(feed.getOgDescription());
		sharedFeed.setTitle(feed.getOgTitle());
		sharedFeed.setImageLink(feed.getOgImage());
		return sharedFeed;
	}
}