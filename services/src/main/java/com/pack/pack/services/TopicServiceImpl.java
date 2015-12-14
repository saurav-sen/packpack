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
import com.pack.pack.model.web.JTopics;
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
	public JTopics getUserFollowedTopics(String userId)
			throws PackPackException {
		UserTopicMapRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserTopicMapRepositoryService.class);
		List<Topic> topics = service.getAllTopicsFollowedByUser(userId);
		List<JTopic> list = ModelConverter.convertTopicList(topics);
		JTopics jTopics = new JTopics();
		jTopics.setTopics(list);
		return jTopics;
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
}