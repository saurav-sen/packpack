package com.pack.pack.rest.api.security;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.media.sse.SseBroadcaster;

import com.pack.pack.model.User;
import com.pack.pack.model.web.dto.UserDTO;
import com.pack.pack.oauth.token.AccessToken;
import com.pack.pack.oauth.token.TokenRegistry;
import com.pack.pack.rest.api.broadcast.BroadcastManager;
import com.pack.pack.services.couchdb.UserRepositoryService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.rabbitmq.MessageSubscriber;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@Path("/" + OAuthConstants.OAUTH_ACCESS_TOKEN_PATH)
public class AccessTokenProvider {

	@POST
	@Produces(value = MediaType.APPLICATION_JSON)
	@Consumes(value = MediaType.APPLICATION_JSON)
	public AccessToken login(
			@HeaderParam(OAuthConstants.AUTHORIZATION_HEADER) String requestToken,
			@HeaderParam(OAuthConstants.DEVICE_ID) String deviceId, UserDTO dto)
			throws PackPackException {
		AccessToken token = null;
		try {
			token = UserAuthenticator.INSTANCE.getAccessToken(requestToken,
					dto.getUsername(), dto.getPassword(), deviceId);
			UserRepositoryService userService = ServiceRegistry.INSTANCE
					.findService(UserRepositoryService.class);
			User user = userService.getBasedOnUsername(dto.getUsername())
					.get(0);
			MessageSubscriber messageSubscriber = ServiceRegistry.INSTANCE
					.findService(MessageSubscriber.class);
			messageSubscriber.subscribeToChannel(user);
			BroadcastManager.INSTANCE.registerUserSseBroadcaster(user.getId());
			return token;
		} catch (PackPackException e) {
			if (token != null) {
				TokenRegistry.INSTANCE.invalidateAccessToken(token.getToken(),
						dto.getUsername());
			}
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