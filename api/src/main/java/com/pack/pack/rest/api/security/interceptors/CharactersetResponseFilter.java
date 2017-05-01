package com.pack.pack.rest.api.security.interceptors;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Saurav
 *
 */
@Provider
public class CharactersetResponseFilter implements ContainerResponseFilter {

	private static final Logger LOG = LoggerFactory
			.getLogger(CharactersetResponseFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {
		MediaType mediaType = responseContext.getMediaType();
		if (mediaType != null) {
			String contentType = mediaType.toString();
			if (contentType != null && !contentType.contains("charset")) {
				LOG.debug("Adding charset=utf-8 in response");
				contentType = contentType + "; charset=utf-8";
				responseContext.getHeaders().putSingle("Content-Type",
						contentType);
			}
		}
	}
}
