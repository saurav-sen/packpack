package com.pack.pack.services.es;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.User;
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

	//@PostConstruct
	public void start() {
		pool = Executors.newFixedThreadPool(1);
		ConsumerTask consumerTask = new ConsumerTask();
		pool.submit(consumerTask);
	}

	//@PreDestroy
	public void stop() {
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
					}
				}
			} catch (Exception e) {
				logger.info(e.getMessage(), e);
			}
		}

		private void uploadNewUserDetails(User newUser) throws Exception {
			UserDetail userDetail = convert(newUser);
			// TODO -- call an check this once we add ES infrastructure
			// IndexUploadService.INSTANCE.uploadNewUserDetails(userDetail);
			logger.info("Successfully uploaded new user details to ES");
		}

		private UserDetail convert(User newUser) {
			UserDetail userDetail = new UserDetail();
			userDetail.setUserId(newUser.getId());
			userDetail.setName(newUser.getName());
			userDetail.setUserName(newUser.getUsername());
			userDetail.setProfilePictureUrl(newUser.getProfilePicture());
			userDetail.setCity(newUser.getCity());
			return userDetail;
		}
	}
}