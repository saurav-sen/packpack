package com.pack.pack.services.redis.services;

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
import com.pack.pack.util.RssFeedUtil;
import com.pack.pack.util.SystemPropertyUtil;
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

	//private static final int MINIMUM_PAGE_SIZE = 10;
	
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
	public Pagination<JRssFeed> getAllNewsRssFeeds(String userId,
			int pageNo) throws PackPackException {
		return getAllRssFeeds(JRssFeedType.NEWS, userId, pageNo);
	}

	@Override
	public Pagination<JRssFeed> getAllSportsNewsRssFeeds(String userId,
			int pageNo) throws PackPackException {
		return getAllRssFeeds(JRssFeedType.NEWS_SPORTS, userId, pageNo);
	}
	
	@Override
	public Pagination<JRssFeed> getAllOpinionRssFeeds(String userId,
			int pageNo) throws PackPackException {
		return getAllRssFeeds(JRssFeedType.OPINION, userId, pageNo);
	}

	@Override
	public Pagination<JRssFeed> getAllScienceAndTechnologyNewsRssFeeds(
			String userId, int pageNo) throws PackPackException {
		return getAllRssFeeds(JRssFeedType.NEWS_SCIENCE_TECHNOLOGY, userId,
				pageNo);
	}

	@Override
	public Pagination<JRssFeed> getArticleNewsRssFeeds(String userId,
			int pageNo) throws PackPackException {
		return getAllRssFeeds(JRssFeedType.ARTICLE, userId, pageNo);
	}

	private Pagination<JRssFeed> getAllRssFeeds(JRssFeedType type,
			String userId, int pageNo) throws PackPackException {
		if(pageNo < 0) {
			return endOfPageResponse();
		}
		List<JRssFeed> result = new ArrayList<JRssFeed>();
		$_LOG.info("Reading from pageNo = " + pageNo);
		Pagination<JRssFeed> page = getAllRssFeeds0(type, userId, pageNo);
		int nextPageNo = page.getNextPageNo();
		boolean needToIterate = false;
		List<JRssFeed> feeds = page.getResult();
		result.addAll(feeds);
		while (result != null && nextPageNo > 0
				&& result.size() < 4) {
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
	public boolean upload(List<JRssFeed> feeds, TTL ttl, long batchId)
			throws PackPackException {
		if (feeds == null || feeds.isEmpty())
			return false;
		try {
			RssFeedRepositoryService service = ServiceRegistry.INSTANCE
					.findService(RssFeedRepositoryService.class);
			List<RSSFeed> toAdd = new ArrayList<RSSFeed>();
			List<RSSFeed> toUpdate = new ArrayList<RSSFeed>();
			for(JRssFeed feed : feeds) {
				if (feed.getShareableUrl() == null) {
					boolean storeSharedFeed = false;
					ShortenUrlInfo shortenUrlInfo = UrlShortener
							.calculateShortenShareableUrl(feed,
									SystemPropertyUtil.getExternalSharedLinkBaseUrl(),
									storeSharedFeed);
					feed.setShareableUrl(shortenUrlInfo.getUrl());
				}
				RSSFeed rssFeed = ModelConverter.convert(feed);
				if(!service.checkFeedExists(feed)) {
					toAdd.add(rssFeed);
				} else {
					toUpdate.add(rssFeed);
				}
			}
			service.uploadNewsFeed(toAdd, ttl, batchId, true);
			service.uploadNewsFeed(toUpdate, ttl, batchId, false);
			return true;
		} catch (NoSuchAlgorithmException e) {
			$_LOG.error(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61, e.getMessage());
		}
	}
}
