package com.pack.pack.rest.api.security.interceptors;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.PreMatching;

/**
 * 
 * @author Saurav
 *
 */
@PreMatching
public class PreRequestFilter implements ClientRequestFilter {

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		requestContext.getHeaders().add("Accept-Encoding", "gzip");
	}
}