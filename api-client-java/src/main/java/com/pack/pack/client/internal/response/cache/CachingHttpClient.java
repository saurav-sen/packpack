package com.pack.pack.client.internal.response.cache;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.VersionInfo;

/**
 * 
 * @author Saurav
 *
 */
public class CachingHttpClient implements HttpClient {

	private HttpClient client;
	private HttpCache responseCache;
	private CacheConfig config;

	private final AtomicLong cacheHits = new AtomicLong();
	private final AtomicLong cacheUpdates = new AtomicLong();
	private final AtomicLong cacheMisses = new AtomicLong();
	
	private final Map<ProtocolVersion, String> viaHeaders = new HashMap<ProtocolVersion, String>(4);

	private ResponseCachingPolicy responseCachingPolicy;
	private final CachedResponseSuitabilityChecker suitabilityChecker;
	private final CacheValidityPolicy validityPolicy;

	private final CachedHttpResponseGenerator responseGenerator;

	private final ConditionalRequestBuilder conditionalRequestBuilder;
	
	private final CacheableRequestPolicy cacheableRequestPolicy;

	public CachingHttpClient(HttpClient client, HttpCacheStorage storage,
			CacheConfig config) {
		this.client = client;
		this.responseCache = new BasicHttpCache(storage, config);
		this.config = config;

		responseCachingPolicy = new ResponseCachingPolicy(
				config.getMaxObjectSize(), true);
		
		this.validityPolicy = new CacheValidityPolicy();
		this.responseGenerator = new CachedHttpResponseGenerator(
				this.validityPolicy);
		this.suitabilityChecker = new CachedResponseSuitabilityChecker(
				this.validityPolicy, config);

		this.conditionalRequestBuilder = new ConditionalRequestBuilder();
		
		this.cacheableRequestPolicy = new CacheableRequestPolicy();
	}

	@Override
	public HttpParams getParams() {
		return client.getParams();
	}

	@Override
	public ClientConnectionManager getConnectionManager() {
		return client.getConnectionManager();
	}

	@Override
	public HttpResponse execute(HttpUriRequest request) throws IOException,
			ClientProtocolException {
		HttpContext context = null;
		return execute(request, context);
	}

	@Override
	public HttpResponse execute(HttpUriRequest request, HttpContext context)
			throws IOException, ClientProtocolException {
		URI uri = request.getURI();
		HttpHost httpHost = new HttpHost(uri.getHost(), uri.getPort(),
				uri.getScheme());
		return execute(httpHost, request, context);
	}

	@Override
	public HttpResponse execute(HttpHost target, HttpRequest request)
			throws IOException, ClientProtocolException {
		HttpContext defaultContext = null;
		return execute(target, request, defaultContext);
	}

	@Override
	public HttpResponse execute(HttpHost target, HttpRequest request,
			HttpContext context) throws IOException, ClientProtocolException {
		
		String via = generateViaHeader(request);
		
		//request = requestCompliance.makeRequestCompliant(request);
		 request.addHeader("Via",via);
		 flushEntriesInvalidatedByRequest(target, request);
		 
		if (!cacheableRequestPolicy.isServableFromCache(request)) {
			// log.debug("Request is not servable from cache");
			return callBackend(target, request, context);
		}
		/*
		 * // default response context setResponseStatus(context,
		 * CacheResponseStatus.CACHE_MISS);
		 * 
		 * String via = generateViaHeader(request);
		 * 
		 * if (clientRequestsOurOptions(request)) { setResponseStatus(context,
		 * CacheResponseStatus.CACHE_MODULE_RESPONSE); return new
		 * OptionsHttp11Response(); }
		 * 
		 * HttpResponse fatalErrorResponse = getFatallyNoncompliantResponse(
		 * request, context); if (fatalErrorResponse != null) return
		 * fatalErrorResponse;
		 * 
		 * request = requestCompliance.makeRequestCompliant(request);
		 * request.addHeader("Via",via);
		 * 
		 * flushEntriesInvalidatedByRequest(target, request);
		 * 
		 * if (!cacheableRequestPolicy.isServableFromCache(request)) {
		 * log.debug("Request is not servable from cache"); return
		 * callBackend(target, request, context); }
		 */
		HttpCacheEntry entry = satisfyFromCache(target, request);
		if (entry == null) {
			// log.debug("Cache miss");
			return handleCacheMiss(target, request, context);
		}
		return handleCacheHit(target, request, context, entry);
	}
	
	private void flushEntriesInvalidatedByRequest(HttpHost target,
            HttpRequest request) {
        try {
            responseCache.flushInvalidatedCacheEntriesFor(target, request);
        } catch (IOException ioe) {
            // log.warn("Unable to flush invalidated entries from cache", ioe);
        }
    }

