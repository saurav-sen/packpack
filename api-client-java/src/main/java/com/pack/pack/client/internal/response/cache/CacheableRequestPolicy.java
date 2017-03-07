package com.pack.pack.client.internal.response.cache;

/*import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;*/
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;

class CacheableRequestPolicy {

    //private final Log log = LogFactory.getLog(getClass());

    /**
     * Determines if an HttpRequest can be served from the cache.
     *
     * @param request
     *            an HttpRequest
     * @return boolean Is it possible to serve this request from cache
     */
    public boolean isServableFromCache(HttpRequest request) {
        String method = request.getRequestLine().getMethod();

        ProtocolVersion pv = request.getRequestLine().getProtocolVersion();
        if (HttpVersion.HTTP_1_1.compareToVersion(pv) != 0) {
            // log.trace("non-HTTP/1.1 request was not serveable from cache");
            return false;
        }

        if (!method.equals(HeaderConstants.GET_METHOD)) {
            // log.trace("non-GET request was not serveable from cache");
            return false;
        }

        if (request.getHeaders(HeaderConstants.PRAGMA).length > 0) {
            // log.trace("request with Pragma header was not serveable from cache");
            return false;
        }

        Header[] cacheControlHeaders = request.getHeaders(HeaderConstants.CACHE_CONTROL);
        for (Header cacheControl : cacheControlHeaders) {
            for (HeaderElement cacheControlElement : cacheControl.getElements()) {
                if (HeaderConstants.CACHE_CONTROL_NO_STORE.equalsIgnoreCase(cacheControlElement
                        .getName())) {
                    // log.trace("Request with no-store was not serveable from cache");
                    return false;
                }

                if (HeaderConstants.CACHE_CONTROL_NO_CACHE.equalsIgnoreCase(cacheControlElement
                        .getName())) {
                    // log.trace("Request with no-cache was not serveable from cache");
                    return false;
                }
            }
        }

        // log.trace("Request was serveable from cache");
        return true;
    }

}