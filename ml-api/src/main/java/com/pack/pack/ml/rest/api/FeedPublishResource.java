package com.pack.pack.ml.rest.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.model.web.dto.FeedPublish;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.ext.article.comparator.ArticleInfo;
import com.pack.pack.services.ext.article.comparator.TitleBasedArticleComparator;
import com.pack.pack.services.redis.INewsFeedService;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.RssFeedUtil;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;
import com.squill.feed.web.model.JRssFeedUploadRequest;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.feed.web.model.TTL;
import com.squill.feed.web.model.UploadType;
import com.squill.utils.ArchiveUtil;
import com.squill.utils.HtmlUtil;
import com.squill.utils.NotificationUtil;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path("/publish")
public class FeedPublishResource {

	private static final Logger $_LOG = LoggerFactory
			.getLogger(FeedPublishResource.class);

	@GET
	@Path("recent")
	@Produces(MediaType.APPLICATION_JSON)
	public JRssFeeds getRecentAutoUploadFeeds() throws PackPackException {
		INewsFeedService service = ServiceRegistry.INSTANCE
				.findCompositeService(INewsFeedService.class);
		return service.getRecentAutoUploadFeeds();
	}

	@GET
	@Path("unprovision/page/{pageLink}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JRssFeed> getUnpublishedUploadFeeds(
			@PathParam("pageLink") String pageLink) throws PackPackException {
		INewsFeedService service = ServiceRegistry.INSTANCE
				.findCompositeService(INewsFeedService.class);
		int pageNo = -1;
		if (pageLink != null && !pageLink.trim().isEmpty()) {
			try {
				pageNo = Integer.parseInt(pageLink.trim());
			} catch (NumberFormatException e) {
				$_LOG.error(e.getMessage(), e);
			}
		}
		return service.getUnprovisionUploadFeeds(pageNo);
	}

