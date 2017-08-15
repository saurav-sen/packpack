package com.pack.pack.services.rabbitmq;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amazonaws.util.json.JSONObject;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.notification.FeedMsg;
import com.pack.pack.model.web.notification.FeedMsgType;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.rabbitmq.objects.BroadcastCriteria;
import com.pack.pack.util.RssFeedUtil;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class MessagePublisher {
	
	/*@Autowired
	private MsgConnectionManager connectionManager;*/
	
	private static Logger logger = LoggerFactory.getLogger(MessagePublisher.class);
	
	public final static String AUTH_KEY_FCM = "AAAApQZn_ZI:APA91bGidkJYWfz2JYHTPXWr5a0NrLwV6K2DE-z57eIoLpBmUgqaUQ239pGVbDA8Aw_KKZqBFfsxLYv3wp2bjH1XXN-uGeRuAQNpN7LrAsfWJBikVAedOzs0GvzNzPHK2eWdSKVmLXod";
	public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
	
	/*public void broadcastNewRSSFeedUpload_old(JRssFeed feed, BroadcastCriteria criteria, boolean sendNotification) throws PackPackException {
		MsgConnection connection = null;
		try {
			connection = connectionManager.openConnection();
			Channel channel = connection.getChannel();
			FeedMsg msg = new FeedMsg();
			msg.setTitle(feed.getOgTitle());
			msg.setKey(RssFeedUtil.generateUploadKey(feed));
			msg.setTimestamp(String.valueOf(System.currentTimeMillis()));
			String message = JSONUtil.serialize(msg);
			String exchange_name = resolveExchangeName(criteria);
			channel.exchangeDeclare(exchange_name, "fanout");
			if(sendNotification) {
				channel.basicPublish(exchange_name, "", null, message.getBytes());
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new PackPackException("", e.getMessage(), e);
		} catch (TimeoutException e) {
			logger.error(e.getMessage(), e);
			throw new PackPackException("", e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new PackPackException("", e.getMessage(), e);
		} finally {
			try {
				connectionManager.closeConnection(connection);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			} catch (TimeoutException e) {
				logger.error(e.getMessage(), e);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}*/
	
	public void broadcastNewRSSFeedUpload(JRssFeed feed, BroadcastCriteria criteria, boolean sendNotification) throws PackPackException {
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClientBuilder.create().build();
			HttpPost POST = new HttpPost(API_URL_FCM);
			POST.addHeader("Authorization", "key=" + AUTH_KEY_FCM);
			POST.addHeader("Content-Type", "application/json");
			
			FeedMsg msg = new FeedMsg();
			msg.setTitle(feed.getOgTitle());
			msg.setKey(RssFeedUtil.generateUploadKey(feed));
			msg.setTimestamp(String.valueOf(System.currentTimeMillis()));
			msg.setMsgType(FeedMsgType.SQUILL_TEAM);
			msg.setDataObj(JSONUtil.serialize(feed));
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("to", "/topics/allDevices");
			String json = JSONUtil.serialize(msg);	
			jsonObj.put("data", json);
			
			POST.setEntity(new StringEntity(jsonObj.toString(), ContentType.APPLICATION_JSON));
			httpClient.execute(POST);
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if(httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	private String resolveExchangeName(BroadcastCriteria criteria) {
		String exchange_name = "Feeds_";
		if(criteria == null) {
			exchange_name = exchange_name + "global";
			return exchange_name;
		}
		exchange_name = exchange_name + criteria.getCity() + "_"
				+ criteria.getState() + "_" + criteria.getCountry();
		return exchange_name;
	}
 	
	/*public void forwardPack(FwdPack fwdPack, User user) throws PackPackException {
		try {
			MsgConnection connection = connectionManager.openConnection();
			Channel channel = connection.getChannel();
			String msg_queue = user.getId() + "_" + user.getUsername();
			channel.queueDeclare(msg_queue, false, false, false, null);
			String message = JSONUtil.serialize(fwdPack);
			BasicProperties props = new BasicProperties(null, null, null, 0, 0,
					null, Constants.REPLY_TO_USER_PREFIX + user.getId(), null, null, null, null,
					user.getUsername(), null, null);
			channel.basicPublish("", msg_queue, props, message.getBytes());
		} catch (IOException e) {
			new PackPackException("TODO", e.getMessage(), e);
		} catch (TimeoutException e) {
			new PackPackException("TODO", e.getMessage(), e);
		} catch (Exception e) {
			throw new PackPackException("", e.getMessage(), e);
		}
	}
	
	public void notifyPackModify(FwdPack fwdPack, Topic topic, User fromUser) throws PackPackException {
		MsgConnection connection = null;
		try {
			connection = connectionManager.openConnection();
			Channel channel = connection.getChannel();
			String exchange_name = "topic_" + topic.getId() + "_" + topic.getName();
			logger.debug("RabbitMQ exchange name: " + exchange_name);
			channel.exchangeDeclare(exchange_name, "fanout");
			String message = JSONUtil.serialize(fwdPack);
			logger.debug("message to RabbitMQ: " + message);
			BasicProperties props = new BasicProperties(null, null, null, 0, 0,
					null, Constants.REPLY_TO_TOPIC_PREFIX + topic.getId(),
					null, null, null, null, null, null, null);
			channel.basicPublish(exchange_name, "", null, message.getBytes());
		} catch (IOException e) {
			new PackPackException("TODO", e.getMessage(), e);
		} catch (TimeoutException e) {
			new PackPackException("TODO", e.getMessage(), e);
		} catch (Exception e) {
			throw new PackPackException("", e.getMessage(), e);
		} finally {
			try {
				connectionManager.closeConnection();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void broadcast(BroadcastPack broadcastPack) throws PackPackException {
		FwdPack fwdPack = broadcastPack.getFwdPack();
		BroadcastCriteria criteria = broadcastPack.getCriteria();
		try {
			MsgConnection connection = connectionManager.openConnection();
			Channel channel = connection.getChannel();
			String exchange_name = criteria.getCity() + "_"
					+ criteria.getState() + "_" + criteria.getCountry();
			channel.exchangeDeclare(exchange_name, "fanout");
			String message = JSONUtil.serialize(fwdPack);
			channel.basicPublish(exchange_name, "", null, message.getBytes());
		} catch (IOException e) {
			throw new PackPackException("", e.getMessage(), e);
		} catch (TimeoutException e) {
			throw new PackPackException("", e.getMessage(), e);
		} catch (Exception e) {
			throw new PackPackException("", e.getMessage(), e);
		}
	}*/
}