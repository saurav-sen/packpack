package com.pack.pack.rest.api.security.interceptors;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

import com.pack.pack.oauth.token.TokenRegistry;
import com.pack.pack.rest.api.security.OAuthConstants;

/**
 * 
 * @author Saurav
 *
 */
public class AccessTokenVerifier implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		boolean allow = true;
		String token = requestContext
				.getHeaderString(OAuthConstants.AUTHORIZATION_HEADER);
		String path = requestContext.getUriInfo().getPath();
		boolean isTokenEmpty = token == null || token.trim().isEmpty();
		if (!path.endsWith(OAuthConstants.OAUTH_REQUEST_TOKEN_PATH)) {
			if(path.endsWith(OAuthConstants.OAUTH_ACCESS_TOKEN_PATH)) {
				allow = isTokenEmpty;
			}
			else if (isTokenEmpty) {
				allow = false;
			} else {
				allow = TokenRegistry.INSTANCE.isValidAccessToken(token);
			}
		}
		if (!allow) {
			requestContext.abortWith(Response
					.status(Response.Status.UNAUTHORIZED)
					.entity("Unauthorized access: Permission denied").build());
		}
	}
}