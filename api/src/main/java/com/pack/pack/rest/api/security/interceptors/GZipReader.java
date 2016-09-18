package com.pack.pack.rest.api.security.interceptors;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.WebApplicationException;
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

	@Override
	public Object aroundReadFrom(ReaderInterceptorContext context)
			throws IOException, WebApplicationException {
		InputStream inputStream = context.getInputStream();
		context.setInputStream(new GZIPInputStream(inputStream));
		return context.proceed();
	}
}