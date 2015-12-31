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

import com.pack.pack.model.web.dto.UserDTO;
import com.pack.pack.oauth.token.AccessToken;
import com.pack.pack.services.exception.PackPackException;

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
			@HeaderParam(OAuthConstants.DEVICE_ID) String deviceId,
			UserDTO dto) {
		return UserAuthenticator.INSTANCE.getAccessToken(requestToken,
				dto.getUsername(), dto.getPassword(), deviceId);
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
					.getNewAccessTokenIfRefreshTokenIsValid(refreshToken, username, deviceId);
		} catch (Exception e) {
			throw new PackPackException("TODO", e);
		}
	}
}