package com.pack.pack.rest.api;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.model.web.dto.OpinionDTO;
import com.pack.pack.rest.api.security.interceptors.CompressWrite;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.ext.HttpRequestExecutor;
import com.pack.pack.services.redis.INewsFeedService;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.RssFeedUtil;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.feed.web.model.TTL;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path("/opinions")
public class Opinions {
	
	private static Logger $LOG = LoggerFactory.getLogger(Opinions.class);
	
	@GET
	@CompressWrite
	@Produces(MediaType.APPLICATION_JSON)
	public JRssFeeds getAllOptionsFeeds() throws PackPackException {
		JRssFeeds result = new JRssFeeds();
		INewsFeedService service = ServiceRegistry.INSTANCE
				.findCompositeService(INewsFeedService.class);
		List<JRssFeed> feeds = service.getAllOpinionsFeeds();
		if (feeds != null && !feeds.isEmpty()) {
			result.getFeeds().addAll(feeds);
		}
		return result;
	}
	
	@GET
	@CompressWrite
	@Path("usr/{userId}/page/{pageLink}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JRssFeed> getOptionsFeeds(@PathParam("userId") String userId,
			@PathParam("pageLink") String pageLink)
			throws PackPackException {
		int pageNo = -1;
		if(pageLink != null && !pageLink.trim().isEmpty()) {
			try {
				pageNo = Integer.parseInt(pageLink.trim());
			} catch (NumberFormatException e) {
				$LOG.error(e.getMessage(), e);
			}
		}
		INewsFeedService service = ServiceRegistry.INSTANCE
				.findCompositeService(INewsFeedService.class);
		Pagination<JRssFeed> page = service.getAllOpinionRssFeeds(userId, pageNo);
		if(page == null) {
			return emptyResponse();
		}
		return page;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus update(String json) throws PackPackException {
		JStatus status = new JStatus();
		try {
			OpinionDTO dto = JSONUtil.deserialize(json, OpinionDTO.class);
			String url = dto.getUrl();
			JRssFeed feed = new JRssFeed();
			feed.setOgUrl(url);
			feed.setHrefSource(url);
			String feedType = resolveFeedType(dto.getType(), url).name();
			feed.setFeedType(feedType);
			String title = dto.getTitle();
			String content = dto.getContent();
			JRssFeed f0 = null;
			if(url != null && !url.trim().isEmpty() && (title == null || title.trim().isEmpty())) {
				String htmlContent = new HttpRequestExecutor().GET(url);
				f0 = HtmlUtil.parse4mHtml(htmlContent);
				feed.setOgTitle(f0.getOgTitle());
			}
			if(url != null && !url.trim().isEmpty() && (content == null || content.trim().isEmpty())) {
				if(f0 == null) {
					String htmlContent = new HttpRequestExecutor().GET(url);
					f0 = HtmlUtil.parse4mHtml(htmlContent);
				}
				feed.setArticleSummaryText(f0.getOgDescription());
				feed.setOgDescription("");
			}
			String imageUrl = dto.getImageUrl();
			if(imageUrl != null && !imageUrl.trim().isEmpty()) {
				feed.setOgImage(imageUrl);
			} else if(!dto.isNoImage() && url != null && !url.trim().isEmpty()) {
				if(f0 == null) {
					String htmlContent = new HttpRequestExecutor().GET(url);
					f0 = HtmlUtil.parse4mHtml(htmlContent);
				}
				feed.setOgImage(f0.getOgImage());
			}
			feed.setCreatedBy(dto.getAuthor());
			long batchId = System.currentTimeMillis();
			feed.setUploadTime(batchId);
			TTL ttl = new TTL();
			if(feed.getFeedType().equalsIgnoreCase(JRssFeedType.OPINION.name())) {
				ttl.setTime((short) 7);
				ttl.setUnit(TimeUnit.DAYS);
				RssFeedUtil.uploadOpinionFeed(feed, ttl, batchId, true);
			} else if(feed.getFeedType().equalsIgnoreCase(JRssFeedType.REFRESHMENT.name())) {
				ttl.setTime((short) 2);
				ttl.setUnit(TimeUnit.DAYS);
				JRssFeeds feeds = new JRssFeeds();
				feeds.getFeeds().add(feed);
				RssFeedUtil.uploadRefreshmentFeeds(feeds, ttl, batchId, true);
			} else {
				ttl.setTime((short) 1);
				ttl.setUnit(TimeUnit.DAYS);
				JRssFeeds feeds = new JRssFeeds();
				feeds.getFeeds().add(feed);
				RssFeedUtil.uploadNewsFeeds(feeds, ttl, batchId, true);
			}
			status.setInfo("Successfully uploaded opinion");
			status.setStatus(StatusType.OK);
		} catch (Exception e) {
			status.setInfo("Failed to upload opinion (Internal Server Error)");
			status.setStatus(StatusType.ERROR);
			$LOG.error(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		}
		return status;
	}
	
	private boolean isRefreshmentLink(String link) {
		try {
			URL url = new URL(link);
			String host = url.getHost();
			if ("youtu.be".equalsIgnoreCase(host)
					|| "youtube.com".equalsIgnoreCase(host)
					|| "www.youtu.be".equalsIgnoreCase(host)
					|| "www.youtube.com".equalsIgnoreCase(host)) {
				return true;
			}
		} catch (Exception e) {
			$LOG.error(e.getMessage(), e);
		}
		return false;
	}
	
	private JRssFeedType resolveFeedType(String type, String url) {
		boolean isRefreshmentLink = isRefreshmentLink(url);
		if(isRefreshmentLink) {
			return JRssFeedType.REFRESHMENT;
		}
		if (type == null || type.trim().isEmpty()) {
			return JRssFeedType.OPINION;
		}
		JRssFeedType feedType = null;
		type = type.trim();
		if(type.equalsIgnoreCase("OPINION")) {
			feedType = JRssFeedType.OPINION;
		} else if (type.equalsIgnoreCase("SCIENCE")
				|| type.equalsIgnoreCase("TECHNOLOGY")
				|| type.equalsIgnoreCase("SCIENCE AND TECHNOLOGY")
				|| type.equalsIgnoreCase("SCIENCE & TECHNOLOGY")) {
			feedType = JRssFeedType.NEWS_SCIENCE_TECHNOLOGY;
		} else if (type.equalsIgnoreCase("SPORTS")) {
			feedType = JRssFeedType.NEWS_SPORTS;
		} else if (type.equalsIgnoreCase("ARTICLE")) {
			feedType = JRssFeedType.ARTICLE;
		} else if (type.equalsIgnoreCase("NEWS")
				|| type.equalsIgnoreCase("POLITICS")
				|| type.equalsIgnoreCase("CRIME")
				|| type.equalsIgnoreCase("SOCIAL")
				|| type.equalsIgnoreCase("WEATHER")
				|| type.equalsIgnoreCase("DISTASTER")) {
			feedType = JRssFeedType.NEWS;
		} else if(isRefreshmentLink) {
			feedType = JRssFeedType.REFRESHMENT;
		} else {
			feedType = JRssFeedType.OPINION;
		}
		if(feedType == JRssFeedType.REFRESHMENT && !isRefreshmentLink) {
			feedType = JRssFeedType.NEWS;
		}
		if(feedType != JRssFeedType.REFRESHMENT && isRefreshmentLink) {
			feedType = JRssFeedType.REFRESHMENT;
		}
		return feedType;
	}
	
	private Pagination<JRssFeed> emptyResponse() {
		Pagination<JRssFeed> page = new Pagination<JRssFeed>();
		page.setNextPageNo(-1);
		page.setResult(Collections.emptyList());
		return page;
	}
}
