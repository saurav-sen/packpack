package com.pack.pack.client.internal.response.cache;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HTTP;

class BasicHttpCache implements HttpCache {

    private final CacheKeyGenerator uriExtractor;
    private final ResourceFactory resourceFactory;
    private final long maxObjectSizeBytes;
    private final CacheEntryUpdater cacheEntryUpdater;
    private final CachedHttpResponseGenerator responseGenerator;
    private final CacheInvalidator cacheInvalidator;
    private final HttpCacheStorage storage;

    //private final Log log = LogFactory.getLog(getClass());

    public BasicHttpCache(HttpCacheStorage storage, CacheConfig config) {
        this.resourceFactory = new HeapResourceFactory();
        this.uriExtractor = new CacheKeyGenerator();
        this.cacheEntryUpdater = new CacheEntryUpdater(resourceFactory);
        this.maxObjectSizeBytes = config.getMaxObjectSize();
        this.responseGenerator = new CachedHttpResponseGenerator();
        this.storage = storage;
        this.cacheInvalidator = new CacheInvalidator(this.uriExtractor, this.storage);
    }

    public void flushCacheEntriesFor(HttpHost host, HttpRequest request)
            throws IOException {
        String uri = uriExtractor.getURI(host, request);
        storage.remove(uri);
    }

    public void flushInvalidatedCacheEntriesFor(HttpHost host, HttpRequest request, HttpResponse response) {
        cacheInvalidator.flushInvalidatedCacheEntries(host, request, response);
    }

    void storeInCache(
            HttpHost target, HttpRequest request, HttpCacheEntry entry) throws IOException {
        if (entry.hasVariants()) {
            storeVariantEntry(target, request, entry);
        } else {
            storeNonVariantEntry(target, request, entry);
        }
    }

    void storeNonVariantEntry(
            HttpHost target, HttpRequest req, HttpCacheEntry entry) throws IOException {
        String uri = uriExtractor.getURI(target, req);
        storage.put(uri, entry);
    }

    void storeVariantEntry(
            final HttpHost target,
            final HttpRequest req,
            final HttpCacheEntry entry) throws IOException {
        final String parentURI = uriExtractor.getURI(target, req);
        final String variantURI = uriExtractor.getVariantURI(target, req, entry);
        storage.put(variantURI, entry);

        HttpCacheUpdateCallback callback = new HttpCacheUpdateCallback() {

            public HttpCacheEntry update(HttpCacheEntry existing) throws IOException {
                return doGetUpdatedParentEntry(
                        req.getRequestLine().getUri(), existing, entry,
                        uriExtractor.getVariantKey(req, entry),
                        variantURI);
            }

        };

        try {
            storage.update(parentURI, callback);
        } catch (HttpCacheUpdateException e) {
            // // log.warn("Could not update key [" + parentURI + "]", e);
        }
    }

    public void reuseVariantEntryFor(HttpHost target, final HttpRequest req,
            final Variant variant) throws IOException {
        final String parentCacheKey = uriExtractor.getURI(target, req);
        final HttpCacheEntry entry = variant.getEntry();
        final String variantKey = uriExtractor.getVariantKey(req, entry);
        final String variantCacheKey = variant.getCacheKey();

        HttpCacheUpdateCallback callback = new HttpCacheUpdateCallback() {
            public HttpCacheEntry update(HttpCacheEntry existing)
                    throws IOException {
                return doGetUpdatedParentEntry(req.getRequestLine().getUri(),
                        existing, entry, variantKey, variantCacheKey);
            }
        };

        try {
            storage.update(parentCacheKey, callback);
        } catch (HttpCacheUpdateException e) {
            // log.warn("Could not update key [" + parentCacheKey + "]", e);
        }
    }

    boolean isIncompleteResponse(HttpResponse resp, Resource resource) {
        int status = resp.getStatusLine().getStatusCode();
        if (status != HttpStatus.SC_OK
            && status != HttpStatus.SC_PARTIAL_CONTENT) {
            return false;
        }
        Header hdr = resp.getFirstHeader(HTTP.CONTENT_LEN);
        if (hdr == null) return false;
        int contentLength;
        try {
            contentLength = Integer.parseInt(hdr.getValue());
        } catch (NumberFormatException nfe) {
            return false;
        }
        return (resource.length() < contentLength);
    }

