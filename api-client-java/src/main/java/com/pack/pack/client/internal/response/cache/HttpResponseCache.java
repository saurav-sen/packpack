package com.pack.pack.client.internal.response.cache;

import java.io.IOException;

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
	public void put(String url, HttpCacheEntry entry) throws IOException {
		delegate.put(url, entry);
	}

	@Override
	public HttpCacheEntry get(String url) throws IOException {
		return delegate.get(url);
	}

	@Override
	public void remove(String url) throws IOException {
		delegate.remove(url);
	}

	@Override
	public void update(String uri, HttpCacheUpdateCallback callback)
			throws IOException, HttpCacheUpdateException {
		delegate.update(uri, callback);
	}

	/*@Override
	public void updateEntry(String key, HttpCacheUpdateCallback callback)
			throws IOException, HttpCacheUpdateException {
		delegate.updateEntry(key, callback);
	}*/
}