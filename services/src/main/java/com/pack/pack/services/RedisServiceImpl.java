package com.pack.pack.services;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.pack.pack.IRedisService;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class RedisServiceImpl implements IRedisService {
	
	private RedisClient client;
	private StatefulRedisConnection<String, String> connection;
	
	@PostConstruct
	private void init() {
		client = RedisClient.create("redis://localhost");
		connection = client.connect();
	}
	
	@Override
	public void dispose() {
		if(connection != null && connection.isOpen()) {
			connection.close();
		}
	}
	
	private StatefulRedisConnection<String, String> getConnection() {
		if(connection == null || !connection.isOpen()) {
			connection = client.connect();
		}
		return connection;
	}

	@Override
	public <T> void addToCache(String key, Object value)
			throws PackPackException {
		String json = JSONUtil.serialize(value);
		RedisCommands<String,String> sync = getConnection().sync();
		sync.set(key, json);
		sync.close();
	}

	@Override
	public <T> T getFromCache(String key, Class<T> targetType)
			throws PackPackException {
		RedisCommands<String,String> sync = getConnection().sync();
		String json = sync.get(key);
		sync.close();
		return JSONUtil.deserialize(json, targetType);
	}

	@Override
	public void removeFromCache(String key) throws PackPackException {
		RedisCommands<String,String> sync = getConnection().sync();
		sync.del(key);
		sync.close();
	}

	@Override
	public void removeAllFromCache(String keyPrefix) throws PackPackException {
		RedisCommands<String,String> sync = getConnection().sync();
		List<String> keys = sync.keys(keyPrefix + "*");
		if(keys == null || keys.isEmpty())
			return;
		sync.del(keys.toArray(new String[keys.size()]));
		sync.close();
	}
}