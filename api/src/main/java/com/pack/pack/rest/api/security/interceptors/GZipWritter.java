package com.pack.pack.rest.api.security.interceptors;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import static com.pack.pack.rest.api.security.interceptors.GZipReader.CONTENT_ENCODING_HEADER;
import static com.pack.pack.rest.api.security.interceptors.GZipReader.GZIP_CONTENT_ENCODING;;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@CompressWrite
public class GZipWritter implements WriterInterceptor {

	@Override
	public void aroundWriteTo(WriterInterceptorContext context)
			throws IOException, WebApplicationException {
		MultivaluedMap<String,Object> headers = context.getHeaders();
		headers.add(CONTENT_ENCODING_HEADER, GZIP_CONTENT_ENCODING);		
		OutputStream outStream = context.getOutputStream();
		context.setOutputStream(new GZIPOutputStream(outStream));
		context.proceed();
	}
}