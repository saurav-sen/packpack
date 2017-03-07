package com.pack.pack.rest.api.security.interceptors;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@CacheControl
public class CacheControlFilter implements ContainerResponseFilter {
	
	private Logger LOG = LoggerFactory.getLogger(CacheControlFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {
		LOG.debug("CacheControlFilter invoked");
		Annotation[] annotations = responseContext.getEntityAnnotations();
		if (annotations != null) {
			LOG.debug("CacheControlFilter annotations.size() = " + annotations.length);
			for (Annotation annotation : annotations) {
				if (annotation.annotationType() == CacheControl.class) {
					CacheControl cacheControl = (CacheControl) annotation;
					StringBuilder headerValue = new StringBuilder(
							cacheControl.type());
					boolean mustRevalidate = cacheControl.mustRevalidate();
					if (mustRevalidate) {
						headerValue = headerValue.append(", ").append(
								"must-revalidate");
					}
					headerValue = headerValue.append(", ").append("max-age= ")
							.append(cacheControl.maxAge());

					LOG.debug("headerValue = " + headerValue);
					responseContext.getHeaders().putSingle(
							HttpHeaders.CACHE_CONTROL, headerValue.toString());
					break;
				}
			}
		}
	}
}