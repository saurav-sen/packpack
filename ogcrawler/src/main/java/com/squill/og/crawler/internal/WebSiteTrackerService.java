package com.squill.og.crawler.internal;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.security.util.EncryptionUtil;
import com.pack.pack.services.exception.PackPackException;
import com.squill.og.crawler.model.WebSpiderTracker;

/**
 * 
 * @author Saurav
 *
 */
@Component("webSiteTrackerService")
@Scope("singleton")
public class WebSiteTrackerService {

	private RedisClient client;
	private StatefulRedisConnection<String, String> connection;

	private static final Logger LOG = LoggerFactory
			.getLogger(WebSiteTrackerService.class);

	private void init() {
		if (connection == null || !connection.isOpen()) {
			client = RedisClient.create("redis://localhost");
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
			throw new RuntimeException(e);
		} catch (PackPackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			throw new RuntimeException(e);
		} catch (PackPackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			if (sync != null) {
				sync.close();
			}
		}
	}

	public boolean isKeyExists(String key) {
		init();
		RedisCommands<String, String> sync = getConnection().sync();
		String value = sync.get(key);
		sync.close();
		return value != null;
	}

	public void clearAll(String keyPrefix) throws PackPackException {
		init();
		RedisCommands<String, String> sync = getConnection().sync();
		List<String> keys = sync.keys(keyPrefix + "*");
		if (keys == null || keys.isEmpty())
			return;
		sync.del(keys.toArray(new String[keys.size()]));
		sync.close();
	}
}
