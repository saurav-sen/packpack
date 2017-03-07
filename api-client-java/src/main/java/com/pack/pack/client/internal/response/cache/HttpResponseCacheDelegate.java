package com.pack.pack.client.internal.response.cache;

import java.io.IOException;

/**
 * 
 * @author Saurav
 *
 */
public interface HttpResponseCacheDelegate {

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param entry
	 * @throws IOException
	 */
	public void put(String url, HttpCacheEntry entry) throws IOException;

	/**
	 * 
	 * @param url
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	public HttpCacheEntry get(String url) throws IOException;

	/**
	 * 
	 * @param url
	 * @param headers
	 * @throws IOException
	 */
	public void remove(String url) throws IOException;
	
	/**
	 * 
	 * @param uri
	 * @param callback
	 * @throws IOException
	 * @throws HttpCacheUpdateException
	 */
	public void update(String uri, HttpCacheUpdateCallback callback)
			throws IOException, HttpCacheUpdateException;
}