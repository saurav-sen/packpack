package com.pack.pack.ml.rest.api.filter;

import java.io.IOException;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.oauth.OAuthConstants;
import com.pack.pack.services.redis.RedisCacheService;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
@Provider
public class ApiRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		boolean allow = true;
		String path = requestContext.getUriInfo().getPath();
		String method = requestContext.getMethod();
		if (path.contains("/feeds")) {
			String apiKey = requestContext
					.getHeaderString(OAuthConstants.AUTHORIZATION_HEADER);
			if (!OAuthConstants.RSS_FEED_UPLOAD_API_KEY.equals(apiKey)) {
				allow = false;
			}
		} else if (path.contains("/promote") && HttpMethod.PUT.equalsIgnoreCase(method)) {
			String apiKey = requestContext
					.getHeaderString(OAuthConstants.AUTHORIZATION_HEADER);
			if (!isValidAccessToken(apiKey)) {
				allow = false;
			}
		}

		if (!allow) {
			JStatus status = new JStatus();
			status.setStatus(StatusType.ERROR);
			status.setInfo("Unauthorized access: Permission denied");
			requestContext.abortWith(Response
					.status(Response.Status.UNAUTHORIZED)
					.header("Content-Type", "application/json").entity(status)
					.build());
		}
	}
	
	private boolean isValidAccessToken(String token) {
		RedisCacheService cacheService = ServiceRegistry.INSTANCE
				.findService(RedisCacheService.class);
		boolean exists = cacheService.isKeyExists(token);
		if (exists) {
			cacheService.setTTL(token, 2 * 60 * 60);
		}
		return exists;
	}
}