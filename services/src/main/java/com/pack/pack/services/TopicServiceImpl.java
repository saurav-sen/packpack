package com.pack.pack.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.ITopicService;
import com.pack.pack.model.Pack;
import com.pack.pack.model.Topic;
import com.pack.pack.model.UserTopicMap;
import com.pack.pack.model.web.JPacks;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.services.couchdb.Pagination;
import com.pack.pack.services.couchdb.TopicRepositoryService;
import com.pack.pack.services.couchdb.UserTopicMapRepositoryService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.ModelConverter;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class TopicServiceImpl implements ITopicService {
	
	private static Logger logger = LoggerFactory.getLogger(TopicServiceImpl.class);

	@Override
	public JPacks getAllPacks(String topicId, int pageNo)
			throws PackPackException {
		logger.debug("Fetching all packs for topicId=" + topicId + " with pageNumber=" + pageNo);
		TopicRepositoryService service = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		List<Pack> packs = service.getAllPacks(topicId, pageNo);
		return ModelConverter.convert(packs);
	}

	@Override
	public JTopic getTopicById(String topicId) throws PackPackException {
		//logger.debug("Getting Topic Information for topicId=" + topicId);
		TopicRepositoryService service = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Topic topic = service.getTopicById(topicId);
		if (topic != null) {
			return ModelConverter.convert(topic);
		}
		return null;
	}

	@Override
	public void followTopic(String userId, String topicId)
			throws PackPackException {
		//logger.debug("followTopic(userId=" + userId + ", topicId=" + topicId);
		UserTopicMapRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserTopicMapRepositoryService.class);
		UserTopicMap userTopicMap = service.findUserTopicMapById(userId,
				topicId);
		if (userTopicMap == null) {
			userTopicMap = new UserTopicMap();
			userTopicMap.setUserId(userId);
			userTopicMap.setTopicId(topicId);
			service.add(userTopicMap);
		}
		//logger.info("User having id=" + userId + " following topic with id=" + topicId);
	}

	@Override
	public Pagination<JTopic> getUserFollowedTopics(String userId,
			String pageLink) throws PackPackException {
		/*logger.debug("Fetching user followed topics in pagination API for userId="
				+ userId + ", pageLink=" + pageLink);*/
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
	public Pagination<JTopic> getAllTopicListing(String userId, String pageLink)
			throws PackPackException {
		/*logger.debug("Loading all topics as requested by user having userId="
				+ userId + " page info(page-link)=" + pageLink);*/
		TopicRepositoryService service = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Pagination<Topic> page = service.getAllTopics(userId, pageLink);
		String nextLink = page.getNextLink();
		String previousLink = page.getPreviousLink();
		List<Topic> topics = page.getResult();
		List<JTopic> jTopics = ModelConverter.convertTopicList(topics);
		return new Pagination<JTopic>(previousLink, nextLink, jTopics);
	}

	@Override
	public void neglectTopic(String userId, String topicId)
			throws PackPackException {
		//logger.debug("neglectTopic(userId=" + userId + ", topicId=" + topicId);
		UserTopicMapRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserTopicMapRepositoryService.class);
		UserTopicMap userTopicMap = service.findUserTopicMapById(userId,
				topicId);
		if (userTopicMap == null)
			return;
		service.remove(userTopicMap);
		/*logger.info("User having ID=" + userId
				+ " is neglecting topic whose ID=" + topicId);*/
	}

	@Override
	public JTopic createNewTopic(JTopic jTopic) throws PackPackException {
		/*logger.debug("creating new topic " + jTopic.getName()
				+ " by user(owner) having ID=" + jTopic.getOwnerId());*/
		TopicRepositoryService service = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		Topic topic = ModelConverter.convert(jTopic);
		service.add(topic);
		/*logger.info("Successfully created new topic " + jTopic.getName()
				+ " by user(owner) having ID=" + jTopic.getOwnerId()
				+ " User(owner) name=" + jTopic.getOwnerName());*/
		return ModelConverter.convert(topic);
	}
}