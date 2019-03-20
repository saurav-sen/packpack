package com.squill.og.crawler.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
import com.squill.feed.web.model.JRssFeedType;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.utils.EncryptionUtil;
import com.squill.og.crawler.model.DocumentHeadersMemento;
import com.squill.og.crawler.model.WebSpiderTracker;
import com.squill.og.crawler.rss.RSSConstants;
import com.squill.services.exception.OgCrawlException;
import com.squill.utils.JSONUtil;

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
	
	private static final String KEY_PREFIX = "TRACK_";
	
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

	public void upsertCrawledInfo(String link, WebSpiderTracker value,
			long ttlSeconds, boolean updateLastModifiedTime) {
		if(updateLastModifiedTime) {
			updateLastModifedTime(value);
		}
		RedisCommands<String, String> sync = null;
		try {
			String key = KEY_PREFIX + EncryptionUtil.generateMD5HashKey(link, false, false);
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
	
	private void updateLastModifedTime(WebSpiderTracker value) {
		if (value == null || value.getLink() == null
				|| value.getLink().trim().isEmpty())
			return;
		try {
			URL url = new URL(value.getLink());
			URLConnection connection = url.openConnection();
			String lastModified = connection.getHeaderField("Last-Modified");
			if(lastModified != null) {
				value.setLastModifiedSince(lastModified);
			}
		} catch (MalformedURLException e) {
			LOG.debug(e.getMessage(), e);
		} catch (IOException e) {
			LOG.debug(e.getMessage(), e);
		}
	}
	
	@Override
	public List<WebSpiderTracker> getAllTrackedInfo() {
		List<WebSpiderTracker> result = new ArrayList<WebSpiderTracker>();
		try {
			sync = sync();
			List<String> keys = sync.keys(KEY_PREFIX + "*");
			if(keys == null || keys.isEmpty())
				return result;
			for(String key : keys) {
				String json = sync.get(key);
				WebSpiderTracker info = JSONUtil.deserialize(json, WebSpiderTracker.class, true);
				if(!info.isUploadCompleted()) {
					result.add(info);
				}
			}
		} catch (OgCrawlException e) {
			LOG.error(e.getMessage(), e);
		}
		return result;
	}

	public WebSpiderTracker getTrackedInfo(String link) {
		RedisCommands<String, String> sync = null;
		try {
			String key = KEY_PREFIX + EncryptionUtil.generateMD5HashKey(link, false, false);
			if (!isKeyExists(key)) {
				LOG.debug("Key doesn't exist for link = " + link);
				return null;
			}
			sync = sync();
			String json = sync.get(key);
			return JSONUtil.deserialize(json, WebSpiderTracker.class, true);
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (OgCrawlException e) {
			LOG.error(e.getMessage(), e);
			LOG.debug("Deserialization of previously stored info failed for link = " + link);
			return null;
		} /*finally {
			if (sync != null) {
				sync.close();
			}
		}*/
	}
	
	private DocumentHeadersMemento headersMemento;
	
	private boolean isMementoTriedLoading = false;
	
	@Override
	public DocumentHeadersMemento getPreviousSessionMemento(JRssFeedType type) {
		if(type == null)
			return null;
		
		if(headersMemento != null) {
			return headersMemento;
		}
		
		if(isMementoTriedLoading) {
			return null;
		}
		
		RedisCommands<String, String> sync = null;
		try {
			String key = "MEMENTO_HEADER_" + type.name();
			if (!isKeyExists(key)) {
				LOG.debug("Headers memento doesn't exist for type = " + type.name());
				return null;
			}
			sync = sync();
			String json = sync.get(key);
			headersMemento = JSONUtil.deserialize(json, DocumentHeadersMemento.class, true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			LOG.debug("Deserialization of previously stored headers memento failed for type = " + type.name());
			return null;
		} finally {
			isMementoTriedLoading = true;
			/*if (sync != null) {
				sync.close();
			}*/
		}
		return headersMemento;
	}
	
	@Override
	public void flushNewHeadersMemento(DocumentHeadersMemento headersMemento, JRssFeedType type) {
		if(headersMemento == null || type == null)
			return;
		
		RedisCommands<String, String> sync = null;
		try {
			String key = "MEMENTO_HEADER_" + type.name();
			sync = sync();
			String json = JSONUtil.serialize(headersMemento);
			sync.setex(key, RSSConstants.DEFAULT_TTL_HEADERS_MEMENTO, json);
			this.headersMemento = headersMemento;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
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

	@Override
	public void addValue(String keyPrefix, String key, String value, long ttlSeconds) {
		RedisCommands<String, String> sync = null;
		try {
			key = keyPrefix + "_" + key;
			sync = sync();
			sync.setex(key, ttlSeconds, value);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public String getValue(String keyPrefix, String key) {
		RedisCommands<String, String> sync = null;
		String value = null;
		try {
			String k1 = keyPrefix + "_" + key;
			if (!isKeyExists(k1)) {
				LOG.debug("Key doesn't exist for key = " + key);
				return null;
			}
			sync = sync();
			value = sync.get(k1);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		return value;
	}
}
