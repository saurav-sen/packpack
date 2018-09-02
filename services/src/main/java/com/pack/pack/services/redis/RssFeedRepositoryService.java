package com.pack.pack.services.redis;

import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE;
import static com.pack.pack.common.util.CommonConstants.END_OF_PAGE_TIMESTAMP;
import static com.pack.pack.common.util.CommonConstants.PAGELINK_DIRECTION_NEGATIVE;
import static com.pack.pack.common.util.CommonConstants.PAGELINK_DIRECTION_POSITIVE;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import com.lambdaworks.redis.ScoredValue;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.RSSFeed;
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
		$_LOG.info("Uploading Feed for Refreshment");
		$_LOG.info("Uploading " + feed.getOgType() + " Feed @ " + feed.getOgUrl());
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
	
	private long[] resolveRangeScores(String[] scores, long timestamp,
			int direction) {
		if (scores == null || scores.length == 0) { // NO_RESULT (Empty
													// Responses/Feeds)
			return new long[0];
		}

		long[] result = new long[2];
		int len = scores.length;

		short d0 = 1;
		if (direction < 0) {
			d0 = -1;
		}

		if (timestamp == 0) {
			if (d0 == -1) { // END_OF_PAGE (PreviousLink direction)
				return new long[0];
			}
			if (len > 2) {
				result[0] = Long.parseLong(scores[2]);
				result[1] = Long.parseLong(scores[0]);
			} else if (len > 1) {
				result[0] = Long.parseLong(scores[1]);
				result[1] = Long.parseLong(scores[0]);
			} else {
				result[0] = Long.parseLong(scores[0]);
				result[1] = Long.MAX_VALUE;
				//result[1] = result[0];
			}
			return result;
		}

		int i = 0;
		long t0 = timestamp * d0;
		int j = i;
		int k = j;
		while (i < len && t0 <= (Long.parseLong(scores[i]) * d0)) {
			k = j;
			j = i;
			i++;
		}

		if ((i == len && d0 == 1) || (j == 0 && d0 == -1)) { // END_OF_PAGE
																// (PreviousLink
																// OR NextLink
																// direction)
			result = new long[0];
		} else if (d0 == 1) {
			int p = i + 1;
			if (p < len) {
				j = p;
			} else {
				j = i;
			}
			i = i - 1;
			if(i < 0) {
				result = new long[0];
			} else {
				result[0] = Long.parseLong(scores[j]);
				result[1] = Long.parseLong(scores[i]);
			}
			// result[1] = timestamp;
		} else {
			result[0] = Long.parseLong(scores[j]);
			int q = k - 1;
			if (q >= 0) {
				k = q;
			}
			result[1] = Long.parseLong(scores[k]);
		}
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
		String[] split = value.split(";");
		Set<Long> sortedSet = new TreeSet<Long>(new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				long r = o2 - o1;
				return r == 0 ? 0 : (r > 0 ? 1 : -1);
			}
		});
		for(String s : split) {
			if(s == null || s.trim().isEmpty())
				continue;
			sortedSet.add(Long.parseLong(s));
		}
		sortedSet.add(batchId);
		StringBuilder stringBuilder = new StringBuilder();
		Iterator<Long> itr = sortedSet.iterator();
		while(itr.hasNext()) {
			Long v = itr.next();
			stringBuilder.append(v);
			if(itr.hasNext()) {
				stringBuilder.append(";");
			}
		}
		sync.set(setKey, stringBuilder.toString());
		/*String nValue = String.valueOf(batchId);
		if(!value.contains(nValue)) {
			value = nValue + ";" + value;
			sync.set(setKey, value);
		}*/
	}

	public void uploadNewsFeed(RSSFeed feed, TTL ttl, long batchId, boolean updatePaginationInfo)
			throws PackPackException, NoSuchAlgorithmException {
		String feedType = feed.getFeedType().toUpperCase();
		$_LOG.info("Uploading Feed for " + feedType);
		$_LOG.info("Uploading " + feedType + " Feed @ " + feed.getOgUrl());
		$_LOG.info("Uploading Feed Titled :: " + feed.getOgTitle());
		RedisCommands<String, String> sync = getSyncRedisCommands();
		long ttlSeconds = resolveTTL_InSeconds(ttl);
		String key = RssFeedUtil.generateUploadKey(feed);
		feed.setId(key);
		String json = JSONUtil.serialize(feed);
		if(updatePaginationInfo) {
			$_LOG.info("Updating pagination infomation");
			String setKey = SET_KEY_PREFIX + RssFeedUtil.resolvePrefix(feed);
			sync.zadd(setKey, batchId, key);
		}
		sync.setex(key, ttlSeconds, json);
		if(updatePaginationInfo) {
			$_LOG.info("Updating latest score");
			updateLatestScoreForNewsFeed(sync, feed, batchId);
		}
		$_LOG.info("Successfully uploaded " + feedType + " Feed");
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
		$_LOG.trace("Getting News Feeds");
		return getAllUpdatedFeeds(RssFeedUtil.resolvePrefix(JRssFeedType.NEWS.name()), timestamp, direction);
	}
	
	public Pagination<RSSFeed> getSportsNewsFeeds(long timestamp, int direction) throws PackPackException {
		$_LOG.trace("Getting Sports News Feeds");
		return getAllUpdatedFeeds(RssFeedUtil.resolvePrefix(JRssFeedType.NEWS_SPORTS.name()), timestamp, direction);
	}
	
	public Pagination<RSSFeed> getScienceAndTechnologyNewsFeeds(long timestamp, int direction) throws PackPackException {
		$_LOG.trace("Getting Science & Technology News Feeds");
		return getAllUpdatedFeeds(RssFeedUtil.resolvePrefix(JRssFeedType.NEWS_SCIENCE_TECHNOLOGY.name()), timestamp, direction);
	}
	
	public Pagination<RSSFeed> getArticleNewsFeeds(long timestamp, int direction) throws PackPackException {
		$_LOG.trace("Getting Article News Feeds");
		return getAllUpdatedFeeds(RssFeedUtil.resolvePrefix(JRssFeedType.ARTICLE.name()), timestamp, direction);
	}
	
	public void cleanupRangeKeys() {
		cleanupRangeKeys0(SET_KEY_PREFIX + RssFeedUtil.resolvePrefix(JRssFeedType.NEWS.name()));
		cleanupRangeKeys0(SET_KEY_PREFIX + RssFeedUtil.resolvePrefix(JRssFeedType.NEWS_SPORTS.name()));
		cleanupRangeKeys0(SET_KEY_PREFIX + RssFeedUtil.resolvePrefix(JRssFeedType.NEWS_SCIENCE_TECHNOLOGY.name()));
		cleanupRangeKeys0(SET_KEY_PREFIX + RssFeedUtil.resolvePrefix(JRssFeedType.ARTICLE.name()));
	}
	
	private void cleanupRangeKeys0(String setKey) {
		$_LOG.trace("setKey = " + setKey);
		RedisCommands<String, String> sync = getSyncRedisCommands();
		List<String> keys = resolveAllSetKeys(sync, setKey);
		if(keys == null || keys.isEmpty()) {
			$_LOG.debug("Could not find any key for setKey = " + setKey);
			return;
		}
		for (String key : keys) {
			String json = sync.get(key);
			if(json == null) {
				sync.zrem(setKey, key);
			}
		}
		
		String rangeKey = setKey + LATEST_SCORE;
		String ranges = sync.get(rangeKey);
		if(ranges != null && !ranges.trim().isEmpty()) {
			ranges = removeExpiredRanges(ranges);
			if(ranges != null && !ranges.trim().isEmpty()) {
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
		for(String range : ranges) {
			if(range == null || range.trim().isEmpty())
				continue;
			set.add(Long.parseLong(range.trim()));
		}
		long first = -1;
		long diff = 50 * 60 * 60 * 1000;
		Iterator<Long> itr = set.iterator();
		while(itr.hasNext()) {
			Long v = itr.next();
			if(first < 0) {
				first = v;
			} else if(first - v > diff) {
				itr.remove();
			}
		}
		StringBuilder finalRangesValue = new StringBuilder();
		itr = set.iterator();
		while(itr.hasNext()) {
			finalRangesValue.append(itr.next());
			if(itr.hasNext()) {
				finalRangesValue.append(";");
			}
		}
		return finalRangesValue.toString();
	}
	
	private List<String> resolveAllSetKeys(
			RedisCommands<String, String> sync, String setKey) {
		List<String> keys = new LinkedList<String>();
		List<ScoredValue<String>> zrangeWithScores = sync.zrangeWithScores(
				setKey, 0, -1);
		if (zrangeWithScores == null || zrangeWithScores.isEmpty())
			return Collections.emptyList();
		for (ScoredValue<String> zrangeWithScore : zrangeWithScores) {
			keys.add(zrangeWithScore.value);
		}
		return keys;
	}
	
	public Pagination<RSSFeed> getAllFeedsInStore(String keyPattern)
			throws PackPackException {
		return getAllUpdatedFeeds(keyPattern, -1, 1);
	}
	
	private static boolean isExpiredTimestamp(long timestamp) {
		long currentTimestamp = System.currentTimeMillis();
		long diff = currentTimestamp - timestamp;
		int days = (int)((((diff/1000)/60)/60)/24);
		return days >= 2 ? true : false;
	}
	
	private Pagination<RSSFeed> getAllUpdatedFeeds(String keyPattern, long timestamp, int direction) throws PackPackException {
		$_LOG.trace("timestamp = " + timestamp + " & direction = " + direction);
		$_LOG.trace("Key Pattern = " + keyPattern);
		Pagination<RSSFeed> page = new Pagination<RSSFeed>(timestamp);
		List<RSSFeed> feeds = new ArrayList<RSSFeed>();
		if(timestamp < 0) {
			if(timestamp != END_OF_PAGE_TIMESTAMP) {
				feeds = getAllFeeds(keyPattern + "*");
			}
			page.setResult(feeds);
			page.setNextLink(END_OF_PAGE + PAGELINK_DIRECTION_NEGATIVE);
			page.setPreviousLink(END_OF_PAGE + PAGELINK_DIRECTION_POSITIVE);
			return page;
		}
		
		RedisCommands<String, String> sync = getSyncRedisCommands();
		String setKey = SET_KEY_PREFIX + keyPattern.toUpperCase();
		String rangeKey = setKey + LATEST_SCORE;
		$_LOG.trace("Range Key = " + rangeKey);
		String ranges = sync.get(rangeKey);
		$_LOG.trace("ranges = " + ranges);
		if(ranges == null) {
			page.setResult(feeds);
			page.setNextLink(END_OF_PAGE + PAGELINK_DIRECTION_NEGATIVE);
			page.setPreviousLink(END_OF_PAGE + PAGELINK_DIRECTION_POSITIVE);
			return page;
		}
		String[] split = ranges.split(";");
		if(split.length == 0) {
			page.setResult(feeds);
			page.setNextLink(END_OF_PAGE + PAGELINK_DIRECTION_NEGATIVE);
			page.setPreviousLink(END_OF_PAGE + PAGELINK_DIRECTION_POSITIVE);
			return page;
		}
		
		List<String> keys = null;
		if(isExpiredTimestamp(timestamp) || timestamp < Long.parseLong(split[split.length - 1])) {
			timestamp = 0;
		}
		long[] scores = resolveRangeScores(split, timestamp, direction);
		$_LOG.debug("Scores = " + StringUtils.stringify(scores));
		if(scores.length == 0)
			return endOfPageResponse(timestamp, timestamp);
		long r1 = scores[0];
		long r2 = scores[1]; // r1 is expected to be <= r2
		long max = r1 >= r2 ? r1 : r2;
		long min = r1 <= r2 ? r1 : r2;
		$_LOG.trace("setKey = " + setKey);
		keys = resolveKeysForPagination(sync, r1, r2, setKey);
		$_LOG.trace("Keys = " + StringUtils.stringify(keys));
		if (keys == null || keys.isEmpty()) { // This means all the keys got expired due to TTL (And have been removed earlier or during auto sync, but ranges/scores NOT updated accordingly)
			//removeAllExpiredRanges(rangeKey, split, timestamp);
			return endOfPageResponse(timestamp, max);
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
				//removeAllExpiredRanges(rangeKey, split, timestamp);
				return endOfPageResponse(timestamp, max);
			} else {
				String nextLink = String.valueOf(min) + PAGELINK_DIRECTION_NEGATIVE;
				$_LOG.debug("nextLink = " + nextLink);
				page.setNextLink(nextLink);
				/*while(feeds.size() < 10) {
					Pagination<RSSFeed> nextPage = getAllUpdatedFeeds(keyPattern, r1);
					feeds.addAll(nextPage.getResult());
					page.setNextLink(nextPage.getNextLink());
				}*/
			}
		}
		
		if(timestamp == 0) {
			page.setPreviousLink(END_OF_PAGE + PAGELINK_DIRECTION_POSITIVE);
		} else if(timestamp == Long.MAX_VALUE) {
			page.setPreviousLink(END_OF_PAGE + PAGELINK_DIRECTION_POSITIVE);
		} else {
			page.setPreviousLink(String.valueOf(timestamp) + PAGELINK_DIRECTION_POSITIVE);
		}
		$_LOG.info("Size of Feeds = " + feeds.size());
		page.setResult(feeds);
		
		if(direction >= 0) {
			page.setTimestamp(min);
		} else {
			page.setTimestamp(max);
		}
		
		if(direction >= 0) {
			page.setNextLink(page.getTimestamp() + PAGELINK_DIRECTION_POSITIVE);
			page.setPreviousLink(page.getTimestamp() + PAGELINK_DIRECTION_NEGATIVE);
		} else {
			page.setNextLink(page.getTimestamp() + PAGELINK_DIRECTION_NEGATIVE);
			page.setPreviousLink(page.getTimestamp() + PAGELINK_DIRECTION_POSITIVE);
		}
		
		return page;
	}
	
	private List<String> resolveKeysForPagination(
			RedisCommands<String, String> sync, long r1, long r2,
			String rangeKey) {
		long min = r1 < r2 ? r1 : r2;
		long max = r1 > r2 ? r1 : r2;
		List<String> keys = new LinkedList<String>();
		List<ScoredValue<String>> zrangeWithScores = sync.zrangeWithScores(
				rangeKey, 0, -1);
		if (zrangeWithScores == null || zrangeWithScores.isEmpty())
			return Collections.emptyList();
		for (ScoredValue<String> zrangeWithScore : zrangeWithScores) {
			if (zrangeWithScore.score >= min && zrangeWithScore.score <= max) {
				keys.add(zrangeWithScore.value);
			}
		}
		return keys;
	}
	
	private Pagination<RSSFeed> endOfPageResponse(long previousTimestamp, long currentTimestamp) {
		Pagination<RSSFeed> page = new Pagination<RSSFeed>(currentTimestamp);
		page.setNextLink(END_OF_PAGE + PAGELINK_DIRECTION_POSITIVE);
		page.setPreviousLink(String.valueOf(previousTimestamp) + PAGELINK_DIRECTION_NEGATIVE);
		page.setResult(Collections.emptyList());
		return page;
	}
	
	/*private void removeAllExpiredRanges(String rangeKey, String[] rangesArr, long timestamp) {
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
		$_LOG.debug("After removing expired ranges = " + ranges);
		sync.set(rangeKey, ranges);
	}*/
	
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
		//long score = Long.parseLong("1535757064222");
		//long[] ranges = resolveRangeScores(scores, score, 1);
		//System.out.println(StringUtils.stringify(ranges));
		scores = new String[] { "10", "20", "30", "40", "50", "60",
				"70", "80", "90", "100", "110", "120", "130", "140", "150",
				"160", "170", "180", "190", "200", "210", "220", "230", "240",
				"250", "260", "270", "280", "290", "300", "310", "320", "330" };
		List<String> asList = Arrays.asList(scores);
		Collections.reverse(asList);
		scores = asList.toArray(new String[scores.length]);
		//System.out.println(StringUtils.stringify(scores));
		long[] ranges = new long[] {Long.parseLong(scores[2])};
		while(ranges.length > 0) {
			long score = ranges[0];
			ranges = resolveRangeScores(scores, score, 1);
			System.out.println(StringUtils.stringify(ranges));
		}
	}
	
	public static void main(String[] args) {
		new RssFeedRepositoryService().test();
	}
}