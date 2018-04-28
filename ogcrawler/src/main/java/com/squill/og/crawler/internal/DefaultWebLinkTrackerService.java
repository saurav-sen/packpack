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
import com.pack.pack.util.SystemPropertyUtil;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.utils.EncryptionUtil;
import com.squill.og.crawler.internal.utils.JSONUtil;
import com.squill.og.crawler.model.WebSpiderTracker;
import com.squill.services.exception.OgCrawlException;

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

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultWebLinkTrackerService.class);
	
	private boolean initialized = false;
	
	private boolean errored = false;
	
	private RedisCommands<String, String> sync;
	
	@Override
	public void init(IWebSite webSite) {
	}

	@PostConstruct
	private void init0() {
		try {
			if(initialized || errored) {
				return;
			}
			if (connection == null || !connection.isOpen()) {
				client = RedisClient.create(SystemPropertyUtil.getRedisURI());
				connection = client.connect();
			}
			initialized = true;
			errored = false;
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
			init0();
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
		} catch (OgCrawlException e) {
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
		} catch (OgCrawlException e) {
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

	protected void clearAll(String keyPrefix) throws OgCrawlException {
		RedisCommands<String, String> sync = sync();
		List<String> keys = sync.keys(keyPrefix + "*");
		if (keys == null || keys.isEmpty())
			return;
		sync.del(keys.toArray(new String[keys.size()]));
		//sync.close();
	}
}
