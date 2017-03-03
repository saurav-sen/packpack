package com.pack.pack.rest.api.security.interceptors;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@CacheControl
public class CacheControlFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {
		Annotation[] annotations = responseContext.getEntityAnnotations();
		if (annotations != null) {
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

					responseContext.getHeaders().putSingle(
							HttpHeaders.CACHE_CONTROL, headerValue.toString());
					break;
				}
			}
		}
	}
}