package com.pack.pack.services;

import java.util.List;

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

	@Override
	public JPacks getAllPacks(String topicId, int pageNo)
			throws PackPackException {
		TopicRepositoryService service = ServiceRegistry.INSTANCE
				.findService(TopicRepositoryService.class);
		List<Pack> packs = service.getAllPacks(topicId, pageNo);
		return ModelConverter.convert(packs);
	}

	@Override
	public JTopic getTopicById(String topicId) throws PackPackException {
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
	}

	@Override
	public Pagination<JTopic> getUserFollowedTopics(String userId,
			String pageLink) throws PackPackException {
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
		UserTopicMapRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserTopicMapRepositoryService.class);
		UserTopicMap userTopicMap = service.findUserTopicMapById(userId,
				topicId);
		if (userTopicMap == null)
			return;
		service.remove(userTopicMap);
	}
	
	@Override
	public JTopic createNewTopic(JTopic topic) throws PackPackException {
		// TODO Auto-generated method stub
		TopicRepositoryService service = ServiceRegistry.INSTANCE.findService(TopicRepositoryService.class);
		//ModelConverter.co
		return null;
	}
}