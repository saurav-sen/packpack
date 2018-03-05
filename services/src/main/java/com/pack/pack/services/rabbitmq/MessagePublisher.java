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
import com.pack.pack.model.RssFeedType;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.notification.FeedMsgType;
import com.pack.pack.services.exception.PackPackException;

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
	
	private static Logger LOG = LoggerFactory.getLogger(MessagePublisher.class);
	
	public final static String AUTH_KEY_FCM = "AAAApQZn_ZI:APA91bGidkJYWfz2JYHTPXWr5a0NrLwV6K2DE-z57eIoLpBmUgqaUQ239pGVbDA8Aw_KKZqBFfsxLYv3wp2bjH1XXN-uGeRuAQNpN7LrAsfWJBikVAedOzs0GvzNzPHK2eWdSKVmLXod";
	public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
	
	public static void main(String[] args) throws Exception {
		JRssFeed feed = new JRssFeed();
		feed.setOgTitle("\"Test Java 123\"");
		System.out.println(JSONUtil.serialize(feed, false));
		feed = JSONUtil.deserialize(JSONUtil.serialize(feed, false), JRssFeed.class);
		System.out.println(feed.getOgTitle());
		new MessagePublisher().broadcastNewRSSFeedUploadSummary("Test Java 123");
		
	}
	
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
	
	public void broadcastNewRSSFeedUploadSummary(String notification) throws PackPackException {
		CloseableHttpClient httpClient = null;
		try {
			LOG.debug("broadcastNewRSSFeedUpload");
			
			httpClient = HttpClientBuilder.create().build();
			HttpPost POST = new HttpPost(API_URL_FCM);
			POST.addHeader("Authorization", "key=" + AUTH_KEY_FCM);
			POST.addHeader("Content-Type", "application/json");
			
			LOG.trace("Sending notification using firebase service @ " + API_URL_FCM);
			
			JSONObject jsonObj = new JSONObject();
			//jsonObj.put("to", "/topics/squillWorld");
			jsonObj.put("to", "/topics/allDevices");
			JSONObject j = new JSONObject();
			j.put("title", notification);
			jsonObj.put("notification", j);
			
			
			LOG.info("Sending notification \n " + notification);
			LOG.debug(jsonObj.toString());
			
			POST.setEntity(new StringEntity(jsonObj.toString(), ContentType.APPLICATION_JSON));
			httpClient.execute(POST);
		} catch (ClientProtocolException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if(httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	private FeedMsgType resolveMsgType(JRssFeed feed) {
		if(RssFeedType.REFRESHMENT.equals(feed.getFeedType())) {
			return FeedMsgType.SQUILL_TEAM;
		} else if(RssFeedType.NEWS.equals(feed.getFeedType())) {
			return FeedMsgType.NEWS;
		}
		return FeedMsgType.UNKNOWN;
	}
}