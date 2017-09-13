package com.pack.pack.ml.rest.api;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.data.upload.FeedUploadUtil;
import com.pack.pack.ml.rest.api.context.ClassificationEngine;
import com.pack.pack.ml.rest.api.context.FeedStatusListener;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.JRssFeeds;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.model.web.TTL;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.util.RssFeedUtil;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path("/feeds")
public class RssFeedClassifier {
	
	private static final Logger LOG = LoggerFactory.getLogger(RssFeedClassifier.class);
	
	/**
	 * 
	 * Should be protected by API key
	 * 
	 * @param bulk
	 * @return
	 * @throws PackPackException
	 */
	@PUT
	@Path("classify")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus bulkUpload(String json) throws PackPackException {
		LOG.info("Bulk Upload of feeds");
		JRssFeeds bulk = JSONUtil.deserialize(json, JRssFeeds.class);
		LOG.info("Submitting feeds to ClassificationEngine");
		ClassificationEngine.INSTANCE.submitFeeds(bulk,
				new FeedStatusListenerImpl(false));
		JStatus status = new JStatus();
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully Submitted Feeds for batch upload");
		LOG.info("Successfully Submitted Feeds for batch upload");
		return status;
	}
	
	@PUT
	@Path("train/notify")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus updateTrainingData_2(String json) throws PackPackException {
		return updateTrainingData(json, true);
	}
	
	@PUT
	@Path("train")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus updateTrainingData_1(String json) throws PackPackException {
		return updateTrainingData(json, false);
	}
	
	private JStatus updateTrainingData(String json, boolean sendNotification) throws PackPackException {
		LOG.info("Bulk Upload of feeds to be trained");
		JRssFeeds bulk = JSONUtil.deserialize(json, JRssFeeds.class);
		List<JRssFeed> feeds = bulk.getFeeds();
		if(feeds == null || feeds.isEmpty()) {
			JStatus status = new JStatus();
			status.setStatus(StatusType.ERROR);
			status.setInfo("Failed to Upload Training data");
			LOG.info("JSON data found to be empty");
			return status;
		}
		for(JRssFeed feed : feeds) {
			feed.setId(String.valueOf(System.nanoTime()));
			if(feed.getCreatedBy() == null || feed.getCreatedBy().trim().isEmpty()) {
				feed.setCreatedBy("SQUILL Team");
			}
		}
		ClassificationEngine.INSTANCE.uploadPreClassifiedFeeds(bulk,
				new FeedStatusListenerImpl(sendNotification));
		JStatus status = new JStatus();
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully Uploaded Training data");
		LOG.info("Successfully Uploaded Training data");
		return status;
	}
	
	@PUT
	@Path("upload/{email}/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus uploadFeedd(@PathParam("email") String email,
			@PathParam("name") String name, String json)
			throws PackPackException {
		LOG.info("Bulk Upload of feeds to be trained");
		JRssFeed feed = JSONUtil.deserialize(json, JRssFeed.class);
		JRssFeeds nonBulk = new JRssFeeds();
		nonBulk.getFeeds().add(feed);
		ClassificationEngine.INSTANCE.uploadPreClassifiedFeeds(nonBulk,
				new FeedStatusListenerImpl(false));
		JStatus status = new JStatus();
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully Uploaded Training data");
		LOG.info("Successfully Uploaded Training data");
		return status;
	}

	private class FeedStatusListenerImpl implements FeedStatusListener {
		
		private boolean sendNotification;
		
		FeedStatusListenerImpl(boolean sendNotification) {
			this.sendNotification = sendNotification;
		}

		@Override
		public void completed(JRssFeeds feeds) {
			JRssFeeds jRssFeeds = FeedUploadUtil.reloadSelectiveFeeds();
			TTL ttl = new TTL();
			ttl.setTime((short)2);
			ttl.setUnit(TimeUnit.DAYS);
			RssFeedUtil.uploadNewFeeds(jRssFeeds, ttl, sendNotification);
		}

		@Override
		public void failed(JRssFeeds feeds) {
			// TODO Auto-generated method stub

		}
	}
}