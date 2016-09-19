package com.pack.pack.client.internal;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;

/**
 * 
 * @author Saurav
 *
 */
public class GZipUtil {

	private GZipUtil() {
		
	}
	
	public static HttpEntity decompress(HttpEntity httpEntity) {
		if(httpEntity == null)
			return httpEntity;
		Header contentEncoding = httpEntity.getContentEncoding();
		if(contentEncoding == null)
			return httpEntity;
		HeaderElement[] headerElements = contentEncoding.getElements();
		if(headerElements == null)
			return httpEntity;
		for(HeaderElement headerElement : headerElements) {
			if("gzip".equalsIgnoreCase(headerElement.getName())) {
				httpEntity = new GzipDecompressingEntity(httpEntity);
				break;
			}
		}
		return httpEntity;
	}
	
	public static HttpEntity compress(HttpEntity httpEntity) {
		return new GzipCompressingEntity(httpEntity);
	}
}