	private HttpResponse handleCacheHit(HttpHost target, HttpRequest request,
			HttpContext context, HttpCacheEntry entry)
			throws ClientProtocolException, IOException {
		recordCacheHit(target, request);
		HttpResponse out = null;
		Date now = getCurrentDate();
		if (suitabilityChecker.canCachedResponseBeUsed(target, request, entry,
				now)) {
			// log.debug("Cache hit");
			out = generateCachedResponse(request, context, entry, now);
		} else if (!mayCallBackend(request)) {
			// log.debug("Cache entry not suitable but only-if-cached requested");
			out = generateGatewayTimeout(context);
		} /*
		 * else if (validityPolicy.isRevalidatable(entry)) {
		 * //log.debug("Revalidating cache entry"); return
		 * revalidateCacheEntry(target, request, context, entry, now); }
		 */else {
			// log.debug("Cache entry not usable; calling backend");
			return callBackend(target, request, context);
		}
		if (context != null) {
			context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, target);
			context.setAttribute(ExecutionContext.HTTP_REQUEST, request);
			context.setAttribute(ExecutionContext.HTTP_RESPONSE, out);
			context.setAttribute(ExecutionContext.HTTP_REQ_SENT, true);
		}
		return out;
	}

	public static final String CACHE_RESPONSE_STATUS = "http.cache.response.status";

	private HttpResponse generateCachedResponse(HttpRequest request,
			HttpContext context, HttpCacheEntry entry, Date now) {
		final HttpResponse cachedResponse;
		if (request.containsHeader(HeaderConstants.IF_NONE_MATCH)
				|| request.containsHeader(HeaderConstants.IF_MODIFIED_SINCE)) {
			cachedResponse = responseGenerator
					.generateNotModifiedResponse(entry);
		} else {
			cachedResponse = responseGenerator.generateResponse(entry);
		}
		setResponseStatus(context, CacheResponseStatus.CACHE_HIT);
		if (validityPolicy.getStalenessSecs(entry, now) > 0L) {
			cachedResponse.addHeader(HeaderConstants.WARNING,
					"110 localhost \"Response is stale\"");
		}
		return cachedResponse;
	}

	private void setResponseStatus(final HttpContext context,
			final CacheResponseStatus value) {
		if (context != null) {
			context.setAttribute(CACHE_RESPONSE_STATUS, value);
		}
	}

	private HttpResponse generateGatewayTimeout(HttpContext context) {
		setResponseStatus(context, CacheResponseStatus.CACHE_MODULE_RESPONSE);
		return new BasicHttpResponse(HttpVersion.HTTP_1_1,
				HttpStatus.SC_GATEWAY_TIMEOUT, "Gateway Timeout");
	}

	private HttpResponse handleCacheMiss(HttpHost target, HttpRequest request,
			HttpContext context) throws IOException {
		recordCacheMiss(target, request);

		if (!mayCallBackend(request)) {
			return new BasicHttpResponse(HttpVersion.HTTP_1_1,
					HttpStatus.SC_GATEWAY_TIMEOUT, "Gateway Timeout");
		}

		Map<String, Variant> variants = getExistingCacheVariants(target,
				request);
		if (variants != null && variants.size() > 0) {
			return negotiateResponseFromVariants(target, request, context,
					variants);
		}

		return callBackend(target, request, context);
	}

	HttpResponse negotiateResponseFromVariants(HttpHost target,
			HttpRequest request, HttpContext context,
			Map<String, Variant> variants) throws IOException {
		HttpRequest conditionalRequest = conditionalRequestBuilder
				.buildConditionalRequestFromVariants(request, variants);

		Date requestDate = getCurrentDate();
		HttpResponse backendResponse = client.execute(target,
				conditionalRequest, context);
		Date responseDate = getCurrentDate();

		backendResponse.addHeader("Via", generateViaHeader(backendResponse));

		if (backendResponse.getStatusLine().getStatusCode() != HttpStatus.SC_NOT_MODIFIED) {
			return handleBackendResponse(target, request, requestDate,
					responseDate, backendResponse);
		}

		Header resultEtagHeader = backendResponse
				.getFirstHeader(HeaderConstants.ETAG);
		if (resultEtagHeader == null) {
			// log.warn("304 response did not contain ETag");
			return callBackend(target, request, context);
		}

		String resultEtag = resultEtagHeader.getValue();
		Variant matchingVariant = variants.get(resultEtag);
		if (matchingVariant == null) {
			// log.debug("304 response did not contain ETag matching one sent in If-None-Match");
			return callBackend(target, request, context);
		}

		HttpCacheEntry matchedEntry = matchingVariant.getEntry();

		if (revalidationResponseIsTooOld(backendResponse, matchedEntry)) {
			EntityUtils.consume(backendResponse.getEntity());
			return retryRequestUnconditionally(target, request, context,
					matchedEntry);
		}

		recordCacheUpdate(context);

		HttpCacheEntry responseEntry = getUpdatedVariantEntry(target,
				conditionalRequest, requestDate, responseDate, backendResponse,
				matchingVariant, matchedEntry);

		HttpResponse resp = responseGenerator.generateResponse(responseEntry);
		tryToUpdateVariantMap(target, request, matchingVariant);

		if (shouldSendNotModifiedResponse(request, responseEntry)) {
			return responseGenerator.generateNotModifiedResponse(responseEntry);
		}

		return resp;
	}
	
	 private void tryToUpdateVariantMap(HttpHost target, HttpRequest request,
	            Variant matchingVariant) {
	        try {
	            responseCache.reuseVariantEntryFor(target, request, matchingVariant);
	        } catch (IOException ioe) {
	            //log.warn("Could not update cache entry to reuse variant", ioe);
	        }
	    }
	
	private String generateViaHeader(HttpMessage msg) {

        final ProtocolVersion pv = msg.getProtocolVersion();
        String existingEntry = viaHeaders.get(pv);
        if (existingEntry != null) return existingEntry;

        final VersionInfo vi = VersionInfo.loadVersionInfo("org.apache.http.client", getClass().getClassLoader());
        final String release = (vi != null) ? vi.getRelease() : VersionInfo.UNAVAILABLE;

        String value;
        if ("http".equalsIgnoreCase(pv.getProtocol())) {
            value = String.format("%d.%d localhost (Apache-HttpClient/%s (cache))", pv.getMajor(), pv.getMinor(),
                    release);
        } else {
            value = String.format("%s/%d.%d localhost (Apache-HttpClient/%s (cache))", pv.getProtocol(), pv.getMajor(),
                    pv.getMinor(), release);
        }
        viaHeaders.put(pv, value);

        return value;
    }
	
	private void recordCacheUpdate(HttpContext context) {
        cacheUpdates.getAndIncrement();
        setResponseStatus(context, CacheResponseStatus.VALIDATED);
    }
	
	private HttpResponse retryRequestUnconditionally(HttpHost target,
            HttpRequest request, HttpContext context,
            HttpCacheEntry matchedEntry) throws IOException {
        HttpRequest unconditional = conditionalRequestBuilder
            .buildUnconditionalRequest(request, matchedEntry);
        return callBackend(target, unconditional, context);
    }
	
	private boolean revalidationResponseIsTooOld(HttpResponse backendResponse,
            HttpCacheEntry cacheEntry) {
        final Header entryDateHeader = cacheEntry.getFirstHeader(HTTP.DATE_HEADER);
        final Header responseDateHeader = backendResponse.getFirstHeader(HTTP.DATE_HEADER);
        if (entryDateHeader != null && responseDateHeader != null) {
            try {
                Date entryDate = DateUtils.parseDate(entryDateHeader.getValue());
                Date respDate = DateUtils.parseDate(responseDateHeader.getValue());
                if (respDate.before(entryDate)) return true;
            } catch (DateParseException e) {
                // either backend response or cached entry did not have a valid
                // Date header, so we can't tell if they are out of order
                // according to the origin clock; thus we can skip the
                // unconditional retry recommended in 13.2.6 of RFC 2616.
            }
        }
        return false;
    }
	
	private HttpCacheEntry getUpdatedVariantEntry(HttpHost target,
            HttpRequest conditionalRequest, Date requestDate,
            Date responseDate, HttpResponse backendResponse,
            Variant matchingVariant, HttpCacheEntry matchedEntry) {
        HttpCacheEntry responseEntry = matchedEntry;
        try {
            responseEntry = responseCache.updateVariantCacheEntry(target, conditionalRequest,
                    matchedEntry, backendResponse, requestDate, responseDate, matchingVariant.getCacheKey());
        } catch (IOException ioe) {
            //log.warn("Could not update cache entry", ioe);
        }
        return responseEntry;
    }
	
	private boolean shouldSendNotModifiedResponse(HttpRequest request,
            HttpCacheEntry responseEntry) {
        return (suitabilityChecker.isConditional(request)
                && suitabilityChecker.allConditionalsMatch(request, responseEntry, new Date()));
    }

	private Map<String, Variant> getExistingCacheVariants(HttpHost target,
			HttpRequest request) {
		Map<String, Variant> variants = null;
		try {
			variants = responseCache.getVariantCacheEntriesWithEtags(target,
					request);
		} catch (IOException ioe) {
		}
		return variants;
	}

	Date getCurrentDate() {
		return new Date();
	}

	HttpResponse callBackend(HttpHost target, HttpRequest request,
			HttpContext context) throws IOException {

		Date requestDate = getCurrentDate();

		HttpResponse backendResponse = client.execute(target, request, context);
		// backendResponse.addHeader("Via", generateViaHeader(backendResponse));
		return handleBackendResponse(target, request, requestDate,
				getCurrentDate(), backendResponse);

	}

	HttpResponse handleBackendResponse(HttpHost target, HttpRequest request,
			Date requestDate, Date responseDate, HttpResponse backendResponse)
			throws IOException {

		// responseCompliance.ensureProtocolCompliance(request,
		// backendResponse);

		boolean cacheable = responseCachingPolicy.isResponseCacheable(request,
				backendResponse);
		responseCache.flushInvalidatedCacheEntriesFor(target, request,
				backendResponse);
		if (cacheable
				&& !alreadyHaveNewerCacheEntry(target, request, backendResponse)) {
			try {
				return responseCache.cacheAndReturnResponse(target, request,
						backendResponse, requestDate, responseDate);
			} catch (IOException ioe) {
			}
		}
		if (!cacheable) {
			try {
				responseCache.flushCacheEntriesFor(target, request);
			} catch (IOException ioe) {
			}
		}
		return backendResponse;
	}
	
	private boolean alreadyHaveNewerCacheEntry(HttpHost target, HttpRequest request,
            HttpResponse backendResponse) {
        HttpCacheEntry existing = null;
        try {
            existing = responseCache.getCacheEntry(target, request);
        } catch (IOException ioe) {
            // nop
        }
        if (existing == null) return false;
        Header entryDateHeader = existing.getFirstHeader(HTTP.DATE_HEADER);
        if (entryDateHeader == null) return false;
        Header responseDateHeader = backendResponse.getFirstHeader(HTTP.DATE_HEADER);
        if (responseDateHeader == null) return false;
        try {
            Date entryDate = DateUtils.parseDate(entryDateHeader.getValue());
            Date responseDate = DateUtils.parseDate(responseDateHeader.getValue());
            return responseDate.before(entryDate);
        } catch (DateParseException e) {
            // Empty on Purpose
        }
        return false;
    }

	private boolean mayCallBackend(HttpRequest request) {
		for (Header h : request.getHeaders(HeaderConstants.CACHE_CONTROL)) {
			for (HeaderElement elt : h.getElements()) {
				if ("only-if-cached".equals(elt.getName())) {
					// log.trace("Request marked only-if-cached");
					return false;
				}
			}
		}
		return true;
	}

	private void recordCacheMiss(HttpHost target, HttpRequest request) {
		cacheMisses.getAndIncrement();
	}

	private void recordCacheHit(HttpHost target, HttpRequest request) {
		cacheHits.getAndIncrement();
	}

	private HttpCacheEntry satisfyFromCache(HttpHost target, HttpRequest request) {
		HttpCacheEntry entry = null;
		try {
			entry = responseCache.getCacheEntry(target, request);
		} catch (IOException ioe) {
		}
		return entry;
	}

	@Override
	public <T> T execute(HttpUriRequest request,
			ResponseHandler<? extends T> responseHandler) throws IOException,
			ClientProtocolException {
		return execute(request, responseHandler, null);
	}

	@Override
	public <T> T execute(HttpUriRequest request,
			ResponseHandler<? extends T> responseHandler, HttpContext context)
			throws IOException, ClientProtocolException {
		HttpResponse resp = execute(request, context);
		return handleAndConsume(responseHandler, resp);
	}

	@Override
	public <T> T execute(HttpHost target, HttpRequest request,
			ResponseHandler<? extends T> responseHandler) throws IOException,
			ClientProtocolException {
		return execute(target, request, responseHandler, null);
	}

	@Override
	public <T> T execute(HttpHost target, HttpRequest request,
			ResponseHandler<? extends T> responseHandler, HttpContext context)
			throws IOException, ClientProtocolException {
		HttpResponse resp = execute(target, request, context);
		return handleAndConsume(responseHandler, resp);
	}

	private <T> T handleAndConsume(
			final ResponseHandler<? extends T> responseHandler,
			HttpResponse response) throws Error, IOException {
		T result;
		try {
			result = responseHandler.handleResponse(response);
		} catch (Exception t) {
			HttpEntity entity = response.getEntity();
			try {
				EntityUtils.consume(entity);
			} catch (Exception t2) {
				// Log this exception. The original exception is more
				// important and will be thrown to the caller.
				// this.log.warn("Error consuming content after an exception.",
				// t2);
			}
			if (t instanceof RuntimeException) {
				throw (RuntimeException) t;
			}
			if (t instanceof IOException) {
				throw (IOException) t;
			}
			throw new UndeclaredThrowableException(t);
		}

		// Handling the response was successful. Ensure that the content has
		// been fully consumed.
		HttpEntity entity = response.getEntity();
		EntityUtils.consume(entity);
		return result;
	}
}