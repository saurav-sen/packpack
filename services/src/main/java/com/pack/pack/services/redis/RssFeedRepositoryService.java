package com.pack.pack.services.redis;

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
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.TTL;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.util.RssFeedUtil;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
//@Views({ @View(name = "findFeedPromotions", map = "function(doc) { if(doc.promoStartTimestamp && doc.promoExpiryTimestamp) { emit(doc.promoStartTimestamp + doc.promoExpiryTimestamp); } }") })
//public class RssFeedRepositoryService extends CouchDbRepositorySupport<RSSFeed> {
public class RssFeedRepositoryService {

	/*@Autowired
	public RssFeedRepositoryService(@Qualifier("packDB") CouchDbConnector db) {
		super(RSSFeed.class, db);
	}
	
	@PostConstruct
	public void doInit() {
		initStandardDesignDocument();
	}

	public List<RSSFeed> getAllPromotionalFeeds(long startTime, long expiryTime)
			throws PackPackException {
		//String startKey = String.valueOf(startTime);
		//String endKey = startKey + String.valueOf(expiryTime);
		ViewQuery query = createQuery("findFeedPromotions").includeDocs(true);//.startKey(startKey).endKey(endKey);
		return db.queryView(query, RSSFeed.class);
	}*/
	
	private RedisClient client;
	private StatefulRedisConnection<String, String> connection;

	private RedisCommands<String, String> sync;

	private static final Logger LOG = LoggerFactory
			.getLogger(RssFeedRepositoryService.class);

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
	
	public boolean checkFeedExists(JRssFeed feed) {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		//String key = "Feeds_" + String.valueOf(feed.getOgUrl().hashCode());
		String key = RssFeedUtil.generateUploadKey(feed);
		List<String> list = sync.keys(key);
		if(list == null || list.isEmpty())
			return false;
		return true;
	}
	
	public void uploadPromotionalFeed(RSSFeed feed, TTL ttl)
			throws PackPackException {
		LOG.info("Uploading " + feed.getOgType() + " Feed @ " + feed.getOgUrl());
		LOG.info("Uploading Feed Titled :: " + feed.getOgTitle());
		String json = JSONUtil.serialize(feed);
		RedisCommands<String, String> sync = getSyncRedisCommands();
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
		String key = RssFeedUtil.generateUploadKey(feed);
		sync.setex(key, ttlSeconds, json);
		LOG.info("Successfully uploaded Feed");
		// sync.close();
	}
	
	public List<RSSFeed> getAllPromotionalFeeds() throws PackPackException {
		/*RedisCommands<String, String> sync = getSyncRedisCommands();
		List<String> keys = sync.keys("Feeds_*");
		if (keys == null || keys.isEmpty())
			return Collections.emptyList();
		List<RSSFeed> feeds = new LinkedList<RSSFeed>();
		for (String key : keys) {
			String json = sync.get(key);
			RSSFeed feed = JSONUtil.deserialize(json, RSSFeed.class, true);
			feeds.add(feed);
		}
		// sync.close();
		return feeds;*/
		return getAllFeeds("Feeds_*");
	}
	
	public List<RSSFeed> getAllNewsFeeds() throws PackPackException {
		return getAllFeeds("News_*");
	}
	
	/*public List<RSSFeed> getAllUserCustomFeeds(String city, String country) throws PackPackException {
		return getAllFeeds("User_broadcast_*");
	}*/
	
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
}