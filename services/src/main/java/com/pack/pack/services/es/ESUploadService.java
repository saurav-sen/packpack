package com.pack.pack.services.es;

import static com.pack.pack.util.SystemPropertyUtil.CONTENT_TYPE_HEADER_NAME;
import static com.pack.pack.util.SystemPropertyUtil.ES_USER_DOC_TYPE;
import static com.pack.pack.util.SystemPropertyUtil.URL_SEPARATOR;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.Address;
import com.pack.pack.model.User;
import com.pack.pack.model.es.UserDetail;
import com.pack.pack.util.SystemPropertyUtil;

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

	private BlockingQueue<User> producerQueue = new ArrayBlockingQueue<User>(
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

	private class ConsumerTask implements Runnable {

		private CloseableHttpClient client;

		private String esUploadUrl;

		private ConsumerTask() {
			client = HttpClientBuilder.create().build();
			String esUrl = SystemPropertyUtil.getElasticSearchBaseUrl();
			if (!esUrl.endsWith(URL_SEPARATOR)) {
				esUrl = esUploadUrl + URL_SEPARATOR;
			}
			esUploadUrl = new StringBuilder(esUrl)
					.append(SystemPropertyUtil
							.getElasticSearchDefaultDocumentName())
					.append(URL_SEPARATOR).append(ES_USER_DOC_TYPE)
					.append(URL_SEPARATOR).toString();

		}

		@Override
		public void run() {
			try {
				while (!stopSignal) {
					User newUser = producerQueue.take();
					uploadNewUserDetails(newUser);
				}
			} catch (Exception e) {
				logger.info(e.getMessage(), e);
			} finally {
				try {
					if (client != null) {
						client.close();
					}
				} catch (IOException e) {
					logger.info(e.getMessage(), e);
				}
			}
		}

		private void uploadNewUserDetails(User newUser) throws Exception {
			UserDetail userDetail = convert(newUser);
			String url = new StringBuilder(esUploadUrl).append(newUser.getId())
					.toString();
			HttpPut PUT = new HttpPut(url);
			PUT.addHeader(CONTENT_TYPE_HEADER_NAME,
					ContentType.APPLICATION_JSON.getMimeType());
			String json = JSONUtil.serialize(userDetail);
			HttpEntity jsonBody = new StringEntity(json,
					ContentType.APPLICATION_JSON);
			PUT.setEntity(jsonBody);
			CloseableHttpResponse response = client.execute(PUT);
			logger.info("Successfully uploaded new user details to ES @ PUT "
					+ esUploadUrl);
			logger.info(EntityUtils.toString(response.getEntity()));
		}

		private UserDetail convert(User newUser) {
			UserDetail userDetail = new UserDetail();
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