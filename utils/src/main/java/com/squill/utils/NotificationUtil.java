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
	
	public static void main(String[] args) throws PackPackException {
		broadcastLiveNewsUpdateSummary("NDA 263 UPA 147", "http://squill.in/api/electionResult?code=IN");
	}
	
	public static void broadcastLiveNewsUpdateSummary(String notificationMsg,
			String url) throws PackPackException {
		broadcastNewRSSFeedUploadSummary(notificationMsg, null, url, null, url);
	}
	
	public static void main1(String[] args) throws PackPackException {
		broadcastNewRSSFeedUploadSummary(
				"'Many surgical strikes during UPA's tenure', says former Prime Minister Manmohan Singh", 
				"Truth is that Modiji promised 20 million jobs per year, but his disruptive policies of demonetisation and a flawed "
				+ "GST have snatched over 40 million jobs from the youth.. Time has come to pass the leadership mantle to the young.. "
				+ "If you have to draw comparisons, it should be founded upon your service to the nation, your commitment to alleviate poverty, "
				+ "your determination to eradicate agrarian distress, your capacity towards job creation for the young, and your adherence to the "
				+ "economic wellbeing of India..", 
				"https://m.hindustantimes.com/india-news/many-surgical-strikes-during-upa-s-tenure-says-former-prime-minister-manmohan-singh/story-fQXIFvZ0i8iQRD8By5iKUN_amp.html",
				"https://www.hindustantimes.com/rf/image_size_444x250/HT/p2/2019/05/01/Pictures/manmohan-book_07701e9a-6c2d-11e9-be3c-d387070551cb.jpg",
				"http://squill.in/sh/bU44jd");
		
		broadcastNewRSSFeedUploadSummary(
				"Cyclone Fani LIVE: Close to 1 Lakh Evacuated, Alert Issued For All Airports Along East Coast", 
				"TDetailed timings are being worked out.. Advisories asking fishermen not to venture into the sea have also been issued."
				+ "The Centre has released Rs 1,086 crore for four states as advance financial assistance to undertake preventive and relief measures."
				+ "In West Bengal, the cyclone is expected to affect the districts of East and West Medinipur, South and North 24 Parganas, Howrah, Hooghly, "
				+ "Jhargram and Kolkata, while Srikakulam, Vijayanagram and Visakhapatnam are in the storm's path in Andhra Pradesh.Meanwhile, "
				+ "Andhra Pradesh Chief Minister N Chandrababu Naidu wrote to the Election Commission asking that the Model Code of Conduct be relaxed "
				+ "in East Godavari, Visakhapatnam, Vizianagaram and Srikakulam.State-owned Oil and Natural Gas Corp (ONGC) has evacuated 480 of its employees "
				+ "from rigs operating in the Bay of Bengal.. The company has as many as six rigs exploring and drilling for oil and gas off the Andhra coast and "
				+ "personnel on five of them have been evacuated to safer zones..", 
				"https://www.news18.com/amp/news/india/cyclone-fani-live-extremely-severe-storm-450-km-from-odisha-coast-8-lakh-to-be-evacuated-by-evening-2124877.html",
				"https://images.news18.com/ibnlive/uploads/2019/05/Puri.jpg",
				"http://squill.in/sh/3Ci1k");
	}
	
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
		
		if (ogTitle == null || /*summary == null ||*/ ogUrl == null) {
			LOG.debug("Skipping notification data msg send as NULL check/validation of mandatory attributes failed");
			return;
		}

		if (ogTitle.trim().isEmpty() /*|| summary.trim().isEmpty()*/
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
			//jsonObj.put("to", "/topics/squillWorld");
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
