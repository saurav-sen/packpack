package com.pack.pack.services.redis.services;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
import com.pack.pack.services.redis.IRefreshmentFeedService;
import com.pack.pack.services.redis.RssFeedRepositoryService;
import com.pack.pack.services.redis.UrlShortener;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.EncryptionUtil;
import com.pack.pack.util.ModelConverter;
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
public class RefreshmentFeedServiceImpl implements IRefreshmentFeedService {

	private static final Logger LOG = LoggerFactory
			.getLogger(RefreshmentFeedServiceImpl.class);

	@Override
	public Pagination<JRssFeed> getAllRssFeeds(String userId, int pageNo)
			throws PackPackException {
		if (pageNo < 0) {
			Pagination<JRssFeed> page = new Pagination<JRssFeed>();
			page.setNextPageNo(-1);
			page.setResult(Collections.emptyList());
			return page;
		}

		boolean promotionalFeeds = false;
		RssFeedRepositoryService repositoryService = ServiceRegistry.INSTANCE
				.findService(RssFeedRepositoryService.class);
		List<RSSFeed> feeds = Collections.emptyList();

		feeds = repositoryService.getAllRefrehmentFeeds();
		LOG.debug("Promotional Feeds Count = " + feeds.size());

		boolean ignoreVideoFeeds = false;
		boolean ignoreSlideShows = false;
		List<RSSFeed> result = feeds;
		List<JRssFeed> rows = ModelConverter.convertAllRssFeeds(result,
				ignoreVideoFeeds, ignoreSlideShows);
		Iterator<JRssFeed> itr = rows.iterator();
		while(itr.hasNext()) {
			JRssFeed feed = itr.next();
			if(!JRssFeedType.REFRESHMENT.name().equalsIgnoreCase(feed.getOgType())) {
				itr.remove();
			}
		}
		/*if (!promotionalFeeds) {
			Collections.sort(rows, new RssFeedComparator(userId));
		} else {*/
			Collections.sort(rows, new Comparator<JRssFeed>() {
				@Override
				public int compare(JRssFeed o1, JRssFeed o2) {
					return (int) (o2.getUploadTime() - o1.getUploadTime());
				}
			});
		//}
		Pagination<JRssFeed> pageResult = new Pagination<JRssFeed>();
		pageResult.setNextPageNo(-1);
		pageResult.setResult(rows);
		return pageResult;
	}

	@Override
	public boolean upload(JRssFeed feed, TTL ttl, long batchId)
			throws PackPackException {
		try {
			RssFeedRepositoryService service = ServiceRegistry.INSTANCE
					.findService(RssFeedRepositoryService.class);
			boolean checkFeedExists = service.checkFeedExists(feed);
			if (!checkFeedExists) {
				boolean storeSharedFeed = true;
				ShortenUrlInfo shortenUrlInfo = UrlShortener
						.calculateShortenShareableUrl(feed, SystemPropertyUtil
								.getExternalSharedLinkRefreshmentBaseUrl(),
								storeSharedFeed);
				feed.setShareableUrl(shortenUrlInfo.getUrl());
				RSSFeed rssFeed = ModelConverter.convert(feed);
				service.uploadRefreshmentFeed(rssFeed, ttl);
			}
			return !checkFeedExists;
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61, e.getMessage());
		}
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out
				.println(EncryptionUtil
						.generateSH1HashKey(
								"http://www.photodestination.co.za/images/peter_blog_post/Great%20Seascapes%20Tips/8_Tsitsikamma%20sea%20and%20rocks%20by%20wildlife%20and%20conservation%20photographer%20peter%20chadwick.jpg",
								false, true));
	}
}