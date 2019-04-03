package com.squill.og.crawler.internal;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.web.Constants;
import com.pack.pack.services.redis.INewsFeedService;
import com.pack.pack.services.registry.ServiceRegistry;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.TTL;
import com.squill.og.crawler.IWebCrawlable;
import com.squill.og.crawler.hooks.ISpiderSession;

/**
 * 
 * @author Saurav
 *
 */
@Component("unprovisionFeedUploader")
@Scope("prototype")
public class UnprovisionFeedUploader extends DefaultOgFeedUploader {

	private static final Logger $_LOG = LoggerFactory
			.getLogger(DefaultOgFeedUploader.class);

	@Override
	protected void handleReceivedAITaksNotExecuted(
			Map<String, List<JRssFeed>> feedsMap, ISpiderSession session,
			IWebCrawlable webCrawlable) throws Exception {
		List<JRssFeed> list = feedsMap.get(webCrawlable.getUniqueId());
		if (list == null || list.isEmpty())
			return;
		INewsFeedService service = ServiceRegistry.INSTANCE
				.findCompositeService(INewsFeedService.class);
		Iterator<JRssFeed> itr = list.iterator();
		while (itr.hasNext()) {
			JRssFeed f = itr.next();
			JRssFeed existing = service
					.getFeedById(Constants.UN_PROVISIONED_KEY_PREFIX
							+ f.getOgUrl());
			if (existing != null) {
				$_LOG.info("Found existing Unprovisioned Feeds @ "
						+ f.getOgUrl() + " hence skipping upload.");
				itr.remove();
			}
		}
		if (list.isEmpty())
			return;
		TTL ttl = new TTL();
		ttl.setTime((short) 1);
		ttl.setUnit(TimeUnit.DAYS);
		$_LOG.info("Storing Unprovisioned Feeds size = " + list.size());
		service.storeUnprovisionedFeeds(list, ttl, System.currentTimeMillis());
	}
}
