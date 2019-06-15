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
import com.pack.pack.rest.api.FeedUploadResource;
import com.pack.pack.rest.web.util.SystemInfo;

/**
 * 
 * @author Saurav
 *
 */
@Provider
public class UserEmailAuthHeaderVerifier implements ContainerRequestFilter {
	
	/*private static final String PASSWD_RESET_LINK_PATTERN = "passwd/reset";*/
	private static final String SIGNUP_CODE_LINK_PATTERN = "signup/code";
	
	private static final String OPINIONS_UPLOAD_URL_END_PATTERN = "opinions";
	
	private static final String SHARED_URL_TYPE = "/sh/";

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
				&& !path.endsWith(SystemInfo.NTP_INFO_WEB_URL)
				&& !path.endsWith(SystemInfo.ANDROID_APK_URL)
				&& !path.contains(SystemInfo.VALIDATE_USER_NAME_WEB_URL)
				/*&& !path.contains(PASSWD_RESET_LINK_PATTERN)*/
				&& !path.contains(SIGNUP_CODE_LINK_PATTERN)
				&& !path.contains(SHARED_URL_TYPE)
				&& !path.trim().endsWith(OPINIONS_UPLOAD_URL_END_PATTERN)
				&& !path.trim().endsWith(OPINIONS_UPLOAD_URL_END_PATTERN + "/")
				&& !path.trim().contains("electionResult")) {
			if (isTokenEmpty) {
				allow = false;
			} /*else {
				allow = TokenRegistry.INSTANCE.isValidAccessToken(token);
			}*/
			if(path.endsWith("user") && requestContext.getMethod().equals(HttpMethod.POST)) {
				allow = true;				
			}
		} else if (path.endsWith("feeds") || path.endsWith("feeds/")) {
			if(!FeedUploadResource.API_KEY.equals(token)) {
				allow = false;
			}
		} else if (path.trim().endsWith(OPINIONS_UPLOAD_URL_END_PATTERN)
				|| path.trim().endsWith(OPINIONS_UPLOAD_URL_END_PATTERN + "/")
				&& requestContext.getMethod().equals(HttpMethod.POST)) {
			if(!FeedUploadResource.API_KEY.equals(token)) {
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