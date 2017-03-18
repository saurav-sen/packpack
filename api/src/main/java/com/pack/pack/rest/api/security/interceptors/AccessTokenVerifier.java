package com.pack.pack.rest.api.security.interceptors;

import java.io.IOException;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.oauth.OAuthConstants;
import com.pack.pack.oauth.registry.TokenRegistry;
import com.pack.pack.rest.web.util.SystemInfo;

/**
 * 
 * @author Saurav
 *
 */
@Provider
public class AccessTokenVerifier implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		boolean allow = true;
		String token = requestContext
				.getHeaderString(OAuthConstants.AUTHORIZATION_HEADER);
		String path = requestContext.getUriInfo().getPath();
		boolean isTokenEmpty = token == null || token.trim().isEmpty();
		if (!path.endsWith(OAuthConstants.OAUTH_REQUEST_TOKEN_PATH)
				&& !path.endsWith(OAuthConstants.OAUTH_AUTHORIZATION_PATH)
				&& !path.endsWith(OAuthConstants.OAUTH_ACCESS_TOKEN_PATH)
				&& !path.endsWith(SystemInfo.SYSTEM_SUPPORTED_CATEGORIES_INFO_WEB_URL)
				&& !path.endsWith(SystemInfo.NTP_INFO_WEB_URL)) {
			if (isTokenEmpty) {
				allow = false;
			} else {
				allow = TokenRegistry.INSTANCE.isValidAccessToken(token);
			}
			if(path.endsWith("user") && requestContext.getMethod().equals(HttpMethod.POST)) {
				allow = true;				
			}
		}
		if (!allow) {
			JStatus status = new JStatus();
			status.setStatus(StatusType.ERROR);
			status.setInfo("Unauthorized access: Permission denied");
			requestContext.abortWith(Response
					.status(Response.Status.UNAUTHORIZED)
					.header("Content-Type", "application/json")
					.entity(status).build());
		}
	}
}