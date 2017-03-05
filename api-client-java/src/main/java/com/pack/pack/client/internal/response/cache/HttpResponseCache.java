package com.pack.pack.client.internal.response.cache;

import java.io.IOException;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;

/**
 * 
 * @author Saurav
 *
 */
public class HttpResponseCache implements HttpCacheStorage {
	
	private final HttpResponseCacheDelegate delegate;
	
	public HttpResponseCache(HttpResponseCacheDelegate delegate) {
		if(delegate == null) {
			throw new RuntimeException("Failed to initialize "
					+ "HttpResponseCache, delegate has NULL "
					+ "reference");
		}
		this.delegate = delegate;
	}

	@Override
	public void putEntry(String key, HttpCacheEntry entry) throws IOException {
		delegate.putEntry(key, entry);
	}

	@Override
	public HttpCacheEntry getEntry(String key) throws IOException {
		return delegate.getEntry(key);
	}

	@Override
	public void removeEntry(String key) throws IOException {
		delegate.removeEntry(key);
	}

	@Override
	public void updateEntry(String key, HttpCacheUpdateCallback callback)
			throws IOException, HttpCacheUpdateException {
		delegate.updateEntry(key, callback);
	}
}