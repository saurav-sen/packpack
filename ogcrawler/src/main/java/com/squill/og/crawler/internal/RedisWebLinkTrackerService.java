package com.squill.og.crawler.internal;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.utils.EncryptionUtil;
import com.squill.og.crawler.internal.utils.JSONUtil;
import com.squill.og.crawler.model.WebSpiderTracker;
import com.squill.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
@Component("redisWebLinkTrackerService")
@Scope("singleton")
public class RedisWebLinkTrackerService implements IWebLinkTrackerService {

	private RedisClient client;
	private StatefulRedisConnection<String, String> connection;

	private static final String REDIS_HISTORY_TRACKER_URI_CONFIG = "redis.history.tracker.uri";

	private static final Logger LOG = LoggerFactory
			.getLogger(RedisWebLinkTrackerService.class);
	
	private boolean initialized = false;
	
	private boolean errored = false;
	
	private RedisCommands<String, String> sync;
	
	/*public static void main(String[] args) {
		System.setProperty(REDIS_HISTORY_TRACKER_URI_CONFIG, "redis://192.168.35.15");
		DefaultWebLinkTrackerService a1 = new DefaultWebLinkTrackerService();
		a1.abc();
		DefaultWebLinkTrackerService a2 = new DefaultWebLinkTrackerService();
		a2.xyz();
		a1.dispose();
		a2.dispose();
	}
	
	private void abc() {
		init();
		sync.set("myKey", "myValue");
		System.out.println(sync.get("myKey"));
		dispose();
	}
	
	private void xyz() {
		init();
		System.out.println(sync.get("myKey"));
		dispose();
	}*/

	private void init() {
		try {
			if(initialized || errored) {
				return;
			}
			if (connection == null || !connection.isOpen()) {
				client = RedisClient.create(System
						.getProperty(REDIS_HISTORY_TRACKER_URI_CONFIG));
				connection = client.connect();
			}
			initialized = true;
			errored = false;
			/*if(sync == null) {
				sync = sync();
			}*/
		} catch (Throwable e) {
			LOG.info("Error Initializing Redis Connection");
			LOG.info(e.getMessage(), e);
			errored = true;
		}
	}

	public void dispose() {
		if(!initialized) {
			return;
		}
		/*if(sync != null) {
			sync.close();
		}*/
		if (connection != null && connection.isOpen()) {
			/*
			 * try { clearAll(""); } catch (PackPackException e) {
			 * LOG.debug(e.getErrorCode(), e.getMessage(), e); }
			 */
			connection.close();
		}
		initialized = false;
	}

	private RedisCommands<String, String> sync() {
		if(!initialized && !errored) {
			init();
		}
		/*if(sync != null && sync.isOpen()) {
			sync.close();
			sync = connection.sync();
		}*/
		if(sync != null) {
			return sync;
		} else if(connection != null && connection.isOpen()) {
			sync = connection.sync();
		}
		return sync;
	}

	public void addCrawledInfo(String link, WebSpiderTracker value,
			long ttlSeconds) {
		RedisCommands<String, String> sync = null;
		try {
			String key = EncryptionUtil.generateMD5HashKey(link, false, false);
			sync = sync();
			String json = JSONUtil.serialize(value);
			sync.setex(key, ttlSeconds, json);
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (PackPackException e) {
			LOG.error(e.getMessage(), e);
		} /*finally {
			if (sync != null) {
				sync.close();
			}
		}*/
	}

	public WebSpiderTracker getTrackedInfo(String link) {
		RedisCommands<String, String> sync = null;
		try {
			String key = EncryptionUtil.generateMD5HashKey(link, false, false);
			if (!isKeyExists(key)) {
				return null;
			}
			sync = sync();
			String json = sync.get(key);
			return JSONUtil.deserialize(json, WebSpiderTracker.class);
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (PackPackException e) {
			LOG.error(e.getMessage(), e);
			return null;
		} /*finally {
			if (sync != null) {
				sync.close();
			}
		}*/
	}

	private boolean isKeyExists(String key) {
		RedisCommands<String, String> sync = sync();
		if(sync == null) {
			return false;
		}
		String value = sync.get(key);
		//sync.close();
		return value != null;
	}

	public void clearAll() {

	}

	protected void clearAll(String keyPrefix) throws PackPackException {
		RedisCommands<String, String> sync = sync();
		List<String> keys = sync.keys(keyPrefix + "*");
		if (keys == null || keys.isEmpty())
			return;
		sync.del(keys.toArray(new String[keys.size()]));
		//sync.close();
	}
}
