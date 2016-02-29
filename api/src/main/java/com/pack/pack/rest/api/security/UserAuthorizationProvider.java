package com.pack.pack.rest.api.security;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.oauth1.DefaultOAuth1Provider;
import org.glassfish.jersey.server.oauth1.DefaultOAuth1Provider.Token;
import org.glassfish.jersey.server.oauth1.OAuth1Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.model.web.dto.LoginDTO;
import com.pack.pack.oauth.token.AccessToken;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@Path("/" + OAuthConstants.OAUTH_AUTHORIZATION_PATH)
public class UserAuthorizationProvider {

	@Inject
	private OAuth1Provider oauthProvider;
	
	private Logger logger = LoggerFactory.getLogger(UserAuthorizationProvider.class);

	@POST
	@Produces(value = MediaType.TEXT_PLAIN)
	@Consumes(value = MediaType.APPLICATION_JSON)
	public String login(
			@HeaderParam(OAuthConstants.AUTHORIZATION_HEADER) String requestToken,
			final LoginDTO dto) throws PackPackException {
		// AccessToken token = null;
		try {
			logger.info("I am Here");
			Token token = ((DefaultOAuth1Provider)oauthProvider).getRequestToken(requestToken);
			if (token != null
					&& UserAuthenticator.INSTANCE.authenticateUser(
							dto.getUsername(), dto.getPassword())) {
				Principal p = new Principal() {

					@Override
					public String getName() {
						return dto.getUsername();
					}
				};
				Set<String> roles = new HashSet<String>(
						Arrays.asList(new String[] { "role1" }));
				return ((DefaultOAuth1Provider)oauthProvider).authorizeToken(token, p, roles);
			}
			return null;
		} catch (PackPackException e) {
			/*
			 * if (token != null) {
			 * TokenRegistry.INSTANCE.invalidateAccessToken(token.getToken(),
			 * dto.getUsername()); }
			 */
			throw e;
		}
	}

	@GET
	@Path("{username}")
	@Produces(value = MediaType.APPLICATION_JSON)
	public AccessToken relogin(
			@HeaderParam(OAuthConstants.AUTHORIZATION_HEADER) String refreshToken,
			@HeaderParam(OAuthConstants.DEVICE_ID) String deviceId,
			@PathParam("username") String username) throws PackPackException {
		try {
			return UserAuthenticator.INSTANCE
					.getNewAccessTokenIfRefreshTokenIsValid(refreshToken,
							username, deviceId);
		} catch (Exception e) {
			throw new PackPackException("TODO", e);
		}
	}
}