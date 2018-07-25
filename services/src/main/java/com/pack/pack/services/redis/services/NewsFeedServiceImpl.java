package com.pack.pack.services.redis.services;

import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE;
import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE_TIMESTAMP;
import static com.pack.pack.common.util.CommonConstants.NULL_PAGE_LINK;
import static com.pack.pack.common.util.CommonConstants.PAGELINK_DIRECTION_NEGATIVE;
import static com.pack.pack.common.util.CommonConstants.PAGELINK_DIRECTION_POSITIVE;
import static com.pack.pack.common.util.CommonConstants.PAGELINK_DIRECTION_SEPERATOR;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.RSSFeed;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.ShortenUrlInfo;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.redis.INewsFeedService;
import com.pack.pack.services.redis.RssFeedRepositoryService;
import com.pack.pack.services.redis.UrlShortener;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.ModelConverter;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;
import com.squill.feed.web.model.TTL;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class NewsFeedServiceImpl implements INewsFeedService {

	private static final Logger $_LOG = LoggerFactory
			.getLogger(NewsFeedServiceImpl.class);

	private static final int MINIMUM_PAGE_SIZE = 10;

	@Override
	public Pagination<JRssFeed> getAllNewsRssFeeds(String userId,
			String pageLink) throws PackPackException {
		return getAllRssFeeds(JRssFeedType.NEWS, userId, pageLink);
	}

	@Override
	public Pagination<JRssFeed> getAllSportsNewsRssFeeds(String userId,
			String pageLink) throws PackPackException {
		return getAllRssFeeds(JRssFeedType.NEWS_SPORTS, userId, pageLink);
	}

	@Override
	public Pagination<JRssFeed> getAllScienceAndTechnologyNewsRssFeeds(
			String userId, String pageLink) throws PackPackException {
		return getAllRssFeeds(JRssFeedType.NEWS_SCIENCE_TECHNOLOGY, userId,
				pageLink);
	}

	@Override
	public Pagination<JRssFeed> getArticleNewsRssFeeds(String userId,
			String pageLink) throws PackPackException {
		return getAllRssFeeds(JRssFeedType.ARTICLE, userId, pageLink);
	}

	private Pagination<JRssFeed> getAllRssFeeds(JRssFeedType type,
			String userId, String pageLink) throws PackPackException {
		List<JRssFeed> result = new ArrayList<JRssFeed>();
		$_LOG.info("Reading from nextLink = " + pageLink);
		Pagination<JRssFeed> page = getAllRssFeeds0(type, userId, pageLink);
		String[] split = pageLink.split(PAGELINK_DIRECTION_SEPERATOR);
		int direction = 1;
		if (split.length > 1) {
			direction = Integer.parseInt(split[1].trim());
		}
		$_LOG.info("Direction = " + direction);
		boolean needToIterate = false;
		String previousLink = page.getPreviousLink();
		String nextLink = page.getNextLink();
		List<JRssFeed> feeds = page.getResult();
		result.addAll(feeds);
		String link = direction > 0 ? nextLink : previousLink;
		while (result != null && link != null && !result.isEmpty()
				&& result.size() < MINIMUM_PAGE_SIZE
				&& !link.startsWith(END_OF_PAGE)
				&& !link.startsWith(NULL_PAGE_LINK)) {
			$_LOG.info("Reading from nextLink = " + link);
			Pagination<JRssFeed> page0 = getAllRssFeeds0(type, userId, link);
			feeds = page0.getResult();
			link = direction > 0 ? page0.getNextLink() : page0
					.getPreviousLink();
			result.addAll(feeds);
			needToIterate = true;
		}
		if (needToIterate) {
			$_LOG.debug("Needed to iterate over multiple pages due to MINIMUM_PAGE_SIZE");
			if (direction > 0) {
				nextLink = link;
			} else {
				previousLink = link;
			}
			page.setPreviousLink(previousLink);
			page.setResult(result);
			page.setNextLink(nextLink);
		}
		return page;
	}

	private Pagination<JRssFeed> getAllRssFeeds0(JRssFeedType type,
			String userId, String pageLink) throws PackPackException {
		if (END_OF_PAGE.equals(pageLink.trim())
				|| (END_OF_PAGE + PAGELINK_DIRECTION_POSITIVE).equals(pageLink
						.trim())
				|| (END_OF_PAGE + PAGELINK_DIRECTION_NEGATIVE).equals(pageLink
						.trim())) {
			return endOfPageResponse();
		}
		int direction = 1;
		long timestamp = 0;
		if (pageLink != null
				&& !NULL_PAGE_LINK.equals(pageLink)
				&& !(NULL_PAGE_LINK + PAGELINK_DIRECTION_POSITIVE)
						.equals(pageLink)
				&& !(NULL_PAGE_LINK + PAGELINK_DIRECTION_NEGATIVE)
						.equals(pageLink)) {
			String[] split = pageLink.split(PAGELINK_DIRECTION_SEPERATOR);
			timestamp = Long.parseLong(split[0]);
			if (split.length > 1) {
				direction = Integer.parseInt(split[1]);
			}
		}
		Pagination<RSSFeed> page = null;
		/*
		 * List<RSSFeed> feeds = Collections.emptyList(); List<JRssFeed> rows =
		 * ModelConverter.convertAllRssFeeds(feeds, true, true);
		 */
		RssFeedRepositoryService repositoryService = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		switch (type) {
		case NEWS:
			page = repositoryService.getNewsFeeds(timestamp, direction);
			break;
		case NEWS_SPORTS:
			page = repositoryService.getSportsNewsFeeds(
					timestamp, direction);
			break;
		case NEWS_SCIENCE_TECHNOLOGY:
			page = repositoryService.getScienceAndTechnologyNewsFeeds(
					timestamp, direction);
			break;
		case ARTICLE:
			page = repositoryService.getArticleNewsFeeds(timestamp, direction);
			break;
		default:
			break;
		}

		if (page == null) {
			return endOfPageResponse();
		}

		Pagination<JRssFeed> pageResult = new Pagination<JRssFeed>(page.getTimestamp());
		pageResult.setNextLink(page.getNextLink());
		pageResult.setPreviousLink(page.getPreviousLink());
		pageResult.setResult(ModelConverter.convertAllRssFeeds(
				page.getResult(), true, true));
		/*
		 * Collections.sort(rows, new RssFeedComparator(userId));
		 * pageResult.setResult(rows);
		 */
		return pageResult;
	}

	private Pagination<JRssFeed> endOfPageResponse() {
		Pagination<JRssFeed> page = new Pagination<JRssFeed>(END_OF_PAGE_TIMESTAMP);
		page.setNextLink(END_OF_PAGE + PAGELINK_DIRECTION_POSITIVE);
		page.setPreviousLink(END_OF_PAGE + PAGELINK_DIRECTION_NEGATIVE);
		page.setResult(Collections.emptyList());
		return page;
	}

	@Override
	public boolean upload(List<JRssFeed> feeds, TTL ttl, long batchId)
			throws PackPackException {
		if (feeds == null || feeds.isEmpty())
			return false;
		try {
			RssFeedRepositoryService service = ServiceRegistry.INSTANCE
					.findService(RssFeedRepositoryService.class);
			for (JRssFeed feed : feeds) {
				upload(service, feed, ttl, batchId);
			}
			return true;
		} catch (NoSuchAlgorithmException e) {
			$_LOG.error(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61, e.getMessage());
		}
	}

	private boolean upload(RssFeedRepositoryService service, JRssFeed feed,
			TTL ttl, long batchId) throws PackPackException,
			NoSuchAlgorithmException {
		boolean checkFeedExists = service.checkFeedExists(feed);
		if (!checkFeedExists) {
			ShortenUrlInfo shortenUrlInfo = UrlShortener
					.calculateShortenShareableUrl(feed);
			feed.setShareableUrl(shortenUrlInfo.getUrl());
			feed.setBatchId(batchId);
			RSSFeed rssFeed = ModelConverter.convert(feed);
			service.uploadNewsFeed(rssFeed, ttl, batchId, true);
		}
		return !checkFeedExists;
	}
}
