package com.pack.pack.services.redis.services;

import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE;
import static com.pack.pack.common.util.CommonConstants.NEXT_PAGE_LINK_PREFIX;
import static com.pack.pack.common.util.CommonConstants.NULL_PAGE_LINK;
import static com.pack.pack.common.util.CommonConstants.PREV_PAGE_LINK_PREFIX;
import static com.pack.pack.common.util.CommonConstants.STANDARD_NEWS_PAGE_SIZE;
import static com.pack.pack.common.util.CommonConstants.STANDARD_PAGE_SIZE;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.RSSFeed;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.ShortenUrlInfo;
import com.pack.pack.model.web.dto.RssFeedSourceType;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.redis.IRefreshmentFeedService;
import com.pack.pack.services.redis.RssFeedRepositoryService;
import com.pack.pack.services.redis.UrlShortener;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.EncryptionUtil;
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
public class RefreshmentFeedServiceImpl implements IRefreshmentFeedService {
	
	private static final Logger LOG = LoggerFactory.getLogger(RefreshmentFeedServiceImpl.class);
	
	@Override
	public Pagination<JRssFeed> getAllRssFeeds(String userId, String pageLink) throws PackPackException {
		if (pageLink == null || END_OF_PAGE.equals(pageLink.trim())) {
			Pagination<JRssFeed> page = new Pagination<JRssFeed>();
			page.setNextLink(END_OF_PAGE);
			page.setPreviousLink(END_OF_PAGE);
			page.setResult(Collections.emptyList());
			return page;
		}

		/* **************************************************************************************************************************** */
		// Here we need to load from different place if it is already loaded for
		// the day (or couple-of-hours) once &
		// have B+-Tree implementation for our own purpose to minimize
		// comparison (with user-location retrieved as above) to make it
		// as much responsive as possible.
		boolean paginationRequired = false;
		boolean promotionalFeeds = false;
		RssFeedRepositoryService repositoryService = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		List<RSSFeed> feeds = Collections.emptyList();
		String source = JRssFeedType.REFRESHMENT.name();
		
		feeds = repositoryService.getAllRefrehmentFeeds();
		LOG.debug("Promotional Feeds Count = " + feeds.size());
		promotionalFeeds = true;
		
		/*LOG.debug("source = " + source);
		if (source == null || source.trim().isEmpty()
				|| "default".equals(source)
				|| RssFeedSourceType.SQUILL_TEAM.equals(source)
				|| JRssFeedType.REFRESHMENT.name().equals(source)) {
			feeds = repositoryService.getAllPromotionalFeeds();
			LOG.debug("Promotional Feeds Count = " + feeds.size());
			promotionalFeeds = true;
		} else if (RssFeedSourceType.NEWS_API.equals(source)
				|| JRssFeedType.NEWS.name().equals(source)) {
			feeds = repositoryService.getAllNewsFeeds(JRssFeedType.NEWS.name());
			paginationRequired = true;
			LOG.debug("News Feeds Count = " + feeds.size());
		} else if (JRssFeedType.NEWS_SCIENCE_TECHNOLOGY.name().equals(source)) {
			feeds = repositoryService
					.getAllNewsFeeds(JRssFeedType.NEWS_SCIENCE_TECHNOLOGY
							.name());
			paginationRequired = true;
			LOG.debug("News Feeds Count = " + feeds.size());
		} else if (JRssFeedType.NEWS_SPORTS.name().equals(source)) {
			feeds = repositoryService.getAllNewsFeeds(JRssFeedType.NEWS_SPORTS
					.name());
			paginationRequired = true;
			LOG.debug("News Feeds Count = " + feeds.size());
		}*/
		/* **************************************************************************************************************************** */

		boolean ignoreVideoFeeds = false;
		boolean ignoreSlideShows = false;
		/*boolean ignoreVideoFeeds = true;
		boolean ignoreSlideShows = true;
		if(apiVersion != null && apiVersion.equalsIgnoreCase("v2")) {
			ignoreVideoFeeds = false;
			ignoreSlideShows = false;
		}*/
		// For now (for demo purpose lets just return all the feeds).
		Pagination<RSSFeed> page = null;
		List<RSSFeed> result = feeds;
		if(paginationRequired) {
			page = paginate(feeds, pageLink, source);
			result = page.getResult();
		}
		List<JRssFeed> rows = ModelConverter.convertAllRssFeeds(result, ignoreVideoFeeds, ignoreSlideShows);
		if(!promotionalFeeds) {
			Collections.sort(rows, new RssFeedComparator(userId));
		} else {
			Collections.sort(rows, new Comparator<JRssFeed>() {
				@Override
				public int compare(JRssFeed o1, JRssFeed o2) {
					return (int) (o2.getUploadTime() - o1.getUploadTime());
				}
			});
		}
		Pagination<JRssFeed> pageResult = new Pagination<JRssFeed>();
		if(paginationRequired) {
			pageResult.setNextLink(page.getNextLink());
			pageResult.setPreviousLink(page.getPreviousLink());
		} else {
			pageResult.setNextLink(END_OF_PAGE);
			pageResult.setPreviousLink(END_OF_PAGE);
		}
		pageResult.setResult(rows);
		return pageResult;
	}
	
