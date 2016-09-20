package com.pack.pack.services;

import static com.pack.pack.common.util.CommonConstants.NULL_PAGE_LINK;
import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE;
import static com.pack.pack.common.util.CommonConstants.STANDARD_PAGE_SIZE;
import static com.pack.pack.util.AttachmentUtil.resizeAndStoreUploadedAttachment;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.ITopicService;
import com.pack.pack.model.Pack;
import com.pack.pack.model.Topic;
import com.pack.pack.model.TopicProperty;
import com.pack.pack.model.UserTopicMap;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.JTopics;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.couchdb.TopicRepositoryService;
import com.pack.pack.services.couchdb.UserTopicMapRepositoryService;
import com.pack.pack.services.es.ESUploadService;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.redis.RedisCacheService;
import com.pack.pack.services.redis.PackPage;
import com.pack.pack.services.redis.TopicPage;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.ModelConverter;
import com.pack.pack.util.S3Path;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class TopicServiceImpl implements ITopicService {

	private static Logger logger = LoggerFactory
			.getLogger(TopicServiceImpl.class);

	@Override
	public Pagination<JPack> getAllPacks(String topicId, String pageLink)
			throws PackPackException {
		String key = "topic:" + topicId + ":"
				+ (pageLink != null ? pageLink : NULL_PAGE_LINK);
		if (SystemPropertyUtil.isCacheEnabled()) {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			PackPage packPage = cacheService.getFromCache(key,
					PackPage.class);
			if(packPage != null) {
				return packPage.convert();
			}
		}
		
		logger.debug("Fetching all packs for topicId=" + topicId
				+ " with pageLink=" + pageLink);
		TopicRepositoryService service = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Pagination<Pack> pagination = service.getAllPacks(topicId, pageLink);
		List<Pack> packs = pagination != null ? pagination.getResult() : Collections.emptyList();
		List<JPack> jPacks = ModelConverter.convertAll(packs);
		String nextLink = pagination != null ? pagination.getNextLink() : END_OF_PAGE;
		String previousLink = pagination != null ? pagination.getPreviousLink() : END_OF_PAGE;
		Pagination<JPack> page = new Pagination<JPack>(previousLink, nextLink, jPacks);
		if (SystemPropertyUtil.isCacheEnabled() && page != null) {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			cacheService.addToCache(key, PackPage.build(page));
		}
		return page;
	}

	@Override
	public JTopic getTopicById(String topicId) throws PackPackException {
		// logger.debug("Getting Topic Information for topicId=" + topicId);
		String key = "topic:" + topicId;
		if (SystemPropertyUtil.isCacheEnabled()) {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			JTopic jTopic = cacheService.getFromCache(key, JTopic.class);
			if (jTopic != null) {
				return jTopic;
			}
		}
		TopicRepositoryService service = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Topic topic = service.get(topicId);
		if (topic != null) {
			JTopic jTopic = ModelConverter.convert(topic);
			if (SystemPropertyUtil.isCacheEnabled() && jTopic != null) {
				RedisCacheService cacheService = ServiceRegistry.INSTANCE
						.findService(RedisCacheService.class);
				cacheService.addToCache(key, jTopic);
			}
		}
		return null;
	}

	@Override
	public void followTopic(String userId, String topicId)
			throws PackPackException {
		logger.debug("followTopic(userId=" + userId + ", topicId=" + topicId);
		TopicRepositoryService topicService = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Topic topic = topicService.get(topicId);
		if (topic == null) {
			throw new PackPackException("TODO",
					"No topic found matching the criteria");
		}
		UserTopicMapRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserTopicMapRepositoryService.class);
		UserTopicMap userTopicMap = service.findUserTopicMapById(userId,
				topicId);
		if (userTopicMap == null) {
			userTopicMap = new UserTopicMap();
			userTopicMap.setUserId(userId);
			userTopicMap.setTopicId(topicId);
			userTopicMap.setTopicCategory(topic.getCategory());
			service.add(userTopicMap);
			long followers = topic.getFollowers();
			topic.setFollowers(followers + 1);
			topicService.update(topic);
		}
		
		if (SystemPropertyUtil.isCacheEnabled()) {
			String keyPrefix = "topic:user:" + userId + ":category:"
					+ topic.getCategory();
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			cacheService.removeAllFromCache(keyPrefix);
			keyPrefix = "topic:followed:user:" + userId;
			cacheService.removeAllFromCache(keyPrefix);
		}
		// logger.info("User having id=" + userId + " following topic with id="
		// + topicId);
	}

	@Override
	public Pagination<JTopic> getUserFollowedTopics(String userId,
			String pageLink) throws PackPackException {
		String key = "topic:followed:user:" + userId + ":"
				+ (pageLink != null ? pageLink : NULL_PAGE_LINK);
		if (SystemPropertyUtil.isCacheEnabled()) {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			TopicPage topicPage = cacheService.getFromCache(key,
					TopicPage.class);
			if (topicPage != null) {
				return topicPage.convert();
			}
		}
		UserTopicMapRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserTopicMapRepositoryService.class);
		Pagination<Topic> page = service.getAllTopicsFollowedByUser(userId,
				pageLink);
		List<Topic> topics = page.getResult();
		List<JTopic> jTopics = ModelConverter.convertTopicList(topics);
		Pagination<JTopic> r = new Pagination<JTopic>(page.getPreviousLink(),
				page.getNextLink(), jTopics);
		if (SystemPropertyUtil.isCacheEnabled() && r != null) {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			cacheService.addToCache(key, TopicPage.build(r));
		}
		return r;
	}
	
	@Override
	public Pagination<JTopic> getUserFollowedTopicsFilteredByCategory(
			String userId, String categoryName, String pageLink)
			throws PackPackException {
		String key = "topic:user:" + userId + ":category:" + categoryName + ":";
		String previousLink = null;
		String nextLink = null;
		List<JTopic> result = new LinkedList<JTopic>();
		UserTopicMapRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserTopicMapRepositoryService.class);
		if(!pageLink.startsWith("NOT_FOLLOWING")) {
			key = key + (pageLink != null ? pageLink : NULL_PAGE_LINK); 
			if (SystemPropertyUtil.isCacheEnabled()) {
				RedisCacheService cacheService = ServiceRegistry.INSTANCE
						.findService(RedisCacheService.class);
				TopicPage topicPage = cacheService.getFromCache(key,
						TopicPage.class);
				if (topicPage != null) {
					return topicPage.convert();
				}
			}
			Pagination<Topic> page = service.getAllTopicsFollowedByUserAndCategory(
					userId, categoryName, pageLink);
			List<Topic> topics = page != null ? page.getResult() : Collections.emptyList();
			List<JTopic> jTopics = ModelConverter.convertTopicList(topics, true);
			result = jTopics;
			previousLink = page != null ? page.getPreviousLink() : END_OF_PAGE;
			nextLink = page != null ? page.getNextLink() : END_OF_PAGE;
		}
		else {
			pageLink = pageLink.substring("NOT_FOLLOWING".length());
			key = key + pageLink + ":NOT";
			if (SystemPropertyUtil.isCacheEnabled()) {
				RedisCacheService cacheService = ServiceRegistry.INSTANCE
						.findService(RedisCacheService.class);
				TopicPage topicPage = cacheService.getFromCache(key,
						TopicPage.class);
				if (topicPage != null) {
					return topicPage.convert();
				}
			}
			Pagination<Topic> page = service.getAllTopicsNotFollowedByUserAndCategory(
					userId, categoryName, pageLink);
			List<Topic> topics = page != null ? page.getResult() : Collections.emptyList();
			List<JTopic> jTopics = ModelConverter.convertTopicList(topics, false);
			result = jTopics;
			previousLink = page != null ? "NOT_FOLLOWING" + page.getPreviousLink() 
					: "NOT_FOLLOWING" + END_OF_PAGE;
			nextLink = page != null ? "NOT_FOLLOWING" + page.getNextLink() 
					: "NOT_FOLLOWING" + END_OF_PAGE;
		}		
		if(result.size() < STANDARD_PAGE_SIZE) {
			Pagination<Topic> page = service.getAllTopicsNotFollowedByUserAndCategory(
					userId, categoryName, pageLink);
			List<Topic> topics = page.getResult();
			if(topics != null && !topics.isEmpty()) {
				List<JTopic> jTopics = ModelConverter.convertTopicList(topics, false);
				result.addAll(jTopics);
				previousLink = page != null ? "NOT_FOLLOWING" + page.getPreviousLink() 
						: "NOT_FOLLOWING" + END_OF_PAGE;
				nextLink = page != null ? "NOT_FOLLOWING" + page.getNextLink() 
						: "NOT_FOLLOWING" + END_OF_PAGE;
			}
		}		
		Pagination<JTopic> page = new Pagination<JTopic>(previousLink, nextLink, result);
		if (SystemPropertyUtil.isCacheEnabled() && page != null) {
			TopicPage topicPage = TopicPage.build(page);
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			cacheService.addToCache(key, topicPage);
		}
		return page;
	}

	@Override
	public Pagination<JTopic> getAllTopicListing(String userId, String pageLink)
			throws PackPackException {
		UserTopicMapRepositoryService mapService = ServiceRegistry.INSTANCE
				.findService(UserTopicMapRepositoryService.class);
		Pagination<Topic> page = mapService.getAllTopicsFollowedByUser(userId,
				pageLink);
		String nextLink = page != null ? page.getNextLink() : END_OF_PAGE;
		String previousLink = page != null ? page.getPreviousLink() : END_OF_PAGE;
		List<Topic> topics = page != null ? page.getResult() : Collections
				.emptyList();
		List<JTopic> jTopics = ModelConverter.convertTopicList(topics);
		return new Pagination<JTopic>(previousLink, nextLink, jTopics);
	}

	@Override
	public void neglectTopic(String userId, String topicId)
			throws PackPackException {
		// logger.debug("neglectTopic(userId=" + userId + ", topicId=" +
		// topicId);
		UserTopicMapRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserTopicMapRepositoryService.class);
		UserTopicMap userTopicMap = service.findUserTopicMapById(userId,
				topicId);
		if (userTopicMap == null)
			return;
		service.remove(userTopicMap);
		TopicRepositoryService topicService = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Topic topic = topicService.get(topicId);
		if (topic != null) {
			long followers = topic.getFollowers();
			topic.setFollowers(followers - 1);

		}
		if (SystemPropertyUtil.isCacheEnabled()) {
			String keyPrefix = "topic:user:" + userId + ":category:"
					+ topic.getCategory();
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			cacheService.removeAllFromCache(keyPrefix);
		}
	}

	@Override
	public JTopic createNewTopic(JTopic jTopic, InputStream wallpaper,
			String wallpaperName) throws PackPackException {
		TopicRepositoryService service = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Topic topic = ModelConverter.convert(jTopic);
		service.add(topic);
		String location = storeTopicWallpaper(topic.getId(), wallpaper,
				wallpaperName);
		topic.setWallpaperUrl(location);
		service.update(topic);
		followTopic(topic.getOwnerId(), topic.getId());
		ESUploadService esUploadService = ServiceRegistry.INSTANCE
				.findService(ESUploadService.class);
		esUploadService.uploadNewTopicDetails(topic);
		if (SystemPropertyUtil.isCacheEnabled()) {
			String keyPrefix = "topic:owner:" + topic.getOwnerId();
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			cacheService.removeAllFromCache(keyPrefix);
			keyPrefix = "topic:category:" + topic.getCategory();
			cacheService.removeAllFromCache(keyPrefix);
		}
		return ModelConverter.convert(topic);
	}
	
	private String storeTopicWallpaper(String topicId,
			InputStream wallpaper, String wallpaperFileName)
			throws PackPackException {
		String home = SystemPropertyUtil.getTopicWallpaperHome();
		String location = home;
		if (!location.endsWith(File.separator)) {
			location = location + File.separator;
		}
		location = location + topicId;
		File f = new File(location);
		if (!f.exists()) {
			f.mkdir();
		}
		location = location + File.separator + wallpaperFileName;
		S3Path root = new S3Path(topicId, false);
		root.addChild(new S3Path(wallpaperFileName, true));
		resizeAndStoreUploadedAttachment(wallpaper, location, 100, 100, root);
		return location.substring(home.length());
	}

	@Override
	public Pagination<JTopic> getAllTopicsByCategoryName(String categoryName,
			String pageLink) throws PackPackException {
		String key = "topic:category:" + categoryName;
		if (SystemPropertyUtil.isCacheEnabled()) {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			TopicPage topicPage = cacheService.getFromCache(key,
					TopicPage.class);
			if (topicPage != null) {
				return topicPage.convert();
			}
		}
		TopicRepositoryService service = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Pagination<Topic> page = service.getAllTopicsByCategoryName(
				categoryName, pageLink);
		List<JTopic> topics = ModelConverter.convertTopicList(page.getResult());
		Pagination<JTopic> r = new Pagination<JTopic>(page.getPreviousLink(),
				page.getNextLink(), topics);
		if (SystemPropertyUtil.isCacheEnabled()) {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			cacheService.addToCache(key, TopicPage.build(r));
		}
		return r;
	}
	
	@Override
	public JTopics getAllTopicsOwnedByUser(String userId)
			throws PackPackException {
		String key = "topic:owner:" + userId;
		if(SystemPropertyUtil.isCacheEnabled()) {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			JTopics jTopics = cacheService.getFromCache(key, JTopics.class);
			if(jTopics != null) {
				return jTopics;
			}
		}
		JTopics result = new JTopics();
		TopicRepositoryService service = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		List<Topic> topics = service.getAllTopicsOwnedByUser(userId);
		if (topics == null || topics.isEmpty()) {
			return result;
		}
		for (Topic topic : topics) {
			result.getTopics().add(ModelConverter.convert(topic));
		}
		if (SystemPropertyUtil.isCacheEnabled()) {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			cacheService.addToCache(key, result);
		}
		return result;
	}

	@Override
	public JTopic editTopicSettings(String topicId, String key, String value,
			String ownerId) throws PackPackException {
		if (ownerId == null) {
			throw new PackPackException(ErrorCodes.PACK_ERR_93,
					"Permission Denied. Not a valid topic Owner");
		}
		TopicRepositoryService service = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Topic topic = service.get(topicId);
		if (topic == null) {
			throw new PackPackException(ErrorCodes.PACK_ERR_01,
					"No topic found wit ID = " + topicId);
		}
		String ownerId2 = topic.getOwnerId();
		if (!ownerId.equals(ownerId2)) {
			throw new PackPackException(ErrorCodes.PACK_ERR_93,
					"Permission Denied. Not a valid topic Owner");
		}
		boolean isNew = true;
		Iterator<TopicProperty> itr = topic.getPropeties().iterator();
		while(itr.hasNext()) {
			TopicProperty property = itr.next();
			if(property.getKey().equals(key)) {
				property.setValue(value);
				isNew = false;
			}
		}
		if(isNew) {
			topic.getPropeties().add(new TopicProperty(key, value));
		}
		service.update(topic);
		return ModelConverter.convert(topic);
	}
}