package com.squill.broadcast.feed.upload;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.redis.RedisCacheService;
import com.pack.pack.services.registry.ServiceRegistry;
import com.squill.feed.web.model.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
public class RandomFeedSelectionStrategy implements FeedSelectionStrategy {

	private RedisCacheService cacheService;

	private Map<String, List<JRssFeed>> randomFeeds = new HashMap<String, List<JRssFeed>>();

	private static final String CACHE_KEY_PREFIX = "RANDOM_FEED_SELECT_";

	private static final Logger LOG = LoggerFactory
			.getLogger(RandomFeedSelectionStrategy.class);

	public RandomFeedSelectionStrategy() {
		cacheService = ServiceRegistry.INSTANCE
				.findService(RedisCacheService.class);
	}

	@Override
	public void applyStrategy(Map<String, List<JRssFeed>> feedsMap) {
		if (feedsMap == null || feedsMap.isEmpty()) {
			return;
		}
		Iterator<String> itr = feedsMap.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			List<JRssFeed> list = feedsMap.get(key);
			if (list == null || list.isEmpty()) {
				continue;
			}
			List<JRssFeed> random10 = randomizeSelection(list, 10, true);
			List<JRssFeed> list2 = randomFeeds.get(key);
			if (list2 == null) {
				list2 = random10;
				randomFeeds.put(key, list2);
			} else {
				list2.addAll(random10);
			}
		}
	}

	private List<JRssFeed> randomizeSelection(List<JRssFeed> originalItems,
			int maxSelection, boolean validateSelectionInCache) {
		int len = originalItems.size();
		int max = len < maxSelection ? len : maxSelection;
		List<JRssFeed> result = new LinkedList<JRssFeed>();
		for (int i = 0; i < max; i++) {
			JRssFeed feed = makeSelection(originalItems, len,
					validateSelectionInCache);
			result.add(feed);
		}
		return result;
	}

	private boolean checkExistenceInCache(String cacheKey) {
		return cacheService.isKeyExists(cacheKey);
	}

	private void addEntryToCache(String cacheKey, String value,
			long ttlInSeconds) {
		try {
			cacheService.addToCache(cacheKey, value, ttlInSeconds);
		} catch (PackPackException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private JRssFeed makeSelection(List<JRssFeed> originalItems, int len,
			boolean validateSelectionInCache) {
		JRssFeed feed = null;
		int j = 0;
		int rand0 = Math.abs(new Random().nextInt()) % len;
		int rand = rand0;
		while (j < len && feed == null) {
			JRssFeed tmp = originalItems.get(rand);
			if (validateSelectionInCache) {
				String cacheKey = CACHE_KEY_PREFIX + tmp.getId();
				if (!checkExistenceInCache(cacheKey)) {
					feed = tmp;
					long threeMonths = 30 * 24 * 60 * 60;
					addEntryToCache(cacheKey, tmp.getId(), threeMonths);
				} else {
					int nextRand = Math.abs(new Random().nextInt()) % len;
					while (nextRand != rand) {
						nextRand = Math.abs(new Random().nextInt()) % len;
					}
					rand = nextRand;
				}
			} else {
				feed = tmp;
			}
			j++;
		}
		if (feed == null) {
			feed = originalItems.get(rand0);
		}
		return feed;
	}

	@Override
	public Map<String, List<JRssFeed>> getFinalSelection() {
		Iterator<String> itr = randomFeeds.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			List<JRssFeed> list = randomFeeds.get(key);
			list = randomizeSelection(list, 20, false);
		}
		return randomFeeds;
	}
}