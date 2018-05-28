package com.pack.pack.services.redis;

import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE;
import static com.pack.pack.common.util.CommonConstants.PAGELINK_DIRECTION_POSITIVE;
import static com.pack.pack.common.util.CommonConstants.PAGELINK_DIRECTION_NEGATIVE;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.exception.PackPackException;
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
public class RssFeedRepositoryService {

	private RedisClient client;
	private StatefulRedisConnection<String, String> connection;

	private RedisCommands<String, String> sync;

	private static final Logger LOG = LoggerFactory
			.getLogger(RssFeedRepositoryService.class);
	
	private static final String SET_KEY_PREFIX = "SET_";
	
	private static final String LATEST_SCORE = "LATEST_SCORE";

	@PostConstruct
	private void init() {
		if(System.getProperty("service.registry.test") != null) {
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
	
	public boolean checkFeedExists(JRssFeed feed) throws NoSuchAlgorithmException {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		//String key = "Feeds_" + String.valueOf(feed.getOgUrl().hashCode());
		String key = RssFeedUtil.generateUploadKey(feed);
		List<String> list = sync.keys(key);
		if(list == null || list.isEmpty())
			return false;
		return true;
	}
	
	public void uploadRefreshmentFeed(RSSFeed feed, TTL ttl)
			throws PackPackException, NoSuchAlgorithmException {
		LOG.info("Uploading Feed for Refreshment");
		LOG.info("Uploading " + feed.getOgType() + " Feed @ " + feed.getOgUrl());
		LOG.info("Uploading Feed Titled :: " + feed.getOgTitle());
		String json = JSONUtil.serialize(feed);
		RedisCommands<String, String> sync = getSyncRedisCommands();
		long ttlSeconds = resolveTTL_InSeconds(ttl);
		String key = RssFeedUtil.generateUploadKey(feed);
		sync.setex(key, ttlSeconds, json);
		LOG.info("Successfully uploaded Refreshment Feed");
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
	
	private long[] resolveRangeScores(String[] scores, long timestamp, int direction) {
		if(scores == null || scores.length == 0)
			return new long[0];
		long[] result = new long[2];
		if(timestamp == 0) {
			if(scores.length > 1) {
				result[0] = Long.parseLong(scores[1]);
				result[1] = Long.parseLong(scores[0]);
			} else {
				result[0] = Long.parseLong(scores[0]);
				result[1] = result[0];
			}
			return result;
		}
		boolean found = false;
		if(direction < 0) {
			for(int i=0; i<scores.length-1; i++) {
				if(timestamp == Long.parseLong(scores[i])) {
					i++;
					result[0] = Long.parseLong(scores[i]);
					result[1] = timestamp;
					found = true;
				}
			}
		} else {
			result[0] = timestamp;
			result[1] = Long.parseLong(scores[0]);
		}
		if(!found)
			result = new long[0];
		return result;
	}
	
	private void updateLatestScoreForNewsFeed(
			RedisCommands<String, String> sync, RSSFeed feed, long batchId)
			throws PackPackException, NoSuchAlgorithmException {
		String setKey = SET_KEY_PREFIX + RssFeedUtil.resolvePrefix(feed)
				+ LATEST_SCORE;
		String value = sync.get(setKey);
		if(value == null) {
			value = "";
		}
		String nValue = String.valueOf(batchId);
		if(!value.contains(nValue)) {
			value = nValue + ";" + value;
			sync.set(setKey, value);
		}
	}

	public void uploadNewsFeed(RSSFeed feed, TTL ttl, long batchId, boolean updatePaginationInfo)
			throws PackPackException, NoSuchAlgorithmException {
		String feedType = feed.getFeedType().toUpperCase();
		LOG.info("Uploading Feed for " + feedType);
		LOG.info("Uploading " + feedType + " Feed @ " + feed.getOgUrl());
		LOG.info("Uploading Feed Titled :: " + feed.getOgTitle());
		String json = JSONUtil.serialize(feed);
		RedisCommands<String, String> sync = getSyncRedisCommands();
		long ttlSeconds = resolveTTL_InSeconds(ttl);
		String key = RssFeedUtil.generateUploadKey(feed);
		if(updatePaginationInfo) {
			String setKey = SET_KEY_PREFIX + RssFeedUtil.resolvePrefix(feed);
			sync.zadd(setKey, batchId, key);
		}
		sync.setex(key, ttlSeconds, json);
		if(updatePaginationInfo) {
			updateLatestScoreForNewsFeed(sync, feed, batchId);
		}
		LOG.info("Successfully uploaded " + feedType + " Feed");
	}
	
	public List<RSSFeed> getAllRefrehmentFeeds() throws PackPackException {
		return getAllFeeds(RssFeedUtil.resolvePrefix(JRssFeedType.REFRESHMENT.name()) + "*");
	}
	
	private List<RSSFeed> getAllFeeds(String keyPattern) throws PackPackException {
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
	
	public Pagination<RSSFeed> getNewsFeeds(long timestamp, int direction) throws PackPackException {
		return getAllUpdatedFeeds(RssFeedUtil.resolvePrefix(JRssFeedType.NEWS.name()) + "*", timestamp, direction);
	}
	
	public Pagination<RSSFeed> getSportsNewsFeeds(long timestamp, int direction) throws PackPackException {
		return getAllUpdatedFeeds(RssFeedUtil.resolvePrefix(JRssFeedType.NEWS_SPORTS.name()) + "*", timestamp, direction);
	}
	
	public Pagination<RSSFeed> getScienceAndTechnologyNewsFeeds(long timestamp, int direction) throws PackPackException {
		return getAllUpdatedFeeds(RssFeedUtil.resolvePrefix(JRssFeedType.NEWS_SCIENCE_TECHNOLOGY.name()) + "*", timestamp, direction);
	}
	
	public Pagination<RSSFeed> getArticleNewsFeeds(long timestamp, int direction) throws PackPackException {
		return getAllUpdatedFeeds(RssFeedUtil.resolvePrefix(JRssFeedType.ARTICLE.name()) + "*", timestamp, direction);
	}
	
	private Pagination<RSSFeed> getAllUpdatedFeeds(String keyPattern, long timestamp, int direction) throws PackPackException {
		Pagination<RSSFeed> page = new Pagination<RSSFeed>();
		List<RSSFeed> feeds = new ArrayList<RSSFeed>();
		if(timestamp < 0) {
			feeds = getAllFeeds(keyPattern);
			page.setResult(feeds);
			page.setNextLink(END_OF_PAGE + PAGELINK_DIRECTION_POSITIVE);
			page.setPreviousLink(END_OF_PAGE + PAGELINK_DIRECTION_NEGATIVE);
			return page;
		}
		
		RedisCommands<String, String> sync = getSyncRedisCommands();
		String setKey = SET_KEY_PREFIX + keyPattern.toUpperCase();
		String rangeKey = setKey + LATEST_SCORE;
		String ranges = sync.get(rangeKey);
		String[] split = ranges.split(";");
		
		List<String> keys = null;
		long[] scores = resolveRangeScores(split, timestamp, direction);
		if(scores.length == 0)
			return endOfPageResponse(timestamp);
		long r1 = scores[0];
		long r2 = scores[1];
		keys = sync.zrange(setKey, r1, r2);
		
		if (keys == null || keys.isEmpty()) { // This means all the keys got expired due to TTL (And have been removed earlier or during auto sync, but ranges/scores NOT updated accordingly)
			removeAllExpiredRanges(rangeKey, split, timestamp);
			return endOfPageResponse(timestamp);
		} else {
			for (String key : keys) {
				String json = sync.get(key);
				if(json == null) {
					sync.zrem(setKey, key);
					continue;
				}
				RSSFeed feed = JSONUtil.deserialize(json, RSSFeed.class, true);
				feeds.add(feed);
			}
			if(feeds.isEmpty()) { // This means all the keys got expired due to TTL
				removeAllExpiredRanges(rangeKey, split, timestamp);
				return endOfPageResponse(timestamp);
			} else {
				page.setNextLink(String.valueOf(r2) + PAGELINK_DIRECTION_POSITIVE);
				/*while(feeds.size() < 10) {
					Pagination<RSSFeed> nextPage = getAllUpdatedFeeds(keyPattern, r1);
					feeds.addAll(nextPage.getResult());
					page.setNextLink(nextPage.getNextLink());
				}*/
			}
		}
		
		if(timestamp == 0) {
			page.setPreviousLink(END_OF_PAGE + PAGELINK_DIRECTION_NEGATIVE);
		} else if(timestamp == Long.MAX_VALUE) {
			page.setPreviousLink(END_OF_PAGE + PAGELINK_DIRECTION_NEGATIVE);
		} else {
			page.setPreviousLink(String.valueOf(timestamp) + PAGELINK_DIRECTION_NEGATIVE);
		}
		
		page.setResult(feeds);
		
		return page;
	}
	
	private Pagination<RSSFeed> endOfPageResponse(long previousTimestamp) {
		Pagination<RSSFeed> page = new Pagination<RSSFeed>();
		page.setNextLink(END_OF_PAGE + PAGELINK_DIRECTION_POSITIVE);
		page.setPreviousLink(String.valueOf(previousTimestamp) + PAGELINK_DIRECTION_NEGATIVE);
		page.setResult(Collections.emptyList());
		return page;
	}
	
	private void removeAllExpiredRanges(String rangeKey, String[] rangesArr, long timestamp) {
		int i = 0;
		RedisCommands<String, String> sync = getSyncRedisCommands();
		StringBuilder updatedRanges = new StringBuilder();
		while(Long.parseLong(rangesArr[i]) != timestamp) {
			updatedRanges.append(rangesArr[i]);
			updatedRanges.append(";");
			i++;
		}
		updatedRanges.append(String.valueOf(timestamp));
		String ranges = updatedRanges.toString();
		sync.set(rangeKey, ranges);
	}
	
	/*private void removeAllExpiredRanges(String keyPattern, long timestamp) {
		String setKey = SET_KEY_PREFIX + keyPattern.toUpperCase();
		String rangeKey = setKey + LATEST_SCORE;
		String ranges = sync.get(rangeKey);
		String[] rangesArr = ranges.split(";");
		removeAllExpiredRanges(rangeKey, rangesArr, timestamp);
	}*/
}