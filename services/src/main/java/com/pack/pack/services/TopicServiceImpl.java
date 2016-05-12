package com.pack.pack.services;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.ITopicService;
import com.pack.pack.model.Pack;
import com.pack.pack.model.Topic;
import com.pack.pack.model.UserTopicMap;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.couchdb.TopicRepositoryService;
import com.pack.pack.services.couchdb.UserTopicMapRepositoryService;
import com.pack.pack.services.es.ESUploadService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;

import static com.pack.pack.util.AttachmentUtil.*;

import com.pack.pack.util.ModelConverter;
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
		logger.debug("Fetching all packs for topicId=" + topicId
				+ " with pageLink=" + pageLink);
		TopicRepositoryService service = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Pagination<Pack> pagination = service.getAllPacks(topicId, pageLink);
		List<Pack> packs = pagination.getResult();
		List<JPack> jPacks = ModelConverter.convertAll(packs);
		return new Pagination<JPack>(pagination.getPreviousLink(),
				pagination.getNextLink(), jPacks);
	}

	@Override
	public JTopic getTopicById(String topicId) throws PackPackException {
		// logger.debug("Getting Topic Information for topicId=" + topicId);
		TopicRepositoryService service = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Topic topic = service.get(topicId);
		if (topic != null) {
			return ModelConverter.convert(topic);
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
		// logger.info("User having id=" + userId + " following topic with id="
		// + topicId);
	}

	@Override
	public Pagination<JTopic> getUserFollowedTopics(String userId,
			String pageLink) throws PackPackException {
		/*
		 * logger.debug(
		 * "Fetching user followed topics in pagination API for userId=" +
		 * userId + ", pageLink=" + pageLink);
		 */
		UserTopicMapRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserTopicMapRepositoryService.class);
		Pagination<Topic> page = service.getAllTopicsFollowedByUser(userId,
				pageLink);
		List<Topic> topics = page.getResult();
		List<JTopic> jTopics = ModelConverter.convertTopicList(topics);
		return new Pagination<JTopic>(page.getPreviousLink(),
				page.getNextLink(), jTopics);
	}
	
	@Override
	public Pagination<JTopic> getUserFollowedTopicsFilteredByCategory(
			String userId, String categoryName, String pageLink)
			throws PackPackException {
		UserTopicMapRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserTopicMapRepositoryService.class);
		Pagination<Topic> page = service.getAllTopicsFollowedByUserAndCategory(
				userId, categoryName, pageLink);
		List<Topic> topics = page != null ? page.getResult() : Collections.emptyList();
		List<JTopic> jTopics = ModelConverter.convertTopicList(topics);
		return new Pagination<JTopic>(page.getPreviousLink(),
				page.getNextLink(), jTopics);
	}

	@Override
	public Pagination<JTopic> getAllTopicListing(String userId, String pageLink)
			throws PackPackException {
		/*
		 * logger.debug("Loading all topics as requested by user having userId="
		 * + userId + " page info(page-link)=" + pageLink);
		 */
		UserTopicMapRepositoryService mapService = ServiceRegistry.INSTANCE
				.findService(UserTopicMapRepositoryService.class);
		Pagination<Topic> page = mapService.getAllTopicsFollowedByUser(userId,
				pageLink);
		String nextLink = page != null ? page.getNextLink() : null;
		String previousLink = page != null ? page.getPreviousLink() : null;
		List<Topic> topics = page != null ? page.getResult() : Collections
				.emptyList();
		List<JTopic> jTopics = ModelConverter.convertTopicList(topics);
		return new Pagination<JTopic>(previousLink, nextLink, jTopics);
		/*
		 * TopicRepositoryService service = ServiceRegistry.INSTANCE
		 * .findService(TopicRepositoryService.class); Pagination<Topic> page =
		 * service.getAllTopics(userId, pageLink); String nextLink =
		 * page.getNextLink(); String previousLink = page.getPreviousLink();
		 * List<Topic> topics = page.getResult(); List<JTopic> jTopics =
		 * ModelConverter.convertTopicList(topics); return new
		 * Pagination<JTopic>(previousLink, nextLink, jTopics);
		 */
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
		/*
		 * logger.info("User having ID=" + userId +
		 * " is neglecting topic whose ID=" + topicId);
		 */
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
		resizeAndStoreUploadedAttachment(wallpaper, location, 100, 100);
		return location.substring(home.length());
	}

	@Override
	public Pagination<JTopic> getAllTopicsByCategoryName(String categoryName,
			String pageLink) throws PackPackException {
		TopicRepositoryService service = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Pagination<Topic> page = service.getAllTopicsByCategoryName(
				categoryName, pageLink);
		List<JTopic> topics = ModelConverter.convertTopicList(page.getResult());
		return new Pagination<JTopic>(page.getPreviousLink(),
				page.getNextLink(), topics);
	}
}