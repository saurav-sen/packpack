package com.pack.pack.services.redis;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.RSSFeed;
import com.pack.pack.model.RssFeedType;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.util.RssFeedUtil;
import com.pack.pack.util.StringUtils;
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
public class RssFeedRepositoryService {

	private RedisClient client;
	private StatefulRedisConnection<String, String> connection;

	private RedisCommands<String, String> sync;

	private static final Logger $_LOG = LoggerFactory
			.getLogger(RssFeedRepositoryService.class);

	private static final String RECENT_FEED_KEY = "RECENT_FEED";
	
	private static final String SET_KEY_PREFIX = "SET_";

	private static final String LATEST_SCORE = "LATEST_SCORE";

	@PostConstruct
	private void init() {
		if (System.getProperty("service.registry.test") != null) {
			return;
		}
		client = RedisClient.create(SystemPropertyUtil.getRedisURI());
		connection = client.connect();
	}

	public void dispose() {
		if (connection != null && connection.isOpen()) {
			connection.close();
		}
	}

	private StatefulRedisConnection<String, String> getConnection() {
		if (connection == null || !connection.isOpen()) {
			connection = client.connect();
		}
		return connection;
	}

	private RedisCommands<String, String> getSyncRedisCommands() {
		// return getConnection().sync();
		if (sync != null && sync.isOpen()) {
			return sync;
		}
		sync = getConnection().sync();
		return sync;
	}

	public boolean checkFeedExists(JRssFeed feed)
			throws NoSuchAlgorithmException, PackPackException {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		// String key = "Feeds_" + String.valueOf(feed.getOgUrl().hashCode());
		String key = RssFeedUtil.generateUploadKey(feed);
		List<String> list = sync.keys(key);
		if (list == null || list.isEmpty())
			return false;
		String tmpKey = list.get(0);
		String tmpJson = sync.get(tmpKey);
		if (tmpJson == null || tmpJson.trim().isEmpty())
			return false;
		RSSFeed tmpFeed = JSONUtil.deserialize(tmpJson, RSSFeed.class, true);
		if (tmpFeed == null)
			return false;
		String tmpOgUrl = tmpFeed.getOgUrl();
		if (tmpOgUrl == null)
			return false;
		return tmpOgUrl.equals(feed.getOgUrl());
	}

	public void uploadRefreshmentFeed(RSSFeed feed, TTL ttl)
			throws PackPackException, NoSuchAlgorithmException {
		$_LOG.info("Uploading Feed for Refreshment");
		$_LOG.info("Uploading " + feed.getOgType() + " Feed @ "
				+ feed.getOgUrl());
		$_LOG.info("Uploading Feed Titled :: " + feed.getOgTitle());
		String json = JSONUtil.serialize(feed);
		RedisCommands<String, String> sync = getSyncRedisCommands();
		long ttlSeconds = resolveTTL_InSeconds(ttl);
		String key = RssFeedUtil.generateUploadKey(feed);
		sync.setex(key, ttlSeconds, json);
		$_LOG.info("Successfully uploaded Refreshment Feed");
		// sync.close();
	}

	private long resolveTTL_InSeconds(TTL ttl) {
		long ttlSeconds = ttl.getTime();
		TimeUnit unit = ttl.getUnit();
		switch (unit) {
		case NANOSECONDS:
			ttlSeconds = ttlSeconds / (1000 * 1000 * 1000);
			break;
		case MICROSECONDS:
			ttlSeconds = ttlSeconds / (1000 * 1000);
			break;
		case MILLISECONDS:
			ttlSeconds = ttlSeconds / 1000;
			break;
		case SECONDS:
			break;
		case MINUTES:
			ttlSeconds = ttlSeconds * 60;
			break;
		case HOURS:
			ttlSeconds = ttlSeconds * 60 * 60;
			break;
		case DAYS:
			ttlSeconds = ttlSeconds * 24 * 60 * 60;
			break;
		}
		return ttlSeconds;
	}

