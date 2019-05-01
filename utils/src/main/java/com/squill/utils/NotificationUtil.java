package com.squill.utils;

import static com.pack.pack.util.SystemPropertyUtil.API_URL_FCM;
import static com.pack.pack.util.SystemPropertyUtil.AUTH_KEY_FCM;

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
import com.squill.feed.web.model.JRssFeed;

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
		broadcastNewRSSFeedUploadSummary(
				"Lok Sabha elections 2019: 0% voting in Anantnag: ‘Give anger a voice,’ appeals Omar Abdullah", 
				"The voting percentage figures released by the Election Commission showed 0% voting in Anantnag at 9 am as voters stayed indoors and "
				+ "its 1,842 polling booths wore a deserted look.. Considered a PDP bastion, Mehbooba Mufti had won from here in 2014 defeating NC "
				+ "candidate Mehboob Beg by more than 65,000 votes.. When Mehbooba became J K chief minister in 2016 after her father's demise, "
				+ "she got elected from her father's assembly seat and vacated her Lok Sabha seat with an intention to field her younger brother "
				+ "Tasaduq Mufti from there..", 
				"https://www.msn.com/en-in/news/jammu-and-kashmir/lok-sabha-elections-2019%E2%80%890percent-voting-in-anantnag-give-anger-a-voice-appeals-omar-abdullah/ar-BBWco6B?li=AAggbRN",
				"http://img-s-msn-com.akamaized.net/tenant/amp/entityid/BBWcjX3.img?h=278&w=300&m=6&q=60&o=f&l=f&x=1660&y=882",
				"http://squill.in/sh/bGKHvx");
	}*/
	
	public static void broadcastNewRSSFeedUploadSummary(JRssFeed feed)
			throws PackPackException {
		broadcastNewRSSFeedUploadSummary(feed.getOgTitle(),
				feed.getArticleSummaryText(), feed.getOgUrl(),
				feed.getOgImage(), feed.getShareableUrl());
	}
	
	private static void broadcastNewRSSFeedUploadSummary(String ogTitle, String summary, String ogUrl, String ogImage, String shareableUrl)
			throws PackPackException {
		if(!enableNotification)
			return;
		
		if (ogTitle == null || summary == null || ogUrl == null) {
			LOG.debug("Skipping notification data msg send as NULL check/validation of mandatory attributes failed");
			return;
		}

		if (ogTitle.trim().isEmpty() || summary.trim().isEmpty()
				|| ogUrl.trim().isEmpty()) {
			LOG.debug("Skipping notification data msg send as EMPTY text check/validation of mandatory attributes failed");
			return;
		}

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
			//jsonObj.put("to", "/topics/allDevices");
			jsonObj.put("to", "/topics/global");
			
			JSONObject k = new JSONObject();
			k.put("ogTitle", ogTitle);
			k.put("ogImage", ogImage);
			k.put("ogUrl", ogUrl);
			k.put("summary", summary);
			k.put("shareableUrl", shareableUrl);
			jsonObj.put("data", k);

			LOG.info("Sending notification \n " + ogTitle);
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

	public static void broadcastNewRSSFeedUploadSummary_notification(String notification)
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
