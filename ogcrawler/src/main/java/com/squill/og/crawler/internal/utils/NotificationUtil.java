package com.squill.og.crawler.internal.utils;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.util.json.JSONObject;
import com.pack.pack.services.exception.PackPackException;
import static com.pack.pack.util.SystemPropertyUtil.AUTH_KEY_FCM;
import static com.pack.pack.util.SystemPropertyUtil.API_URL_FCM;

/**
 * 
 * @author Saurav
 *
 */
public class NotificationUtil {

	private static final Logger LOG = LoggerFactory
			.getLogger(NotificationUtil.class);
	
	private static boolean enableNotification = true;

	private NotificationUtil() {
	}
	
	/*public static void main(String[] args) throws PackPackException {
		broadcastNewRSSFeedUploadSummary("Congress-led Bharat bandh fuels Opposition fire against Centre");
	}*/

	public static void broadcastNewRSSFeedUploadSummary(String notification)
			throws PackPackException {
		if(!enableNotification)
			return;
		
		DefaultHttpClient httpClient = null;
		try {
			LOG.debug("broadcastNewRSSFeedUpload");

			httpClient = new DefaultHttpClient();
			HttpPost POST = new HttpPost(API_URL_FCM);
			POST.addHeader("Authorization", "key=" + AUTH_KEY_FCM);
			POST.addHeader("Content-Type", "application/json");

			LOG.trace("Sending notification using firebase service @ "
					+ API_URL_FCM);

			JSONObject jsonObj = new JSONObject();
			// jsonObj.put("to", "/topics/squillWorld");
			jsonObj.put("to", "/topics/global");
			JSONObject j = new JSONObject();
			j.put("body", notification);
			j.put("title", "SQUILL");
			jsonObj.put("notification", j);

			LOG.info("Sending notification \n " + notification);
			LOG.debug(jsonObj.toString());

			POST.setEntity(new StringEntity(jsonObj.toString(),
					ContentType.APPLICATION_JSON));
			httpClient.execute(POST);
		} catch (ClientProtocolException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
