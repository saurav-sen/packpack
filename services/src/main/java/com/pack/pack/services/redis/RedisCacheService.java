package com.pack.pack.services.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lambdaworks.redis.GeoArgs.Unit;
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
		if(System.getProperty("service.registry.test") != null) {
			return;
		}
		client = RedisClient.create(SystemPropertyUtil.getRedisURI());
		connection = client.connect();
	}

	public void dispose() {
		if (connection != null && connection.isOpen()) {
			/*try {
				removeAllFromCache("");
			} catch (PackPackException e) {
				LOG.debug(e.getErrorCode(), e.getMessage(), e);
			}*/
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
	
	public void addGeoTaggedItemsToCache(String key, String member, Object value, double longitude, double latitude, long ttlSeconds)
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
		sync.geoadd(key, longitude, latitude, member);
		sync.setex(member, ttlSeconds, json);
		//sync.close();
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getGeoTaggedItemsFromCache(String key, double longitude, double latitude, double distanceInKm, Class<T> targetType)
			throws PackPackException {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		Set<String> members = sync.georadius(key, longitude, latitude, distanceInKm, Unit.km);
		Set<String> expiredMembers = new HashSet<String>();
		List<T> result = new ArrayList<T>();
		for(String member : members) {
			String json = sync.get(member);
			//sync.close();
			T object = null;
			if (json == null) {
				expiredMembers.add(member);
				continue;
			}
			if (targetType.isAssignableFrom(String.class)) {
				object = (T) json;
			}
			object = JSONUtil.deserialize(json, targetType, true);
			if(object == null)
				continue;
			result.add(object);
		}
		if(!expiredMembers.isEmpty()) {
			sync.zrem(key, expiredMembers.toArray(new String[expiredMembers.size()]));
		}
		return result;
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
	
	public <T> List<T> getAllFromCache(String keyPrefix, Class<T> targetType)
			throws PackPackException {
		RedisCommands<String, String> sync = getSyncRedisCommands();
		List<String> keys = sync.keys(keyPrefix + "*");
		if (keys == null || keys.isEmpty())
			return Collections.emptyList();
		List<T> result = new LinkedList<T>();
		for (String key : keys) {
			String json = sync.get(key);
			T t = JSONUtil.deserialize(json, targetType, true);
			result.add(t);
		}
		return result;
	}
}
