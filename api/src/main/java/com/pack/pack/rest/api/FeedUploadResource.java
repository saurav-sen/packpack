package com.pack.pack.rest.api;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
	
	public static void main(String[] args) throws PackPackException {
		JRssFeeds c = new JRssFeeds();
		JRssFeed f = new JRssFeed();
		f.setArticleSummaryText("abc");
		c.getFeeds().add(f);
		String json = JSONUtil.serialize(c);
		c = JSONUtil.deserialize(json, JRssFeeds.class, true);
		f = c.getFeeds().get(0);
		System.out.println(f.getArticleSummaryText());
	}

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
			RssFeedUtil.uploadNewsFeeds(rssFeeds, ttl, System.currentTimeMillis(), true);
			status.setInfo("Success");
			status.setStatus(StatusType.OK);
		} catch (Exception e) {
			$LOG.error(e.getMessage(), e);
			status.setInfo("Failed");
			status.setStatus(StatusType.ERROR);
		}
		return status;
	}
}
