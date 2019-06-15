package com.pack.pack.rest.api;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.util.RssFeedUtil;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.feed.web.model.TTL;
import com.squill.utils.NotificationUtil;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path("/feeds")
public class FeedUploadResource {
	
	public static final String API_KEY = "f651b01535824fdc8a7f9fb231bdae38";
	
	private static Logger $LOG = LoggerFactory
			.getLogger(FeedUploadResource.class);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus upload(String json) throws PackPackException {
		JStatus status = new JStatus();
		try {
			TTL ttl = new TTL();
			ttl.setTime((short) 1);
			ttl.setUnit(TimeUnit.DAYS);
			JRssFeeds rssFeeds = JSONUtil.deserialize(json, JRssFeeds.class, true);
			HtmlUtil.generateNewsFeedsHtmlPages(rssFeeds);
			RssFeedUtil.uploadNewsFeeds(rssFeeds, ttl, System.currentTimeMillis(), false);
			status.setInfo("Success");
			status.setStatus(StatusType.OK);
		} catch (Exception e) {
			$LOG.error(e.getMessage(), e);
			status.setInfo("Failed");
			status.setStatus(StatusType.ERROR);
		}
		return status;
	}
	
	@PUT
	@Path("update/{updateType}/notify/{notify}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus liveUpdate(@PathParam("updateType") String updateType,
			@PathParam("notify") String notify, String json)
			throws PackPackException {
		JStatus status = new JStatus();
		boolean isNotify = false;
		try {
			if (notify != null && !notify.trim().isEmpty()) {
				isNotify = Boolean.parseBoolean(notify.trim());
			}
		} catch (Exception e1) {
		}
		boolean isLiveUrl = false;
		if (updateType != null && !updateType.trim().isEmpty()) {
			isLiveUrl = "live"
					.equalsIgnoreCase(updateType.trim().toLowerCase());
		}
		try {
			TTL ttl = new TTL();
			ttl.setTime((short) 1);
			ttl.setUnit(TimeUnit.DAYS);
			JRssFeed rssFeed = JSONUtil
					.deserialize(json, JRssFeed.class, false);
			JRssFeeds rssFeeds = new JRssFeeds();
			rssFeeds.getFeeds().add(rssFeed);
			RssFeedUtil.uploadNewsFeeds(rssFeeds, ttl,
					System.currentTimeMillis(), true, false, isLiveUrl);
			if (isNotify) {
				NotificationUtil.broadcastLiveNewsUpdateSummary(
						rssFeed.getOgTitle(), rssFeed.getOgUrl());
			}
			status.setInfo("Success");
			status.setStatus(StatusType.OK);
		} catch (Exception e) {
			$LOG.error(e.getMessage(), e);
			status.setInfo("Failed:: " + e.getMessage());
			status.setStatus(StatusType.ERROR);
		}
		return status;
	}
}
