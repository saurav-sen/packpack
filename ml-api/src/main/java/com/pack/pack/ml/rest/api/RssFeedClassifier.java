package com.pack.pack.ml.rest.api;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.data.upload.FeedUploadUtil;
import com.pack.pack.ml.rest.api.context.ClassificationEngine;
import com.pack.pack.ml.rest.api.context.FeedStatusListener;
import com.pack.pack.model.web.JRssFeeds;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.StatusType;
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
				new FeedStatusListenerImpl());
		JStatus status = new JStatus();
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully Submitted Feeds for batch upload");
		LOG.info("Successfully Submitted Feeds for batch upload");
		return status;
	}
	
	@PUT
	@Path("train")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus updateTrainingData(String json) throws PackPackException {
		LOG.info("Bulk Upload of feeds to be trained");
		JRssFeeds bulk = JSONUtil.deserialize(json, JRssFeeds.class, true);
		ClassificationEngine.INSTANCE.uploadPreClassifiedFeeds(bulk,
				new FeedStatusListenerImpl());
		JStatus status = new JStatus();
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully Uploaded Training data");
		LOG.info("Successfully Uploaded Training data");
		return status;
	}

	private class FeedStatusListenerImpl implements FeedStatusListener {

		@Override
		public void completed(JRssFeeds feeds) {
			JRssFeeds jRssFeeds = FeedUploadUtil.reloadSelectiveFeeds();
			RssFeedUtil.uploadNewFeeds(jRssFeeds);
		}

		@Override
		public void failed(JRssFeeds feeds) {
			// TODO Auto-generated method stub

		}
	}
}