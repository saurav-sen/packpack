package com.pack.pack.rest.api.security;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.oauth1.OAuth1Provider;

import com.pack.pack.model.web.dto.LoginDTO;
import com.pack.pack.oauth.OAuthConstants;
import com.pack.pack.oauth.registry.TokenRegistry;
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
			if (password != null) {
				password = EncryptionUtil.encryptPassword(password);
			}
			Token token = ((OAuth10SecurityProvider) oauthProvider)
					.getRequestToken(requestToken);
			if (token != null
					&& (validateUsernamePassword(dto.getUsername(), password) || validateAccessTokenAndSecret(
							dto.getToken(), dto.getSecret()))) {
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

	private boolean validateUsernamePassword(String username, String password)
			throws PackPackException {
		if (username != null && !username.trim().isEmpty() && password != null
				&& !password.trim().isEmpty()) {
			return UserAuthenticator.INSTANCE.authenticateUser(username,
					password);
		}
		return false;
	}

	private boolean validateAccessTokenAndSecret(String accessToken,
			String secret) throws PackPackException {
		if (accessToken != null && !accessToken.trim().isEmpty()
				&& secret != null && !secret.trim().isEmpty()) {
			return TokenRegistry.INSTANCE.removeRefreshToken(secret,
					accessToken, null);
		}
		return false;
	}
}