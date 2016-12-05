package com.pack.pack.rest.api.security.interceptors;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.oauth.OAuthConstants;

/**
 * 
 * @author Saurav
 *
 */
@Provider
public class RssFeedUploadFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		boolean allow = true;
		String path = requestContext.getUriInfo().getPath();
		if(path.endsWith("/home/bulk_upload")) {
			String apiKey = requestContext
					.getHeaderString(OAuthConstants.AUTHORIZATION_HEADER);
			if(!OAuthConstants.RSS_FEED_UPLOAD_API_KEY.equals(apiKey)) {
				allow = false;
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