	private Pagination<RSSFeed> paginate(List<RSSFeed> feeds, String pageLink, String source) {
		Pagination<RSSFeed> page = new Pagination<RSSFeed>();
		if (pageLink == null || pageLink.trim().isEmpty()) {
			pageLink = NULL_PAGE_LINK;
		}

		Collections.sort(feeds, new Comparator<RSSFeed>() {
			@Override
			public int compare(RSSFeed o1, RSSFeed o2) {
				try {
					long l = o2.getUploadTime() - o1.getUploadTime();
					if (l == 0) {
						return 0;
					}
					if (l > 0) {
						return 1;
					}
					return -1;
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					return 0;
				}
			}
		});
		if (NULL_PAGE_LINK.equals(pageLink)) {
			List<RSSFeed> result = new ArrayList<RSSFeed>();
			int len = feeds.size();
			if (len >= resolvePageSize(source)) {
				len = resolvePageSize(source);
			}
			RSSFeed lastFeed = null;
			for (int i = 0; i < len; i++) {
				lastFeed = feeds.get(i);
				result.add(lastFeed);
			}
			page.setResult(result);
			if (lastFeed != null) {
				page.setNextLink(NEXT_PAGE_LINK_PREFIX
						+ String.valueOf(lastFeed.getUploadTime()));
			} else {
				page.setNextLink(END_OF_PAGE);
			}
			page.setPreviousLink(END_OF_PAGE);
		} else if (END_OF_PAGE.equals(pageLink)) {
			page.setResult(Collections.emptyList());
			page.setPreviousLink(END_OF_PAGE);
			page.setNextLink(END_OF_PAGE);
		} else {
			try {
				String link = pageLink.trim();
				boolean isNext = true;
				String timestamp = link.replaceFirst(NEXT_PAGE_LINK_PREFIX, "");
				if (link.startsWith(PREV_PAGE_LINK_PREFIX)) {
					isNext = false;
					timestamp = link.replaceFirst(PREV_PAGE_LINK_PREFIX, "");
				}

				LOG.debug("Timestamp from pageLink = " + timestamp);

				long uploadTime = Long.parseLong(timestamp);
				List<RSSFeed> result = new ArrayList<RSSFeed>();
				int len = feeds.size();
				int count = 0;
				RSSFeed lastFeed = null;
				int pageSize = resolvePageSize(source);
				for (int i = 0; i < len; i++) {
					RSSFeed feed = feeds.get(i);
					if (isIncludeInPage(feed, uploadTime, isNext)) {
						result.add(feed);
						lastFeed = feed;
						count++;
						/*if ((isNext && pageLinkTimestamp < uploadTime)
								|| (!isNext && pageLinkTimestamp > uploadTime)) {
							pageLinkTimestamp = uploadTime;
						}*/
						if(count == pageSize) {
							i = len;
						}
					}
				}
				
				long pageLinkTimestamp = lastFeed != null ? lastFeed.getUploadTime() : -1;
				if (isNext) {
					if(pageLinkTimestamp > 0) {
						page.setNextLink(NEXT_PAGE_LINK_PREFIX
								+ pageLinkTimestamp);
					} else {
						page.setNextLink(END_OF_PAGE);
					}
					page.setPreviousLink(pageLink.replaceFirst(
							NEXT_PAGE_LINK_PREFIX,
							PREV_PAGE_LINK_PREFIX));
				} else {
					if(pageLinkTimestamp > 0) {
						page.setPreviousLink(PREV_PAGE_LINK_PREFIX
								+ pageLinkTimestamp);
					} else {
						page.setPreviousLink(END_OF_PAGE);
					}
					page.setNextLink(pageLink.replaceFirst(
							PREV_PAGE_LINK_PREFIX,
							NEXT_PAGE_LINK_PREFIX));
				}
				
				page.setResult(result);
			} catch (NumberFormatException e) {
				page.setResult(Collections.emptyList());
				page.setNextLink(pageLink);
				LOG.error("Failed parsing pagelink :: " + pageLink, e);
			}

		}
		return page;
	}
	
