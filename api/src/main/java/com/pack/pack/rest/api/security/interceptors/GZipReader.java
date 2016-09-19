package com.pack.pack.rest.api.security.interceptors;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@CompressRead
public class GZipReader implements ReaderInterceptor {
	
	public static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
	public static final String GZIP_CONTENT_ENCODING = "gzip";

	@Override
	public Object aroundReadFrom(ReaderInterceptorContext context)
			throws IOException, WebApplicationException {
		MultivaluedMap<String, String> headers = context.getHeaders();
		if (headers.get(CONTENT_ENCODING_HEADER) == null
				|| !headers.get(CONTENT_ENCODING_HEADER).contains(
						GZIP_CONTENT_ENCODING)) {
			return context.proceed();
		}
		InputStream inputStream = context.getInputStream();
		context.setInputStream(new GZIPInputStream(inputStream));
		return context.proceed();
	}
}