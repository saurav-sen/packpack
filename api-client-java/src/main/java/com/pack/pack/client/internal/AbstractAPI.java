package com.pack.pack.client.internal;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.client.internal.response.cache.CacheConfig;
import com.pack.pack.client.internal.response.cache.HttpCacheStorage;
import com.pack.pack.client.internal.response.cache.HttpResponseCache;
import com.pack.pack.client.internal.response.cache.HttpResponseCacheDelegate;
import com.pack.pack.client.internal.response.cache.HttpResponseCacheDelegateFactory;

/**
 * 
 * @author Saurav
 *
 */
abstract class AbstractAPI implements API {

	protected abstract ApiInvoker getInvoker();

	@Override
	public Object execute() throws Exception {
		return getInvoker().invoke();
	}

	@Override
	public Object execute(MultipartRequestProgressListener listener)
			throws Exception {
		return getInvoker().invoke(listener);
	}

	protected CacheConfig getCacheConfig() {
		CacheConfig cacheConfig = new CacheConfig();
		cacheConfig.setMaxCacheEntries(1000);
		cacheConfig.setMaxObjectSize(8192);
		return cacheConfig;
	}

	protected final HttpCacheStorage getCacheStorage() {
		String clazzName = System.getProperty(APIConstants.CACHE_STORAGE);
		if (clazzName == null || clazzName.trim().isEmpty()) {
			return null;
		}
		try {
			Class<?> clazz = Class.forName(clazzName);
			Object instance = clazz.newInstance();
			if (!(instance instanceof HttpResponseCacheDelegateFactory)) {
				return null;
			}
			HttpResponseCacheDelegateFactory delegateFactory = (HttpResponseCacheDelegateFactory) instance;
			HttpResponseCacheDelegate delegate = delegateFactory.getDelegate();
			if (delegate == null) {
				return null;
			}
			return new HttpResponseCache(delegate);
		} catch (Throwable e) {
			return null;
		}
	}
}