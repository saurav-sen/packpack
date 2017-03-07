package com.pack.pack.client.internal.response.cache;

/**
 * 
 * @author Saurav
 *
 */
public class CacheConfig {
	
	/** Default setting for the maximum object size that will be
     * cached, in bytes.
     */
    public final static int DEFAULT_MAX_OBJECT_SIZE_BYTES = 8192;
	
	 /** Default setting for the maximum number of cache entries
     * that will be retained.
     */
    public final static int DEFAULT_MAX_CACHE_ENTRIES = 1000;
    
    private long maxObjectSize = DEFAULT_MAX_OBJECT_SIZE_BYTES;
	
	private int maxCacheEntries = DEFAULT_MAX_CACHE_ENTRIES;

	/**
     * Returns the maximum number of cache entries the cache will retain.
     */
    public int getMaxCacheEntries() {
        return maxCacheEntries;
    }

    /**
     * Sets the maximum number of cache entries the cache will retain.
     */
    public void setMaxCacheEntries(int maxCacheEntries) {
        this.maxCacheEntries = maxCacheEntries;
    }
    
    /**
     * Returns the current maximum response body size that will be cached.
     * @return size in bytes
     *
     * @since 4.2
     */
    public long getMaxObjectSize() {
        return maxObjectSize;
    }

    /**
     * Specifies the maximum response body size that will be eligible for caching.
     * @param maxObjectSize size in bytes
     *
     * @since 4.2
     */
    public void setMaxObjectSize(long maxObjectSize) {
        this.maxObjectSize = maxObjectSize;
    }
}