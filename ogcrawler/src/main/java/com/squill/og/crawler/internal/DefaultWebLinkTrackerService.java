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
@Component("defaultWebLinkTrackerService")
@Scope("singleton")
public class DefaultWebLinkTrackerService implements IWebLinkTrackerService {

	private RedisClient client;
	private StatefulRedisConnection<String, String> connection;

	private static final String REDIS_HISTORY_TRACKER_URI_CONFIG = "redis.history.tracker.uri";

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultWebLinkTrackerService.class);

	private void init() {
		if (connection == null || !connection.isOpen()) {
			client = RedisClient.create(System
					.getProperty(REDIS_HISTORY_TRACKER_URI_CONFIG));
			connection = client.connect();
		}
	}

	public void dispose() {
		if (connection != null && connection.isOpen()) {
			/*
			 * try { clearAll(""); } catch (PackPackException e) {
			 * LOG.debug(e.getErrorCode(), e.getMessage(), e); }
			 */
			connection.close();
		}
	}

	private StatefulRedisConnection<String, String> getConnection() {
		if (connection == null || !connection.isOpen()) {
			connection = client.connect();
		}
		return connection;
	}

	public void addCrawledInfo(String link, WebSpiderTracker value,
			long ttlSeconds) {
		init();
		RedisCommands<String, String> sync = null;
		try {
			String key = EncryptionUtil.generateMD5HashKey(link, false, false);
			sync = getConnection().sync();
			String json = JSONUtil.serialize(value);
			sync.setex(key, ttlSeconds, json);
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (PackPackException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			if (sync != null) {
				sync.close();
			}
		}
	}

	public WebSpiderTracker getTrackedInfo(String link) {
		init();
		RedisCommands<String, String> sync = null;
		try {
			String key = EncryptionUtil.generateMD5HashKey(link, false, false);
			if (!isKeyExists(key)) {
				return null;
			}
			sync = getConnection().sync();
			String json = sync.get(key);
			return JSONUtil.deserialize(json, WebSpiderTracker.class);
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (PackPackException e) {
			LOG.error(e.getMessage(), e);
			return null;
		} finally {
			if (sync != null) {
				sync.close();
			}
		}
	}

	private boolean isKeyExists(String key) {
		init();
		RedisCommands<String, String> sync = getConnection().sync();
		String value = sync.get(key);
		sync.close();
		return value != null;
	}

	public void clearAll() {

	}

	protected void clearAll(String keyPrefix) throws PackPackException {
		init();
		RedisCommands<String, String> sync = getConnection().sync();
		List<String> keys = sync.keys(keyPrefix + "*");
		if (keys == null || keys.isEmpty())
			return;
		sync.del(keys.toArray(new String[keys.size()]));
		sync.close();
	}
}
