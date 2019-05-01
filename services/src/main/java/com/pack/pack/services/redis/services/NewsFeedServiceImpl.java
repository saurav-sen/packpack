package com.pack.pack.services.redis.services;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.RSSFeed;
import com.pack.pack.model.RssFeedType;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.ShortenUrlInfo;
import com.pack.pack.model.web.dto.FeedPublish;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.redis.INewsFeedService;
import com.pack.pack.services.redis.RssFeedRepositoryService;
import com.pack.pack.services.redis.UrlShortener;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.ModelConverter;
import com.pack.pack.util.RssFeedUtil;
import com.pack.pack.util.SystemPropertyUtil;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;
import com.squill.feed.web.model.JRssFeeds;
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

	// private static final int MINIMUM_PAGE_SIZE = 10;

	@Override
	public List<JRssFeed> getAllFeeds() throws PackPackException {
		List<JRssFeed> list = new ArrayList<JRssFeed>();
		RssFeedRepositoryService repositoryService = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		Pagination<RSSFeed> page = repositoryService
				.getAllFeedsInStore(RssFeedUtil.resolvePrefix(JRssFeedType.NEWS
						.name()) + "*");
		list.addAll(ModelConverter.convertAllRssFeeds(page.getResult(), true,
				true));
		page = repositoryService.getAllFeedsInStore(RssFeedUtil
				.resolvePrefix(JRssFeedType.ARTICLE.name()) + "*");
		list.addAll(ModelConverter.convertAllRssFeeds(page.getResult(), true,
				true));

		page = repositoryService.getAllFeedsInStore(RssFeedUtil
				.resolvePrefix(JRssFeedType.REFRESHMENT.name()) + "*");
		list.addAll(ModelConverter.convertAllRssFeeds(page.getResult(), false,
				false));

		page = repositoryService.getAllFeedsInStore(RssFeedUtil
				.resolvePrefix(JRssFeedType.OPINION.name()) + "*");
		list.addAll(ModelConverter.convertAllRssFeeds(page.getResult(), false,
				false));
		return list;
	}

	@Override
	public List<JRssFeed> getAllOpinionsFeeds() throws PackPackException {
		List<JRssFeed> list = new ArrayList<JRssFeed>();
		RssFeedRepositoryService repositoryService = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		Pagination<RSSFeed> page = repositoryService
				.getAllFeedsInStore(RssFeedUtil
						.resolvePrefix(JRssFeedType.OPINION.name()) + "*");
		list.addAll(ModelConverter.convertAllRssFeeds(page.getResult(), false,
				false));
		return list;
	}

	@Override
	public void cleanupExpiredPageInfos() {
		RssFeedRepositoryService repositoryService = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		repositoryService.cleanupRangeKeys();
	}

	@Override
	public Pagination<JRssFeed> getAllNewsRssFeeds(String userId, int pageNo)
			throws PackPackException {
		return getAllRssFeeds(JRssFeedType.NEWS, userId, pageNo);
	}

	@Override
	public Pagination<JRssFeed> getAllSportsNewsRssFeeds(String userId,
			int pageNo) throws PackPackException {
		return getAllRssFeeds(JRssFeedType.NEWS_SPORTS, userId, pageNo);
	}

	@Override
	public Pagination<JRssFeed> getAllOpinionRssFeeds(String userId, int pageNo)
			throws PackPackException {
		return getAllRssFeeds(JRssFeedType.OPINION, userId, pageNo);
	}

	@Override
	public Pagination<JRssFeed> getAllScienceAndTechnologyNewsRssFeeds(
			String userId, int pageNo) throws PackPackException {
		return getAllRssFeeds(JRssFeedType.NEWS_SCIENCE_TECHNOLOGY, userId,
				pageNo);
	}

	@Override
	public Pagination<JRssFeed> getArticleNewsRssFeeds(String userId, int pageNo)
			throws PackPackException {
		return getAllRssFeeds(JRssFeedType.ARTICLE, userId, pageNo);
	}

	private Pagination<JRssFeed> getAllRssFeeds(JRssFeedType type,
			String userId, int pageNo) throws PackPackException {
		if (pageNo < 0) {
			return endOfPageResponse();
		}
		List<JRssFeed> result = new ArrayList<JRssFeed>();
		$_LOG.info("Reading from pageNo = " + pageNo);
		Pagination<JRssFeed> page = getAllRssFeeds0(type, userId, pageNo);
		int nextPageNo = page.getNextPageNo();
		boolean needToIterate = false;
		List<JRssFeed> feeds = page.getResult();
		result.addAll(feeds);
		while (result != null && nextPageNo > 0 && result.size() < 4) {
			pageNo++;
			$_LOG.info("Reading from next pageNo = " + pageNo);
			Pagination<JRssFeed> page0 = getAllRssFeeds0(type, userId, pageNo);
			feeds = page0.getResult();
			result.addAll(feeds);
			needToIterate = true;
			nextPageNo = page0.getNextPageNo();
		}
		if (needToIterate) {
			$_LOG.debug("Needed to iterate over multiple pages due to MINIMUM_PAGE_SIZE");
			page.setResult(result);
			page.setNextPageNo(nextPageNo);
		}
		return page;
	}

	private Pagination<JRssFeed> getAllRssFeeds0(JRssFeedType type,
			String userId, int pageNo) throws PackPackException {
		if (pageNo < 0) {
			return endOfPageResponse();
		}
		/*
		 * List<RSSFeed> feeds = Collections.emptyList(); List<JRssFeed> rows =
		 * ModelConverter.convertAllRssFeeds(feeds, true, true);
		 */
		Pagination<RSSFeed> page = null;
		RssFeedRepositoryService repositoryService = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		switch (type) {
		case NEWS:
			page = repositoryService.getNewsFeeds(pageNo);
			break;
		case NEWS_SPORTS:
			page = repositoryService.getSportsNewsFeeds(pageNo);
			break;
		case OPINION:
			page = repositoryService.getOpinionFeeds(pageNo);
			break;
		case NEWS_SCIENCE_TECHNOLOGY:
			page = repositoryService.getScienceAndTechnologyNewsFeeds(pageNo);
			break;
		case ARTICLE:
			page = repositoryService.getArticleNewsFeeds(pageNo);
			break;
		default:
			break;
		}

		if (page == null) {
			return endOfPageResponse();
		}

		Pagination<JRssFeed> pageResult = new Pagination<JRssFeed>();
		pageResult.setNextPageNo(page.getNextPageNo());
		pageResult.setResult(ModelConverter.convertAllRssFeeds(
				page.getResult(), true, true));
		/*
		 * Collections.sort(rows, new RssFeedComparator(userId));
		 * pageResult.setResult(rows);
		 */
		return pageResult;
	}

	private Pagination<JRssFeed> endOfPageResponse() {
		Pagination<JRssFeed> page = new Pagination<JRssFeed>();
		page.setNextPageNo(-1);
		page.setResult(Collections.emptyList());
		return page;
	}

	@Override
	public Set<String> upload(List<JRssFeed> feeds, TTL ttl, long batchId)
			throws PackPackException {
		if (feeds == null || feeds.isEmpty())
			return Collections.emptySet();
		Set<String> allIds = new HashSet<String>();
		try {
			RssFeedRepositoryService service = ServiceRegistry.INSTANCE
					.findService(RssFeedRepositoryService.class);
			List<RSSFeed> toAdd = new ArrayList<RSSFeed>();
			List<RSSFeed> toUpdate = new ArrayList<RSSFeed>();
			for (JRssFeed feed : feeds) {
				if (feed.getShareableUrl() == null) {
					boolean storeSharedFeed = false;
					ShortenUrlInfo shortenUrlInfo = UrlShortener
							.calculateShortenShareableUrl(feed,
									SystemPropertyUtil
											.getExternalSharedLinkBaseUrl(),
									storeSharedFeed);
					feed.setShareableUrl(shortenUrlInfo.getUrl());
				}
				RSSFeed rssFeed = ModelConverter.convert(feed);
				if (!service.checkFeedExists(feed)) {
					toAdd.add(rssFeed);
				} else {
					toUpdate.add(rssFeed);
				}
			}
			Set<String> ids = service.uploadNewsFeed(toAdd, ttl, batchId, true);
			if (ids != null && !ids.isEmpty()) {
				allIds.addAll(ids);
			}
			ids = service.uploadNewsFeed(toUpdate, ttl, batchId, false);
			if (ids != null && !ids.isEmpty()) {
				allIds.addAll(ids);
			}
			return allIds;
		} catch (NoSuchAlgorithmException e) {
			$_LOG.error(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61, e.getMessage());
		}
	}

	@Override
	public JRssFeed getFeedById(String id) throws PackPackException {
		RssFeedRepositoryService service = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		return ModelConverter.convert(service.getFeedByKey(id));
	}

	@Override
	public JRssFeed upload(FeedPublish feedPublish) throws PackPackException {
		RssFeedRepositoryService service = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		String id = feedPublish.getId();
		if (id == null || id.trim().isEmpty())
			return null;
		RSSFeed rssFeed = service.getFeedByKey(id);
		if (rssFeed == null)
			return null;
		String titleText = feedPublish.getTitleText();
		if (titleText != null && !titleText.trim().isEmpty()) {
			rssFeed.setOgTitle(titleText);
		}
		String summaryText = feedPublish.getSummaryText();
		if (summaryText != null && !summaryText.trim().isEmpty()) {
			rssFeed.setArticleSummaryText(summaryText);
			rssFeed.setOgDescription(summaryText);
		}
		rssFeed.setOpenDirectLink(feedPublish.isOpenDirectLink());
		boolean success = service.updateFeed(id, rssFeed);
		if(!success) {
			return null;
		}
		return ModelConverter.convert(rssFeed);
	}

	@Override
	public JRssFeed delete(String id) throws PackPackException {
		if (id == null || id.trim().isEmpty())
			return null;
		RssFeedRepositoryService service = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		RSSFeed rssFeed = service.deleteFeedByKey(id);
		if (rssFeed == null)
			return null;
		// rssFeed.setId(id);
		return ModelConverter.convert(rssFeed);
	}

	@Override
	public void storeRecentFeedIds(Set<String> recentFeedIds)
			throws PackPackException {
		RssFeedRepositoryService service = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		service.storeRecentFeedIds(recentFeedIds);
	}

	@Override
	public Set<String> getRecentFeedIds() throws PackPackException {
		RssFeedRepositoryService service = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		return service.getRecentFeedIds();
	}

	@Override
	public JRssFeeds getRecentAutoUploadFeeds() throws PackPackException {
		RssFeedRepositoryService service = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		List<RSSFeed> feeds = service.getRecentAutoUploadFeeds();
		JRssFeeds result = new JRssFeeds();
		if (feeds == null || feeds.isEmpty())
			return result;
		for (RSSFeed feed : feeds) {
			JRssFeed r = ModelConverter.convert(feed);
			if (r == null)
				continue;
			result.getFeeds().add(r);
		}
		return result;
	}

	@Override
	public Pagination<JRssFeed> getUnprovisionUploadFeeds(int pageNo)
			throws PackPackException {
		Pagination<JRssFeed> page = new Pagination<JRssFeed>();
		page.setResult(Collections.emptyList());
		page.setNextPageNo(-1);
		if (pageNo < 0)
			return page;
		RssFeedRepositoryService service = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		Pagination<RSSFeed> p0 = service.getAllFeedsInStore(RssFeedUtil
				.resolvePrefix(JRssFeedType.UNPROVISIONED.name()));
		List<RSSFeed> result0 = p0.getResult();
		if (result0 == null || result0.isEmpty()) {
			return page;
		}
		result0.sort(new Comparator<RSSFeed>() {
			@Override
			public int compare(RSSFeed r1, RSSFeed r2) {
				return (int) (r1.getUploadTime() - r2.getUploadTime());
			}
		});
		int length = result0.size();
		int pageSize = 20;
		int startIndex = pageNo * pageSize;
		if (startIndex >= length)
			return page;
		List<JRssFeed> result = new ArrayList<JRssFeed>();
		int endIndex = startIndex + pageSize;
		int index = startIndex;
		for (; index < length && index < endIndex; index++) {
			RSSFeed rssFeed = result0.get(index);
			if(!RssFeedType.UNPROVISIONED.name().equals(rssFeed.getFeedType()))
				continue;
			JRssFeed feed = ModelConverter.convert(rssFeed);
			result.add(feed);
		}
		endIndex = index;
		if (endIndex >= length)
			endIndex = -1;
		page.setResult(result);
		page.setNextPageNo(endIndex);
		return page;
	}

	@Override
	public void storeUnprovisionedFeeds(List<JRssFeed> feeds, TTL ttl)
			throws PackPackException {
		//Set<String> keys = new HashSet<String>();
		try {
			RssFeedRepositoryService service = ServiceRegistry.INSTANCE
					.findService(RssFeedRepositoryService.class);
			Iterator<JRssFeed> itr = feeds.iterator();
			while (itr.hasNext()) {
				JRssFeed feed = itr.next();
				feed.setUploadTime(System.currentTimeMillis());
				RSSFeed f = ModelConverter.convert(feed);
				service.uploadUnprovisionedFeed(f, ttl);
				/*String key = service.uploadUnprovisionedFeed(f, ttl);
				if(key != null) {
					keys.add(key);
				}*/
			}
		} catch (NoSuchAlgorithmException e) {
			throw new PackPackException(ErrorCodes.PACK_ERR_62, e.getMessage(),
					e);
		}
		//return keys;
	}
	
	@Override
	public void markAsProvisionedByFeedId(String id, JRssFeedType newType)
			throws PackPackException {
		try {
			RssFeedRepositoryService service = ServiceRegistry.INSTANCE
					.findService(RssFeedRepositoryService.class);
			JRssFeed feed = getFeedById(id);
			RSSFeed f = ModelConverter.convert(feed);
			f.setFeedType(newType.name());
			service.provisionFeed(f, null);
		} catch (NoSuchAlgorithmException e) {
			throw new PackPackException(ErrorCodes.PACK_ERR_62, e.getMessage(),
					e);
		}
	}
}
