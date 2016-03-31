package com.pack.pack.services.es;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.Address;
import com.pack.pack.model.Topic;
import com.pack.pack.model.User;
import com.pack.pack.model.es.TopicDetail;
import com.pack.pack.model.es.UserDetail;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class ESUploadService {

	private static Logger logger = LoggerFactory
			.getLogger(ESUploadService.class);

	private BlockingQueue<Object> producerQueue = new ArrayBlockingQueue<Object>(
			100, true);

	private ExecutorService pool;

	private boolean stopSignal = false;

	@PostConstruct
	private void start() {
		pool = Executors.newFixedThreadPool(1);
		ConsumerTask consumerTask = new ConsumerTask();
		pool.submit(consumerTask);
	}

	@PreDestroy
	private void stop() {
		stopSignal = true;
		if (producerQueue != null && !producerQueue.isEmpty()) {
			producerQueue.clear();
		}
		if (pool != null && !pool.isTerminated()) {
			pool.shutdownNow();
		}
	}

	public void uploadNewUserDetails(User user) {
		if (!stopSignal) {
			logger.info("Scheduling newly added user " + user.getName()
					+ " for upload to Elasticsearch store.");
			producerQueue.add(user);
		}
	}

	public void uploadNewTopicDetails(Topic topic) {
		if (!stopSignal) {
			logger.info("Scheduling newly added topic " + topic.getName()
					+ " for upload to Elasticsearch store.");
			producerQueue.add(topic);
		}
	}

	private class ConsumerTask implements Runnable {

		private ConsumerTask() {
		}

		@Override
		public void run() {
			try {
				while (!stopSignal) {
					Object object = producerQueue.take();
					if (object instanceof User) {
						User newUser = (User) object;
						uploadNewUserDetails(newUser);
					} else if (object instanceof Topic) {
						Topic topic = (Topic) object;
						uploadNewTopcDetails(topic);
					}
				}
			} catch (Exception e) {
				logger.info(e.getMessage(), e);
			}
		}

		private void uploadNewTopcDetails(Topic topic) throws Exception {
			TopicDetail topicDetail = convert(topic);
			IndexUploadService.INSTANCE.uploadNewTopcDetails(topicDetail);
			logger.info("Successfully uploaded newly created topic details to ES");
		}

		private void uploadNewUserDetails(User newUser) throws Exception {
			UserDetail userDetail = convert(newUser);
			IndexUploadService.INSTANCE.uploadNewUserDetails(userDetail);
			logger.info("Successfully uploaded new user details to ES");
		}

		private TopicDetail convert(Topic topic) {
			TopicDetail esTopic = new TopicDetail();
			esTopic.setCategory(topic.getCategory());
			esTopic.setDescription(topic.getDescription());
			esTopic.setName(topic.getName());
			esTopic.setOwnerId(topic.getOwnerId());
			esTopic.setTopicId(topic.getId());
			return esTopic;
		}

		private UserDetail convert(User newUser) {
			UserDetail userDetail = new UserDetail();
			userDetail.setUserId(newUser.getId());
			userDetail.setName(newUser.getName());
			userDetail.setUserName(newUser.getUsername());
			userDetail.setProfilePictureUrl(newUser.getProfilePicture());
			Address address = newUser.getAddress();
			if (address != null) {
				userDetail.setLocality(address.getLocality());
				userDetail.setCity(address.getCity());
				userDetail.setState(address.getState());
				userDetail.setCountry(address.getCountry());
			}
			return userDetail;
		}
	}
}