	@POST
	@Path("type/{type}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus publish(@PathParam("type") String type, String json)
			throws PackPackException {
		int typeInt = Integer.parseInt(type.trim());
		if (typeInt == 0) {
			return processPublishResource_auto(json);
		} else if (typeInt == 1) {
			return processPublishResource_manual(json);
		}
		throw new PackPackException(ErrorCodes.PACK_ERR_71, "Invalid Type = "
				+ type);
	}

	@DELETE
	@Path("id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus delete(@PathParam("id") String id) throws PackPackException {
		$_LOG.info("******************************************* START DELETE *************************************************");
		JStatus status = new JStatus();
		INewsFeedService service = ServiceRegistry.INSTANCE
				.findCompositeService(INewsFeedService.class);
		JRssFeed deleted = service.delete(id);
		if (deleted != null) {
			status.setStatus(StatusType.OK);
			status.setInfo("Successfully deleted feed with id = " + id);
			$_LOG.info(status.getStatus().name() + "::" + status.getInfo());
			$_LOG.info(StringEscapeUtils.unescapeJava(JSONUtil
					.serialize(deleted)));
		} else {
			status.setStatus(StatusType.ERROR);
			status.setInfo("Failed to delete feed with id = " + id);
			$_LOG.info(status.getStatus().name() + "::" + status.getInfo());
		}
		$_LOG.info("******************************************* END DELETE *************************************************");
		return status;
	}

	private JStatus processPublishResource_auto(String json)
			throws PackPackException {
		JStatus status = new JStatus();
		FeedPublish feedPublish = JSONUtil.deserialize(json, FeedPublish.class,
				true);
		INewsFeedService service = ServiceRegistry.INSTANCE
				.findCompositeService(INewsFeedService.class);
		boolean success = service.upload(feedPublish);
		if (success) {
			status.setInfo("Successfully Published");
			status.setStatus(StatusType.OK);
			$_LOG.info("********************************************************************************************");
			$_LOG.info(status.getInfo());
			$_LOG.info(StringEscapeUtils.unescapeJava(JSONUtil
					.serialize(feedPublish)));
			$_LOG.info("********************************************************************************************");
			if (feedPublish.isNotify()) {
				NotificationUtil.broadcastNewRSSFeedUploadSummary(feedPublish
						.getTitleText());
			}
		} else {
			status.setInfo("Failed To Publish");
			status.setStatus(StatusType.ERROR);
			$_LOG.info("********************************************************************************************");
			$_LOG.info(status.getInfo());
			$_LOG.info(StringEscapeUtils.unescapeJava(JSONUtil
					.serialize(feedPublish)));
			$_LOG.info("********************************************************************************************");
		}
		return status;
	}

	private JStatus processPublishResource_manual(String json)
			throws PackPackException {
		JStatus status = new JStatus();
		try {
			TTL ttl = new TTL();
			ttl.setTime((short) 1);
			ttl.setUnit(TimeUnit.DAYS);
			JRssFeedUploadRequest uploadRequest = JSONUtil.deserialize(json,
					JRssFeedUploadRequest.class, true);
			JRssFeed content = uploadRequest.getContent();
			content.setUploadType(UploadType.MANUAL.name());
			content.setUploadTime(System.currentTimeMillis());
			content.setOpenDirectLink(uploadRequest.isOpenDirectLink());
			String feedType = uploadRequest.getFeedType();
			if (feedType == null || feedType.trim().isEmpty()) {
				content.setFeedType(JRssFeedType.NEWS.name());
			} else {
				try {
					JRssFeedType valueOf = JRssFeedType.valueOf(feedType
							.toUpperCase());
					content.setFeedType(valueOf.name());
				} catch (Exception e) {
					content.setFeedType(JRssFeedType.NEWS.name());
					$_LOG.error("Type could not be resolved, fallback to NEWS",
							e.getMessage(), e);
				}
			}

			if (uploadRequest.isCheckDuplicate()) {
				List<ArticleInfo> tgtList = new ArrayList<ArticleInfo>();
				List<JRssFeed> list = ArchiveUtil.getFeedsUploadedFromArchive(
						ArchiveUtil.DEFAULT_ID,
						ArchiveUtil.DEFAULT_MAX_TIME_DIFF_IN_HOURS,
						UploadType.MANUAL);
				if (list != null && !list.isEmpty()) {
					for (JRssFeed l : list) {
						ArticleInfo tgt = new ArticleInfo(l.getOgTitle(), null);
						tgt.setReferenceObject(l);
						tgtList.add(tgt);
					}
				}
				TitleBasedArticleComparator comparator = new TitleBasedArticleComparator();
				if (JRssFeedType.NEWS.name().equals(content.getFeedType())) {
					ArticleInfo src = new ArticleInfo(content.getOgTitle(),
							null);
					src.setReferenceObject(content);
					List<ArticleInfo> probableDuplicates = comparator
							.checkProbableDuplicates(src, tgtList);

					if (probableDuplicates != null
							&& !probableDuplicates.isEmpty()) {
						StringBuilder replyContent = new StringBuilder();
						replyContent.append("Thank you for uploading.");
						replyContent
								.append("But, SQUILL feels Sorry!!! for not being able to upload your content i.e. ");
						replyContent.append(content.getOgTitle());
						replyContent.append(" @ ");
						replyContent.append(content.getOgUrl());
						replyContent.append(".");
						replyContent
								.append(" Possibly because it matched with one of the following pre-uploaded content");
						for (ArticleInfo probableDuplicate : probableDuplicates) {
							Object referenceObject = probableDuplicate
									.getReferenceObject();
							if (referenceObject == null
									|| !(referenceObject instanceof JRssFeed))
								continue;
							JRssFeed referenceFeed = (JRssFeed) referenceObject;
							replyContent.append(" ");
							replyContent.append(referenceFeed.getOgTitle());
							replyContent.append(" @ ");
							replyContent.append(referenceFeed.getOgUrl());
							replyContent.append("   ");
						}
						replyContent.append(". ");
						replyContent
								.append("SQUILL hereby does apologize for the inconvenience caused to you.");
						status.setInfo(replyContent.toString());
						status.setStatus(StatusType.ERROR);

						$_LOG.info("********************************************************************************************");
						$_LOG.info(status.getInfo());
						$_LOG.info("********************************************************************************************");

						return status;
					}
				}
			}

			Map<String, List<JRssFeed>> map = new HashMap<String, List<JRssFeed>>();
			List<JRssFeed> feeds = new LinkedList<JRssFeed>();
			feeds.add(content);
			map.put(ArchiveUtil.DEFAULT_ID, feeds);
			ArchiveUtil.storeInArchive(map);
			JRssFeeds rssFeeds = new JRssFeeds();
			rssFeeds.getFeeds().add(content);
			List<String> ids = new ArrayList<String>();
			ids.add(content.getId());
			HtmlUtil.generateNewsFeedsHtmlPages(rssFeeds);
			RssFeedUtil.uploadNewsFeeds(rssFeeds, ttl,
					System.currentTimeMillis(), true);
			RssFeedUtil.markAsProvisionedByFeedIds(ids, JRssFeedType.valueOf(content.getFeedType().toUpperCase()));
			if (uploadRequest.isNotify()) {
				NotificationUtil.broadcastNewRSSFeedUploadSummary(content
						.getOgTitle());
			}
			status.setInfo("Thank you for uploading. Successfully uploaded.");
			status.setStatus(StatusType.OK);
		} catch (Exception e) {
			$_LOG.error(e.getMessage(), e);
			status.setInfo("Failed");
			status.setStatus(StatusType.ERROR);
		}

		$_LOG.info("********************************************************************************************");
		$_LOG.info(status.getInfo());
		$_LOG.info("********************************************************************************************");

		return status;
	}
}