	private long[] resolveRangeScores(String[] scores, int pageNo) {
		if (scores == null || scores.length == 0) { // NO_RESULT (Empty
													// Responses/Feeds)
			return new long[0];
		}

		if (pageNo < 0) {
			return new long[0];
		}
		int len = scores.length;
		if (pageNo >= len) {
			return new long[0];
		}

		long[] result = new long[1];
		result[0] = Long.parseLong(scores[pageNo].trim());
		//result[1] = Long.parseLong(scores[i].trim());
		return result;
	}

	private void updateLatestScoreForNewsFeed(
			RedisCommands<String, String> sync, RSSFeed feed, long batchId)
			throws PackPackException, NoSuchAlgorithmException {
		String setKey = SET_KEY_PREFIX + RssFeedUtil.resolvePrefix(feed)
				+ LATEST_SCORE;
		String value = sync.get(setKey);
		if (value == null) {
			value = "";
		}
		String[] split = value.split(";");
		Set<Long> sortedSet = new TreeSet<Long>(new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				long r = o2 - o1;
				return r == 0 ? 0 : (r > 0 ? 1 : -1);
			}
		});
		for (String s : split) {
			if (s == null || s.trim().isEmpty())
				continue;
			sortedSet.add(Long.parseLong(s));
		}
		sortedSet.add(batchId);
		StringBuilder stringBuilder = new StringBuilder();
		Iterator<Long> itr = sortedSet.iterator();
		while (itr.hasNext()) {
			Long v = itr.next();
			stringBuilder.append(v);
			if (itr.hasNext()) {
				stringBuilder.append(";");
			}
		}
		sync.set(setKey, stringBuilder.toString());
	}
	
	public boolean updateFeed(String key, RSSFeed feed) throws PackPackException {
		if(key == null || key.trim().isEmpty())
			return false;
		RedisCommands<String, String> sync = getSyncRedisCommands();
		List<String> list = sync.keys(key);
		if (list != null && !list.isEmpty()) {
			long ttlSeconds = sync.ttl(key);
			feed.setId(key);
			String json = JSONUtil.serialize(feed);
			sync.setex(key, ttlSeconds, json);
			return true;
		}
		return false;
	}
	
	public Set<String> uploadNewsFeed(List<RSSFeed> feeds, TTL ttl, long batchId,
			boolean updatePaginationInfo) throws PackPackException,
			NoSuchAlgorithmException {
		Set<String> allKeys = new HashSet<String>();
		StringBuilder newsKeys = new StringBuilder();
		StringBuilder sportsKeys = new StringBuilder();
		StringBuilder opinionKeys = new StringBuilder();
		StringBuilder scienceAndTechnologyKeys = new StringBuilder();
		StringBuilder articleKeys = new StringBuilder();
		for(RSSFeed feed : feeds) {
			String key = uploadNewsFeed(feed, ttl, batchId, updatePaginationInfo);
			if(updatePaginationInfo && key != null) {
				if(RssFeedType.NEWS.name().equalsIgnoreCase(feed.getFeedType())) {
					newsKeys.append(key);
					newsKeys.append(";");
				} else if(RssFeedType.NEWS_SPORTS.name().equalsIgnoreCase(feed.getFeedType())) {
					sportsKeys.append(key);
					sportsKeys.append(";");
				} else if(RssFeedType.NEWS_SCIENCE_TECHNOLOGY.name().equalsIgnoreCase(feed.getFeedType())) {
					scienceAndTechnologyKeys.append(key);
					scienceAndTechnologyKeys.append(";");
				} else if(RssFeedType.ARTICLE.name().equalsIgnoreCase(feed.getFeedType())) {
					articleKeys.append(key);
					articleKeys.append(";");
				} else if(RssFeedType.OPINION.name().equalsIgnoreCase(feed.getFeedType())) {
					opinionKeys.append(key);
					opinionKeys.append(";");
				}
				allKeys.add(key);
			}
		}
		
		long ttlSeconds = 30 * 60 * 60;// * 1000; // 30 Hours
		RedisCommands<String, String> sync = getSyncRedisCommands();
		String setKey = null;
		if (!newsKeys.toString().isEmpty()) {
			setKey = SET_KEY_PREFIX
					+ RssFeedUtil.resolvePrefix(RssFeedType.NEWS.name())
					+ batchId;
			sync.setex(setKey, ttlSeconds, newsKeys.toString());
		}
		
		if (!sportsKeys.toString().isEmpty()) {
			setKey = SET_KEY_PREFIX
					+ RssFeedUtil.resolvePrefix(RssFeedType.NEWS_SPORTS.name())
					+ batchId;
			sync.setex(setKey, ttlSeconds, sportsKeys.toString());
		}
		
		if (!opinionKeys.toString().isEmpty()) {
			setKey = SET_KEY_PREFIX
					+ RssFeedUtil.resolvePrefix(RssFeedType.OPINION.name())
					+ batchId;
			sync.setex(setKey, ttlSeconds, opinionKeys.toString());
		}

		if (!scienceAndTechnologyKeys.toString().isEmpty()) {
			setKey = SET_KEY_PREFIX
					+ RssFeedUtil
							.resolvePrefix(RssFeedType.NEWS_SCIENCE_TECHNOLOGY
									.name()) + batchId;
			sync.setex(setKey, ttlSeconds, scienceAndTechnologyKeys.toString());
		}

		if (!articleKeys.toString().isEmpty()) {
			setKey = SET_KEY_PREFIX
					+ RssFeedUtil.resolvePrefix(RssFeedType.ARTICLE.name())
					+ batchId;
			sync.setex(setKey, ttlSeconds, articleKeys.toString());
		}
		return allKeys;
	}
	
	private Set<String> resolveKeysForPagination(
			RedisCommands<String, String> sync, long batchId, String rangeKey) {
		String keys = sync.get(rangeKey + batchId);
		if (keys == null)
			return Collections.emptySet();
		return new HashSet<String>(Arrays.asList(keys.split(";")));
	}
	
	public String provisionFeed(RSSFeed feed, TTL ttl) throws PackPackException,
			NoSuchAlgorithmException {
		if (ttl == null) {
			ttl = new TTL();
			ttl.setTime((short) 1);
			ttl.setUnit(TimeUnit.DAYS);
		}
		String feedType = feed.getFeedType().toUpperCase();
		String LOG_TAG = "[" + feedType + "] ";
		$_LOG.info(LOG_TAG + " Provisioning " + feedType + " Feed @ "
				+ feed.getOgUrl());
		$_LOG.info(LOG_TAG + " Provisioning Feed Titled :: " + feed.getOgTitle());
		$_LOG.info(LOG_TAG + " Provisioning Feed Link :: " + feed.getOgUrl());
		RedisCommands<String, String> sync = getSyncRedisCommands();
		long ttlSeconds = resolveTTL_InSeconds(ttl);
		//String key = "LL_" + feed.getOgUrl();
		String key = feed.getId();

		String json = sync.get(key);
		if (json == null)
			return null;
		
		RSSFeed tmpFeed = JSONUtil.deserialize(json, RSSFeed.class, true);
		Long ttl2 = sync.ttl(key);
		tmpFeed.setFeedType(feed.getFeedType());
		json = JSONUtil.serialize(tmpFeed);
		sync.setex(key, ttl2, json);
		
		json = "";

		// Avoid duplicate HashValues here (Only for new entries)
		String tmpKey = RssFeedUtil.generateUploadKey(tmpFeed);
		String tmpJson = null;
		boolean isAlreadyExists = false;
		int i = 0;
		do {
			$_LOG.debug(LOG_TAG + " feed.getOgUrl() = " + feed.getOgUrl());
			tmpJson = sync.get(tmpKey);
			$_LOG.debug(LOG_TAG + " tmpKey = " + tmpKey);
			$_LOG.debug(LOG_TAG + " tmpJson = " + tmpJson);
			if (tmpJson != null) {
				tmpFeed = JSONUtil.deserialize(tmpJson, RSSFeed.class, true);
				$_LOG.debug(LOG_TAG + " tmpFeed.getOgUrl() = "
						+ tmpFeed.getOgUrl());
				tmpKey = new StringBuilder(key).append(i).toString();
				$_LOG.debug(LOG_TAG + " tmpKey = " + tmpKey);
				isAlreadyExists = feed.getOgUrl().equals(tmpFeed.getOgUrl());
				i++;
			}
		} while (tmpJson != null && tmpFeed != null && !isAlreadyExists);
		key = tmpKey;
		
		feed.setId(key);
		json = JSONUtil.serialize(feed);
		sync.setex(key, ttlSeconds, json);
		$_LOG.info(LOG_TAG + "Successfully provisioned " + feedType + " Feed");
		return key;
	}
	
	public String uploadUnprovisionedFeed(RSSFeed feed, TTL ttl) throws PackPackException,
			NoSuchAlgorithmException {
		if(ttl == null) {
			ttl = new TTL();
			ttl.setTime((short)1);
			ttl.setUnit(TimeUnit.DAYS);
		}
		feed.setFeedType(RssFeedType.UNPROVISIONED.name());
		String feedType = feed.getFeedType().toUpperCase();
		String LOG_TAG = "[" + feedType + "] ";
		$_LOG.info(LOG_TAG + " Uploading " + feedType + " Feed @ " + feed.getOgUrl());
		$_LOG.info(LOG_TAG + " Uploading Feed Titled :: " + feed.getOgTitle());
		RedisCommands<String, String> sync = getSyncRedisCommands();
		long ttlSeconds = resolveTTL_InSeconds(ttl);
		String key = "LL_" + feed.getOgUrl();
		
		String json = sync.get(key);
		if(json != null)
			return key;

		feed.setId(key);
		json = JSONUtil.serialize(feed);
		sync.setex(key, ttlSeconds, json);
		$_LOG.info(LOG_TAG + "Successfully uploaded " + feedType + " Feed");
		return key;
	}

	private String uploadNewsFeed(RSSFeed feed, TTL ttl, long batchId,
			boolean updatePaginationInfo) throws PackPackException,
			NoSuchAlgorithmException {
		String feedType = feed.getFeedType().toUpperCase();
		$_LOG.info("Uploading Feed for " + feedType);
		$_LOG.info("Uploading " + feedType + " Feed @ " + feed.getOgUrl());
		$_LOG.info("Uploading Feed Titled :: " + feed.getOgTitle());
		RedisCommands<String, String> sync = getSyncRedisCommands();
		long ttlSeconds = resolveTTL_InSeconds(ttl);
		String key = RssFeedUtil.generateUploadKey(feed);

		// Avoid duplicate HashValues here (Only for new entries)
		if (updatePaginationInfo) {
			String tmpKey = key;
			String tmpJson = null;
			RSSFeed tmpFeed = null;
			int i = 0;
			do {
				$_LOG.debug("feed.getOgUrl() = " + feed.getOgUrl());
				tmpJson = sync.get(tmpKey);
				$_LOG.debug("tmpKey = " + tmpKey);
				$_LOG.debug("tmpJson = " + tmpJson);
				if (tmpJson != null) {
					tmpFeed = JSONUtil
							.deserialize(tmpJson, RSSFeed.class, true);
					$_LOG.debug("tmpFeed.getOgUrl() = " + tmpFeed.getOgUrl());
					tmpKey = new StringBuilder(key).append(i).toString();
					$_LOG.debug("tmpKey = " + tmpKey);
					i++;
				}
			} while (tmpJson != null && tmpFeed != null
					&& feed.getOgUrl().equals(tmpFeed.getOgUrl()));
			key = tmpKey;
		}

		if (!updatePaginationInfo) {
			ttlSeconds = sync.ttl(key);
		}
		feed.setId(key);
		String json = JSONUtil.serialize(feed);
		sync.setex(key, ttlSeconds, json);
		if (updatePaginationInfo) {
			$_LOG.info("Updating latest score");
			updateLatestScoreForNewsFeed(sync, feed, batchId);
		}
		$_LOG.info("Successfully uploaded " + feedType + " Feed");
		return key;
	}

	public List<RSSFeed> getAllRefrehmentFeeds() throws PackPackException {
		return getAllFeeds(RssFeedUtil.resolvePrefix(JRssFeedType.REFRESHMENT
				.name()) + "*");
	}

	private List<RSSFeed> getAllFeeds(String keyPattern)
			throws PackPackException {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		List<String> keys = sync.keys(keyPattern);
		if (keys == null || keys.isEmpty())
			return Collections.emptyList();
		List<RSSFeed> feeds = new LinkedList<RSSFeed>();
		for (String key : keys) {
			String json = sync.get(key);
			RSSFeed feed = JSONUtil.deserialize(json, RSSFeed.class, true);
			feeds.add(feed);
		}
		// sync.close();
		return feeds;
	}

	public Pagination<RSSFeed> getNewsFeeds(int pageNo)
			throws PackPackException {
		$_LOG.trace("Getting News Feeds");
		return getAllUpdatedFeeds(
				RssFeedUtil.resolvePrefix(JRssFeedType.NEWS.name()), pageNo);
	}

	public Pagination<RSSFeed> getSportsNewsFeeds(int pageNo)
			throws PackPackException {
		$_LOG.trace("Getting Sports News Feeds");
		return getAllUpdatedFeeds(
				RssFeedUtil.resolvePrefix(JRssFeedType.NEWS_SPORTS.name()),
				pageNo);
	}
	
	public Pagination<RSSFeed> getOpinionFeeds(int pageNo)
			throws PackPackException {
		$_LOG.trace("Getting Opinion Feeds");
		return getAllUpdatedFeeds(
				RssFeedUtil.resolvePrefix(JRssFeedType.OPINION.name()),
				pageNo);
	}

	public Pagination<RSSFeed> getScienceAndTechnologyNewsFeeds(int pageNo)
			throws PackPackException {
		$_LOG.trace("Getting Science & Technology News Feeds");
		return getAllUpdatedFeeds(
				RssFeedUtil.resolvePrefix(JRssFeedType.NEWS_SCIENCE_TECHNOLOGY
						.name()), pageNo);
	}

	public Pagination<RSSFeed> getArticleNewsFeeds(int pageNo)
			throws PackPackException {
		$_LOG.trace("Getting Article News Feeds");
		return getAllUpdatedFeeds(
				RssFeedUtil.resolvePrefix(JRssFeedType.ARTICLE.name()), pageNo);
	}

	public void cleanupRangeKeys() {
		cleanupRangeKeys0(SET_KEY_PREFIX
				+ RssFeedUtil.resolvePrefix(JRssFeedType.NEWS.name()));
		cleanupRangeKeys0(SET_KEY_PREFIX
				+ RssFeedUtil.resolvePrefix(JRssFeedType.NEWS_SPORTS.name()));
		cleanupRangeKeys0(SET_KEY_PREFIX
				+ RssFeedUtil.resolvePrefix(JRssFeedType.OPINION.name()));
		cleanupRangeKeys0(SET_KEY_PREFIX
				+ RssFeedUtil
						.resolvePrefix(JRssFeedType.NEWS_SCIENCE_TECHNOLOGY
								.name()));
		cleanupRangeKeys0(SET_KEY_PREFIX
				+ RssFeedUtil.resolvePrefix(JRssFeedType.ARTICLE.name()));
	}

	private void cleanupRangeKeys0(String setKey) {
		$_LOG.trace("setKey = " + setKey);
		RedisCommands<String, String> sync = getSyncRedisCommands();

		String rangeKey = setKey + LATEST_SCORE;
		String ranges = sync.get(rangeKey);
		if (ranges != null && !ranges.trim().isEmpty()) {
			ranges = removeExpiredRanges(ranges);
			if (ranges != null && !ranges.trim().isEmpty()) {
				sync.set(rangeKey, ranges);
			}
		}
	}

	private String removeExpiredRanges(String rangesValue) {
		String[] ranges = rangesValue.split(";");
		Set<Long> set = new TreeSet<Long>(new Comparator<Long>() {

			@Override
			public int compare(Long o1, Long o2) {
				long r = o2 - o1;
				return r == 0 ? 0 : (r > 0 ? 1 : -1);
			}
		});
		for (String range : ranges) {
			if (range == null || range.trim().isEmpty())
				continue;
			set.add(Long.parseLong(range.trim()));
		}
		long first = -1;
		long diff = 50 * 60 * 60 * 1000;
		Iterator<Long> itr = set.iterator();
		while (itr.hasNext()) {
			Long v = itr.next();
			if (first < 0) {
				first = v;
			} else if (first - v > diff) {
				itr.remove();
			}
		}
		StringBuilder finalRangesValue = new StringBuilder();
		itr = set.iterator();
		while (itr.hasNext()) {
			finalRangesValue.append(itr.next());
			if (itr.hasNext()) {
				finalRangesValue.append(";");
			}
		}
		return finalRangesValue.toString();
	}
	
	public RSSFeed getFeedByKey(String key) throws PackPackException {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		String json = sync.get(key);
		return JSONUtil.deserialize(json, RSSFeed.class, true);
	}
	
	public RSSFeed deleteFeedByKey(String key) throws PackPackException {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		String json = sync.get(key);
		if(json == null)
			return null;
		RSSFeed rssFeed = JSONUtil.deserialize(json, RSSFeed.class, true);
		if(rssFeed == null)
			return null;
		sync.del(key);
		return rssFeed;
	}

	public Pagination<RSSFeed> getAllFeedsInStore(String keyPattern)
			throws PackPackException {
		List<RSSFeed> feeds = new ArrayList<RSSFeed>();
		Pagination<RSSFeed> page = new Pagination<RSSFeed>();
		page.setNextPageNo(-1);
		RedisCommands<String, String> sync = getSyncRedisCommands();
		List<String> keys = sync.keys(keyPattern);
		for (String key : keys) {
			String json = sync.get(key);
			if (json == null) {
				// sync.del(key);
				continue;
			}
			RSSFeed feed = JSONUtil.deserialize(json, RSSFeed.class, true);
			feeds.add(feed);
		}
		page.setResult(feeds);
		return page;
	}

	private Pagination<RSSFeed> getAllUpdatedFeeds(String keyPattern, int pageNo)
			throws PackPackException {
		$_LOG.trace("Key Pattern = " + keyPattern);
		Pagination<RSSFeed> page = new Pagination<RSSFeed>();
		List<RSSFeed> feeds = new ArrayList<RSSFeed>();
		if (pageNo < 0) {
			page.setResult(feeds);
			page.setNextPageNo(-1);
			return page;
		}

		RedisCommands<String, String> sync = getSyncRedisCommands();
		String setKey = SET_KEY_PREFIX + keyPattern.toUpperCase();
		String rangeKey = setKey + LATEST_SCORE;
		$_LOG.trace("Range Key = " + rangeKey);
		String ranges = sync.get(rangeKey);
		$_LOG.trace("ranges = " + ranges);
		if (ranges == null) {
			page.setResult(feeds);
			page.setNextPageNo(-1);
			return page;
		}
		String[] split = ranges.split(";");
		if (split.length == 0) {
			page.setResult(feeds);
			page.setNextPageNo(-1);
			return page;
		}

		Set<String> keys = null;
		long[] scores = resolveRangeScores(split, pageNo);
		$_LOG.debug("Scores = " + StringUtils.stringify(scores));
		if (scores.length == 0)
			return endOfPageResponse(pageNo);
		long batchId = scores[0];
		$_LOG.trace("setKey = " + setKey);
		int i = pageNo + 1;
		keys = resolveKeysForPagination(sync, batchId, setKey);
		while (keys.isEmpty() && scores.length > 0) {
			$_LOG.trace("Keys = " + StringUtils.stringify(keys));
			scores = resolveRangeScores(split, i);
			batchId = scores[0];
			keys = resolveKeysForPagination(sync, batchId, setKey);
			i++;
		}
		
		$_LOG.debug("No of keys = " + keys.size());

		if (scores.length == 0) {
			page = new Pagination<RSSFeed>();
			page.setResult(Collections.emptyList());
			page.setNextPageNo(-1);
			return page;
		} else {
			for (String key : keys) {
				String json = sync.get(key);
				if (json == null) {
					continue;
				}
				RSSFeed feed = JSONUtil.deserialize(json, RSSFeed.class, true);
				feed.setId(key);
				feeds.add(feed);
			}
			page = new Pagination<RSSFeed>();
			$_LOG.info("Size of Feeds = " + feeds.size());
			page.setResult(feeds);
			page.setNextPageNo(i);
		}
		return page;
	}

	private Pagination<RSSFeed> endOfPageResponse(int pageNo) {
		Pagination<RSSFeed> page = new Pagination<RSSFeed>();
		page.setNextPageNo(-1);
		return page;
	}
	
	public void storeRecentFeedIds(Set<String> recentFeedIds) throws PackPackException {
		if(recentFeedIds == null || recentFeedIds.isEmpty())
			return;
		RedisCommands<String, String> sync = getSyncRedisCommands();
		StringBuilder value = new StringBuilder();
		for(String recentFeedId : recentFeedIds) {
			value.append(recentFeedId);
			value.append(";");
		}
		long ttlSeconds = 2 * 60 * 60;
		sync.setex(RECENT_FEED_KEY, ttlSeconds, value.toString());
	}
	
	public Set<String> getRecentFeedIds() throws PackPackException {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		String value = sync.get(RECENT_FEED_KEY);
		if(value == null)
			return Collections.emptySet();
		String[] arr = value.split(";");
		if(arr.length == 0)
			return Collections.emptySet();
		Set<String> recentFeedIds = new HashSet<String>();
		for(String a : arr) {
			recentFeedIds.add(a);
		}
		return recentFeedIds;
	}
	
	public List<RSSFeed> getRecentAutoUploadFeeds() throws PackPackException {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		String value = sync.get(RECENT_FEED_KEY);
		if(value == null)
			return Collections.emptyList();
		String[] arr = value.split(";");
		if(arr.length == 0)
			return Collections.emptyList();
		Set<String> recentFeedIds = new HashSet<String>();
		for(String a : arr) {
			recentFeedIds.add(a);
		}
		List<RSSFeed> feeds = new LinkedList<RSSFeed>();
		for (String recentFeedId : recentFeedIds) {
			String json = sync.get(recentFeedId);
			if (json == null) {
				continue;
			}
			RSSFeed feed = JSONUtil.deserialize(json, RSSFeed.class, true);
			feed.setId(recentFeedId);
			feeds.add(feed);
		}
		return feeds;
	}

	public void test() {
		String[] scores = new String[] { "1535778663195", "1535771463195",
				"1535764263195", "1535757063183", "1535757064222",
				"1535749863195", "1535742663195", "1535735463195",
				"1535728263195", "1535721063195", "1535713863195",
				"1535706663195", "1535699463195", "1535692263195",
				"1535685063195", "1535677863195", "1535670664518",
				"1535670664517", "1535663463195", "1535656263195",
				"1535649063195", "1535641863195", "1535634663195",
				"1535627463195", "1535620263195", "1535613063195",
				"1535605863195", "1535598663195", "1535591463195",
				"1535584263211", "1535566514641", "1535559314641",
				"1535552114659" };
		scores = new String[] { "10", "20", "30", "40", "50", "60", "70", "80",
				"90", "100", "110", "120", "130", "140", "150", "160", "170",
				"180", "190", "200", "210", "220", "230", "240", "250", "260",
				"270", "280", "290", "300", "310", "320", "330" };
		List<String> asList = Arrays.asList(scores);
		Collections.reverse(asList);
		scores = asList.toArray(new String[scores.length]);
		long[] ranges = new long[] { Long.parseLong(scores[2]) };
		for (int i = 0; i < ranges.length; i++) {
			ranges = resolveRangeScores(scores, i);
			System.out.println(StringUtils.stringify(ranges));
		}
	}

	public static void main(String[] args) {
		new RssFeedRepositoryService().test();
	}
}