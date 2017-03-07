package com.pack.pack.client.internal.response.cache;

import java.io.IOException;

/**
 * 
 * @author Saurav
 *
 */
public interface HttpCacheStorage {

	public void put(String url, HttpCacheEntry response) throws IOException;

	public HttpCacheEntry get(String url) throws IOException;

	public void remove(String url) throws IOException;

	void update(String uri, HttpCacheUpdateCallback callback)
			throws IOException, HttpCacheUpdateException;
}