	private int resolvePageSize(String source) {
		/*if (source == null || source.trim().isEmpty()
				|| "default".equals(source)
				|| RssFeedSourceType.SQUILL_TEAM.equals(source)
				|| JRssFeedType.REFRESHMENT.name().equals(source)) {
			return STANDARD_PAGE_SIZE;
		} else if (RssFeedSourceType.NEWS_API.equals(source)
				|| JRssFeedType.NEWS.name().equals(source)) {
			return STANDARD_PAGE_SIZE;
		}
		return STANDARD_PAGE_SIZE;*/
		if (RssFeedSourceType.NEWS_API.equals(source)
				|| JRssFeedType.NEWS.name().equals(source)) {
			return STANDARD_NEWS_PAGE_SIZE;
		}
		return STANDARD_PAGE_SIZE;
	}
	
	private boolean isIncludeInPage(RSSFeed feed, long uploadTime,
			boolean isNext) {
		long uploadTime2 = feed.getUploadTime();
		return isNext ? uploadTime2 < uploadTime : uploadTime2 > uploadTime;
	}
	
	@Override
	public boolean upload(JRssFeed feed, TTL ttl, long batchId) throws PackPackException {
		try {
			RssFeedRepositoryService service = ServiceRegistry.INSTANCE
					.findService(RssFeedRepositoryService.class);
			boolean checkFeedExists = service.checkFeedExists(feed);
			if(!checkFeedExists) {
				ShortenUrlInfo shortenUrlInfo = UrlShortener.calculateShortenShareableUrl(feed);
				feed.setShareableUrl(shortenUrlInfo.getUrl());
				feed.setBatchId(batchId);
				RSSFeed rssFeed = ModelConverter.convert(feed);
				service.uploadRefreshmentFeed(rssFeed, ttl);
			}
			return !checkFeedExists;
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61, e.getMessage());
		}
		//return ModelConverter.convert(rssFeed);
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out
				.println(EncryptionUtil
						.generateSH1HashKey(
								"http://www.photodestination.co.za/images/peter_blog_post/Great%20Seascapes%20Tips/8_Tsitsikamma%20sea%20and%20rocks%20by%20wildlife%20and%20conservation%20photographer%20peter%20chadwick.jpg",
								false, true));
	}
	
	/*@Override
	public Pagination<JRssFeed> getAllRssFeeds(String userId, String pageLink)
			throws PackPackException {
		if (pageLink == null || END_OF_PAGE.equals(pageLink.trim())) {
			Pagination<JRssFeed> page = new Pagination<JRssFeed>();
			page.setNextLink(END_OF_PAGE);
			page.setPreviousLink(END_OF_PAGE);
			page.setResult(Collections.emptyList());
			return page;
		}
		UserLocationRepositoryService locationService = ServiceRegistry.INSTANCE
				.findService(UserLocationRepositoryService.class);
		UserLocation location = locationService.findUserLocationById(userId);

		long now = System.currentTimeMillis();
		long startTime = now - 2 * 60 * 60 * 1000; // 2 hours before
		long endTime = now + 2 * 60 * 60 * 1000;*/

		/* **************************************************************************************************************************** */
		// Here we need to load from different place if it is already loaded for
		// the day (or couple-of-hours) once &
		// have B+-Tree implementation for our own purpose to minimize
		// comparison (with user-location retrieved as above) to make it
		// as much responsive as possible.
		/*RssFeedRepositoryService repositoryService = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		List<RSSFeed> feeds = repositoryService.getAllPromotionalFeeds(
				startTime, endTime);*/
		/* **************************************************************************************************************************** */

		// For now (for demo purpose lets just return all the feeds).
		/*List<RSSFeed> result = feeds;
		List<JRssFeed> rows = ModelConverter.convertAllRssFeeds(result);
		Pagination<JRssFeed> page = new Pagination<JRssFeed>();
		page.setNextLink(END_OF_PAGE);
		page.setPreviousLink(NULL_PAGE_LINK);
		page.setResult(rows);
		return page;*/
	//}

	/*@Override
	public JRssFeed upload(JRssFeed feed, TTL ttl) {
		RSSFeed rssFeed = ModelConverter.convert(feed);
		long now = System.currentTimeMillis();
		rssFeed.setPromoStartTimestamp(String.valueOf(now));
		long expiry = ttl.getTime();
		TimeUnit unit = ttl.getUnit();
		switch(unit) {
		case NANOSECONDS:
			expiry = expiry / (1000*1000);
			break;
		case MICROSECONDS:
			expiry = expiry / 1000;
			break;
		case MILLISECONDS:
			break;
		case SECONDS:
			expiry = expiry * 1000;
			break;
		case MINUTES:
			expiry = expiry * 60 * 1000;
			break;
		case HOURS:
			expiry = expiry * 60 * 60 * 1000;
			break;
		case DAYS:
			expiry = expiry * 24 * 60 * 60 * 1000;
			break;
		}
		expiry = now + expiry;
		rssFeed.setPromoExpiryTimestamp(String.valueOf(expiry));
		RssFeedRepositoryService service = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		service.add(rssFeed);
		return ModelConverter.convert(rssFeed);
	}*/
}