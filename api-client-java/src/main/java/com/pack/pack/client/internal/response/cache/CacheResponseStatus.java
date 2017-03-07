package com.pack.pack.client.internal.response.cache;
public enum CacheResponseStatus {

    /** The response was generated directly by the caching module. */
    CACHE_MODULE_RESPONSE,

    /** A response was generated from the cache with no requests sent
     * upstream.
     */
    CACHE_HIT,

    /** The response came from an upstream server. */
    CACHE_MISS,

    /** The response was generated from the cache after validating the
     * entry with the origin server.
     */
    VALIDATED;

}