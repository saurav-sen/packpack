package com.pack.pack.services.redis;

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
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class RedisCacheService {

	private RedisClient client;
	private StatefulRedisConnection<String, String> connection;
	
	private RedisCommands<String, String> sync;

	private static final Logger LOG = LoggerFactory
			.getLogger(RedisCacheService.class);

	@PostConstruct
	private void init() {
		client = RedisClient.create(SystemPropertyUtil.getRedisURI());
		connection = client.connect();
	}

	public void dispose() {
		if (connection != null && connection.isOpen()) {
			try {
				removeAllFromCache("");
			} catch (PackPackException e) {
				LOG.debug(e.getErrorCode(), e.getMessage(), e);
			}
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
		//return getConnection().sync();
		if(sync != null && sync.isOpen()) {
			return sync;
		}
		sync = getConnection().sync();
		return sync;
	}

	public void setTTL(String key, long ttlSeconds) {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		sync.expire(key, ttlSeconds);
		//sync.close();
	}

	public void addToCache(String key, Object value) throws PackPackException {
		if (value == null)
			return;
		String json = null;
		if (value.getClass().isAssignableFrom(String.class)) {
			json = value.toString();
		} else {
			json = JSONUtil.serialize(value);
		}
		RedisCommands<String, String> sync = getSyncRedisCommands();
		sync.set(key, json);
		//sync.close();
	}

	public void addToCache(String key, Object value, long ttlSeconds)
			throws PackPackException {
		if (value == null)
			return;
		String json = null;
		if (value instanceof String) {
			json = (String) value;
		} else {
			json = JSONUtil.serialize(value);
		}
		RedisCommands<String, String> sync = getSyncRedisCommands();
		sync.setex(key, ttlSeconds, json);
		//sync.close();
	}

	public boolean isKeyExists(String key) {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		String value = sync.get(key);
		//sync.close();
		return value != null;
	}

	public void addToCache(String key, Object value, long ttlSeconds,
			Object recovery) throws PackPackException {
		if (value == null)
			return;
		String json = null;
		if (value instanceof String) {
			json = (String) value;
		} else {
			json = JSONUtil.serialize(value);
		}
		RedisCommands<String, String> sync = getSyncRedisCommands();
		sync.setex(key, ttlSeconds, json);
		if (recovery != null) {
			if (recovery.getClass().isAssignableFrom(String.class)) {
				json = recovery.toString();
			} else {
				json = JSONUtil.serialize(recovery);
			}
			sync.set(key + ":expired", json);
		}
		//sync.close();
	}

	public long getTTL(String key) {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		long ttl = sync.ttl(key);
		//sync.close();
		return ttl;
	}

	public void removeRecoveryIfExpired(String key) {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		long ttl = sync.ttl(key);
		if (ttl <= 0) {
			sync.del(key + ":expired");
		}
		//sync.close();
	}

	@SuppressWarnings("unchecked")
	public <T> T getFromCache(String key, Class<T> targetType)
			throws PackPackException {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		String json = sync.get(key);
		//sync.close();
		if (json == null)
			return null;
		if (targetType.isAssignableFrom(String.class)) {
			return (T) json;
		}
		return JSONUtil.deserialize(json, targetType, true);
	}

	public void removeFromCache(String key) throws PackPackException {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		sync.del(key);
		//sync.close();
	}

	public void removeAllFromCache(String keyPrefix) throws PackPackException {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		List<String> keys = sync.keys(keyPrefix + "*");
		if (keys == null || keys.isEmpty())
			return;
		sync.del(keys.toArray(new String[keys.size()]));
		//sync.close();
	}
}
