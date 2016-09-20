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
	
	private static final Logger LOG = LoggerFactory.getLogger(RedisCacheService.class);
	
	@PostConstruct
	private void init() {
		client = RedisClient.create("redis://localhost");
		connection = client.connect();
	}
	
	public void dispose() {
		if(connection != null && connection.isOpen()) {
			try {
				removeAllFromCache("");
			} catch (PackPackException e) {
				LOG.debug(e.getErrorCode(), e.getMessage(), e);
			}
			connection.close();
		}
	}
	
	private StatefulRedisConnection<String, String> getConnection() {
		if(connection == null || !connection.isOpen()) {
			connection = client.connect();
		}
		return connection;
	}

	public <T> void addToCache(String key, Object value)
			throws PackPackException {
		String json = JSONUtil.serialize(value);
		RedisCommands<String,String> sync = getConnection().sync();
		sync.set(key, json);
		sync.close();
	}

	public <T> T getFromCache(String key, Class<T> targetType)
			throws PackPackException {
		RedisCommands<String,String> sync = getConnection().sync();
		String json = sync.get(key);
		sync.close();
		return JSONUtil.deserialize(json, targetType);
	}

	public void removeFromCache(String key) throws PackPackException {
		RedisCommands<String,String> sync = getConnection().sync();
		sync.del(key);
		sync.close();
	}

	public void removeAllFromCache(String keyPrefix) throws PackPackException {
		RedisCommands<String,String> sync = getConnection().sync();
		List<String> keys = sync.keys(keyPrefix + "*");
		if(keys == null || keys.isEmpty())
			return;
		sync.del(keys.toArray(new String[keys.size()]));
		sync.close();
	}
}
