package com.pack.pack.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.CommonConstants;
import com.pack.pack.model.GeoTag;
import com.pack.pack.model.RSSFeed;
import com.pack.pack.model.RssSubFeed;
import com.pack.pack.model.SemanticElement;
import com.pack.pack.model.Taxonomy;
import com.pack.pack.model.User;
import com.pack.pack.model.UserInfo;
import com.pack.pack.model.es.UserDetail;
import com.pack.pack.model.web.JGeoLocation;
import com.pack.pack.model.web.JSemanticElement;
import com.pack.pack.model.web.JSharedFeed;
import com.pack.pack.model.web.JUser;
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

	//private static final String HTTP = "http://";
	//private static final String HTTPS = "https://";

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
		rFeed.setId(feed.getId());
		rFeed.setHrefSource(feed.getHrefSource());
		rFeed.setOgDescription(feed.getOgDescription());
		rFeed.setOgImage(feed.getOgImage());
		rFeed.setOgTitle(feed.getOgTitle());
		rFeed.setOgType(feed.getOgType());
		rFeed.setOgUrl(feed.getOgUrl());
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
		rFeed.setHtmlSnippet(feed.getHtmlSnippet());
		rFeed.setSquillUrl(feed.getSquillUrl());
		List<GeoTag> geoTags = feed.getGeoTags();
		if(geoTags != null && !geoTags.isEmpty()) {
			for(GeoTag geoTag : geoTags) {
				rFeed.getGeoTags().add(convert(geoTag));
			}
		}
		String htmlSnippet = rFeed.getHtmlSnippet();
		if(htmlSnippet != null && !htmlSnippet.trim().isEmpty()) {
			rFeed.setFullArticleText(htmlSnippet);
		}
		rFeed.setOpenDirectLink(feed.isOpenDirectLink());
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
		if (rFeed == null)
			return null;
		RSSFeed feed = new RSSFeed();
		feed.setId(rFeed.getId());
		feed.setHrefSource(rFeed.getHrefSource());
		feed.setOgDescription(rFeed.getOgDescription());
		feed.setOgImage(rFeed.getOgImage());
		feed.setOgTitle(rFeed.getOgTitle());
		feed.setOgType(rFeed.getOgType());
		feed.setOgUrl(rFeed.getOgUrl());
		feed.setFeedType(rFeed.getFeedType());
		feed.setVideoUrl(rFeed.getVideoUrl());
		feed.setSquillUrl(rFeed.getSquillUrl());
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
		feed.setHtmlSnippet(rFeed.getHtmlSnippet());
		List<JGeoTag> rGeoTags = rFeed.getGeoTags();
		if(rGeoTags != null && !rGeoTags.isEmpty()) {
			for(JGeoTag rGeoTag : rGeoTags) {
				feed.getGeoTags().add(convert(rGeoTag));
			}
		}
		List<JConcept> rConcepts = rFeed.getConcepts();
		if(rConcepts != null && !rConcepts.isEmpty()) {
			for(JConcept rConcept : rConcepts) {
				feed.getConcepts().add(convert(rConcept, rGeoTags));
			}
		}
		List<JTaxonomy> rTaxonomies = rFeed.getTaxonomies();
		if(rTaxonomies != null && !rTaxonomies.isEmpty()) {
			for(JTaxonomy rTaxonomy : rTaxonomies) {
				feed.getTaxonomies().add(convert(rTaxonomy));
			}
		}
		feed.setOpenDirectLink(rFeed.isOpenDirectLink());
		return feed;
	}
	
	private static Taxonomy convert(JTaxonomy rTaxonomy) {
		Taxonomy taxonomy = new Taxonomy();
		taxonomy.setId(rTaxonomy.getId());
		taxonomy.setName(rTaxonomy.getName());
		return taxonomy;
	}
	
	public static SemanticElement convert(JSemanticElement jSemanticElement) {
		if(jSemanticElement == null)
			return null;
		SemanticElement semanticElement = new SemanticElement();
		semanticElement.setConfidence(jSemanticElement.getConfidence());
		semanticElement.setContent(jSemanticElement.getContent());
		semanticElement.setDbpediaRef(jSemanticElement.getDbpediaRef());
		semanticElement.setStartIndex(jSemanticElement.getStartIndex());
		semanticElement.setEndIndex(jSemanticElement.getEndIndex());
		semanticElement.setConceptId(jSemanticElement.getId());
		semanticElement.setOntologyTypes(jSemanticElement.getOntologyTypes());
		semanticElement.setParentContent(jSemanticElement.getParentContent());
		semanticElement.setSpot(jSemanticElement.getSpot());
		List<JGeoLocation> geoTags = jSemanticElement.getGeoTagSet();
		for(JGeoLocation geoTag : geoTags) {
			semanticElement.getGeoTagSet().add(convertToGeoLocation(geoTag));
		}
		return semanticElement;
	}
	
	public static JSemanticElement convert(SemanticElement semanticElement) {
		if(semanticElement == null)
			return null;
		JSemanticElement jSemanticElement = new JSemanticElement();
		jSemanticElement.setConfidence(semanticElement.getConfidence());
		jSemanticElement.setContent(semanticElement.getContent());
		jSemanticElement.setDbpediaRef(semanticElement.getDbpediaRef());
		jSemanticElement.setStartIndex(semanticElement.getStartIndex());
		jSemanticElement.setEndIndex(semanticElement.getEndIndex());
		jSemanticElement.setId(semanticElement.getConceptId());
		jSemanticElement.setOntologyTypes(semanticElement.getOntologyTypes());
		jSemanticElement.setParentContent(semanticElement.getParentContent());
		jSemanticElement.setSpot(semanticElement.getSpot());
		List<GeoTag> geoTags = semanticElement.getGeoTagSet();
		for(GeoTag geoTag : geoTags) {
			jSemanticElement.getGeoTagSet().add(convertToGeoLocation(geoTag));
		}
		return jSemanticElement;
	}
	
	private static GeoTag convertToGeoLocation(JGeoLocation geoLocation) {
		GeoTag geoTag = new GeoTag();
		geoTag.setLongitude(geoLocation.getLongitude());
		geoTag.setLatitude(geoLocation.getLatitude());
		return geoTag;
	}
	
	private static JGeoLocation convertToGeoLocation(GeoTag geoTag) {
		JGeoLocation geoLocation = new JGeoLocation();
		geoLocation.setLongitude(geoTag.getLongitude());
		geoLocation.setLatitude(geoTag.getLatitude());
		return geoLocation;
	}
	
	private static SemanticElement convert(JConcept rConcept, List<JGeoTag> rGeoTags) {
		SemanticElement concept = new SemanticElement();
		concept.setConfidence(rConcept.getConfidence());
		concept.setContent(rConcept.getContent());
		concept.setDbpediaRef(rConcept.getDbpediaRef());
		concept.setStartIndex(rConcept.getStartIndex());
		concept.setEndIndex(rConcept.getEndIndex());
		concept.setConceptId(rConcept.getId());
		concept.setOntologyTypes(rConcept.getOntologyTypes());
		concept.setParentContent(rConcept.getParentContent());
		concept.setSpot(rConcept.getSpot());
		for(JGeoTag rGeoTag : rGeoTags) {
			concept.getGeoTagSet().add(convert(rGeoTag));
		}
		return concept;
	}
	
	public static JSemanticElement convert(JConcept concept) {
		JSemanticElement semanticElement = new JSemanticElement();
		semanticElement.setConfidence(concept.getConfidence());
		semanticElement.setContent(concept.getContent());
		semanticElement.setDbpediaRef(concept.getDbpediaRef());
		semanticElement.setStartIndex(concept.getStartIndex());
		semanticElement.setEndIndex(concept.getEndIndex());
		semanticElement.setId(concept.getId());
		semanticElement.setOntologyTypes(concept.getOntologyTypes());
		semanticElement.setParentContent(concept.getParentContent());
		semanticElement.setSpot(concept.getSpot());
		return semanticElement;
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

	public static String resolveTopicPrimaryCategory(String subCategory) {
		return CommonConstants.resolvePrimaryCategory(subCategory);
	}

	public static JSharedFeed convertToShareableFeed(JRssFeed feed) {
		JSharedFeed sharedFeed = new JSharedFeed();
		sharedFeed.setActualUrl(feed.getHrefSource() != null ? feed
				.getHrefSource() : feed.getOgUrl());
		sharedFeed.setDescription(feed.getOgDescription());
		sharedFeed.setTitle(feed.getOgTitle());
		sharedFeed.setImageLink(feed.getOgImage());
		sharedFeed.setSummaryText(feed.getArticleSummaryText());
		return sharedFeed;
	}
}