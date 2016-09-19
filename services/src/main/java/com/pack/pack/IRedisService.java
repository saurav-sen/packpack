package com.pack.pack;

import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public interface IRedisService {

	public <T> void addToCache(String key, Object value)
			throws PackPackException;

	public <T> T getFromCache(String key, Class<T> targetType)
			throws PackPackException;

	public void removeFromCache(String key) throws PackPackException;

	public void removeAllFromCache(String keyPrefix) throws PackPackException;

	public void dispose();
}