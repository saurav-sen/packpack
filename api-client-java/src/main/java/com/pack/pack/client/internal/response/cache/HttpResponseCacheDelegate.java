package com.pack.pack.client.internal.response.cache;

import java.io.IOException;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;

/**
 * 
 * @author Saurav
 *
 */
public interface HttpResponseCacheDelegate {

	/**
	 * 
	 * @param key
	 * @param entry
	 * @throws IOException
	 */
	public void putEntry(String key, HttpCacheEntry entry) throws IOException;

	/**
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public HttpCacheEntry getEntry(String key) throws IOException;

	/**
	 * 
	 * @param key
	 * @throws IOException
	 */
	public void removeEntry(String key) throws IOException;

	/**
	 * 
	 * @param key
	 * @param callback
	 * @throws IOException
	 * @throws HttpCacheUpdateException
	 */
	public void updateEntry(String key, HttpCacheUpdateCallback callback)
			throws IOException, HttpCacheUpdateException;
}