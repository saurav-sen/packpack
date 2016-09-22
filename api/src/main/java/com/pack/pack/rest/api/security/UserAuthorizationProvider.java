package com.pack.pack.rest.api.security;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

import org.glassfish.jersey.server.oauth1.OAuth1Provider;
import org.glassfish.jersey.server.oauth1.OAuth1Token;

import com.pack.pack.model.web.dto.LoginDTO;
import com.pack.pack.oauth.OAuthConstants;
import com.pack.pack.oauth.registry.TokenRegistry;
import com.pack.pack.oauth.token.AccessToken;
import com.pack.pack.oauth.token.SimplePrinciple;
import com.pack.pack.oauth.token.Token;
import com.pack.pack.rest.api.oauth.provider.jersey.OAuth10SecurityProvider;
import com.pack.pack.security.util.EncryptionUtil;
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

	@POST
	@Produces(value = MediaType.TEXT_PLAIN)
	@Consumes(value = MediaType.APPLICATION_JSON)
	public String login(
			@HeaderParam(OAuthConstants.AUTHORIZATION_HEADER) String requestToken,
			final LoginDTO dto) throws PackPackException {
		try {
			String password = dto.getPassword();
			if(password != null) {
				password = EncryptionUtil.encryptPassword(password);
			}
			Token token = ((OAuth10SecurityProvider) oauthProvider)
					.getRequestToken(requestToken);
			if (token != null
					&& UserAuthenticator.INSTANCE.authenticateUser(
							dto.getUsername(), password)) {
				Principal p = new Principal() {

					@Override
					public String getName() {
						return dto.getUsername();
					}
				};
				Set<String> roles = new HashSet<String>(
						Arrays.asList(new String[] { OAuthConstants.DEFAULT_ROLE }));
				return ((OAuth10SecurityProvider) oauthProvider)
						.authorizeToken(token, p, roles);
			}
			return null;
		} catch (PackPackException e) {
			throw e;
		}
	}

	@GET
	@Path("{username}")
	@Produces(value = MediaType.APPLICATION_JSON)
	public AccessToken relogin(
			@HeaderParam(OAuthConstants.AUTHORIZATION_HEADER) String refreshToken,
			@HeaderParam(OAuthConstants.DEVICE_ID) String deviceId,
			@PathParam("username") final String username)
			throws PackPackException {
		try {
			if (UserAuthenticator.INSTANCE.IsValidRefreshToken(refreshToken,
					username, null)) {
				Token token = ((Token) ((OAuth10SecurityProvider) oauthProvider)
						.newRequestToken(OAuthConstants.DEFAULT_CLIENT_KEY,
								null, new HashMap<String, List<String>>(1)));
				TokenRegistry.INSTANCE.addRequestToken(token);
				SimplePrinciple p = new SimplePrinciple();
				p.setName(username);
				Set<String> roles = new HashSet<String>(
						Arrays.asList(new String[] { OAuthConstants.DEFAULT_ROLE }));
				String verifier = ((OAuth10SecurityProvider) oauthProvider)
						.authorizeToken(token, p, roles);
				OAuth1Token accessToken = ((OAuth10SecurityProvider) oauthProvider)
						.newAccessToken(token, verifier);
				return (AccessToken) accessToken;
			}
			return null;
		} catch (Exception e) {
			throw new PackPackException("TODO", e);
		}
	}
}