    HttpResponse generateIncompleteResponseError(HttpResponse response,
            Resource resource) {
        int contentLength = Integer.parseInt(response.getFirstHeader(HTTP.CONTENT_LEN).getValue());
        HttpResponse error =
            new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_BAD_GATEWAY, "Bad Gateway");
        error.setHeader("Content-Type","text/plain;charset=UTF-8");
        String msg = String.format("Received incomplete response " +
                "with Content-Length %d but actual body length %d",
                contentLength, resource.length());
        byte[] msgBytes = msg.getBytes();
        error.setHeader("Content-Length", Integer.toString(msgBytes.length));
        error.setEntity(new ByteArrayEntity(msgBytes));
        return error;
    }

    HttpCacheEntry doGetUpdatedParentEntry(
            final String requestId,
            final HttpCacheEntry existing,
            final HttpCacheEntry entry,
            final String variantKey,
            final String variantCacheKey) throws IOException {
        HttpCacheEntry src = existing;
        if (src == null) {
            src = entry;
        }

        Resource resource = resourceFactory.copy(requestId, src.getResource());
        Map<String,String> variantMap = new HashMap<String,String>(src.getVariantMap());
        variantMap.put(variantKey, variantCacheKey);
        return new HttpCacheEntry(
                src.getRequestDate(),
                src.getResponseDate(),
                src.getStatusLine(),
                src.getAllHeaders(),
                resource,
                variantMap);
    }

    public HttpCacheEntry updateCacheEntry(HttpHost target, HttpRequest request,
            HttpCacheEntry stale, HttpResponse originResponse,
            Date requestSent, Date responseReceived) throws IOException {
        HttpCacheEntry updatedEntry = cacheEntryUpdater.updateCacheEntry(
                request.getRequestLine().getUri(),
                stale,
                requestSent,
                responseReceived,
                originResponse);
        storeInCache(target, request, updatedEntry);
        return updatedEntry;
    }

    public HttpCacheEntry updateVariantCacheEntry(HttpHost target, HttpRequest request,
            HttpCacheEntry stale, HttpResponse originResponse,
            Date requestSent, Date responseReceived, String cacheKey) throws IOException {
        HttpCacheEntry updatedEntry = cacheEntryUpdater.updateCacheEntry(
                request.getRequestLine().getUri(),
                stale,
                requestSent,
                responseReceived,
                originResponse);
        storage.put(cacheKey, updatedEntry);
        return updatedEntry;
    }

    public HttpResponse cacheAndReturnResponse(HttpHost host, HttpRequest request,
            HttpResponse originResponse, Date requestSent, Date responseReceived)
            throws IOException {

        SizeLimitedResponseReader responseReader = getResponseReader(request, originResponse);
        responseReader.readResponse();

        if (responseReader.isLimitReached()) {
            return responseReader.getReconstructedResponse();
        }

        Resource resource = responseReader.getResource();
        if (isIncompleteResponse(originResponse, resource)) {
            return generateIncompleteResponseError(originResponse, resource);
        }

        HttpCacheEntry entry = new HttpCacheEntry(
                requestSent,
                responseReceived,
                originResponse.getStatusLine(),
                originResponse.getAllHeaders(),
                resource);
        storeInCache(host, request, entry);
        return responseGenerator.generateResponse(entry);
    }

    SizeLimitedResponseReader getResponseReader(HttpRequest request, HttpResponse backEndResponse) {
        return new SizeLimitedResponseReader(
                resourceFactory, maxObjectSizeBytes, request, backEndResponse);
    }

    public HttpCacheEntry getCacheEntry(HttpHost host, HttpRequest request) throws IOException {
        HttpCacheEntry root = storage.get(uriExtractor.getURI(host, request));
        if (root == null) return null;
        if (!root.hasVariants()) return root;
        String variantCacheKey = root.getVariantMap().get(uriExtractor.getVariantKey(request, root));
        if (variantCacheKey == null) return null;
        return storage.get(variantCacheKey);
    }

    public void flushInvalidatedCacheEntriesFor(HttpHost host,
            HttpRequest request) throws IOException {
        cacheInvalidator.flushInvalidatedCacheEntries(host, request);
    }

    public Map<String, Variant> getVariantCacheEntriesWithEtags(HttpHost host, HttpRequest request)
            throws IOException {
        Map<String,Variant> variants = new HashMap<String,Variant>();
        HttpCacheEntry root = storage.get(uriExtractor.getURI(host, request));
        if (root == null || !root.hasVariants()) return variants;
        for(Map.Entry<String, String> variant : root.getVariantMap().entrySet()) {
            String variantKey = variant.getKey();
            String variantCacheKey = variant.getValue();
            addVariantWithEtag(variantKey, variantCacheKey, variants);
        }
        return variants;
    }

    private void addVariantWithEtag(String variantKey,
            String variantCacheKey, Map<String, Variant> variants)
            throws IOException {
        HttpCacheEntry entry = storage.get(variantCacheKey);
        if (entry == null) return;
        Header etagHeader = entry.getFirstHeader(HeaderConstants.ETAG);
        if (etagHeader == null) return;
        variants.put(etagHeader.getValue(), new Variant(variantKey, variantCacheKey, entry));
    }

}