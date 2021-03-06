package com.pack.pack.rest.api.oauth.provider.jersey;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.oauth1.OAuth1Provider;
import org.glassfish.jersey.server.oauth1.OAuth1Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.oauth.OAuthConstants;
import com.pack.pack.oauth.registry.ConsumerRegistry;
import com.pack.pack.oauth.registry.TokenRegistry;
import com.pack.pack.oauth.token.AccessToken;
import com.pack.pack.oauth.token.Consumer;
import com.pack.pack.oauth.token.KeyGenerator;
import com.pack.pack.oauth.token.TTL;
import com.pack.pack.oauth.token.Token;

/**
 * 
 * @author Saurav
 *
 */
@Provider
public class OAuth10SecurityProvider implements OAuth1Provider {

	private static Logger logger = LoggerFactory
			.getLogger(OAuth10SecurityProvider.class);

	public OAuth10SecurityProvider() {
		super();
		registerConsumer("packCL", OAuthConstants.DEFAULT_CLIENT_KEY,
				OAuthConstants.DEFAULT_CLIENT_SECRET,
				new MultivaluedHashMap<String, String>());
	}

	@Override
	public Consumer getConsumer(final String consumerKey) {
		return ConsumerRegistry.INSTANCE.getConsumer(consumerKey);
	}

	public Consumer registerConsumer(final String owner,
			final MultivaluedMap<String, String> attributes) {
		return registerConsumer(owner, newUUIDString(), newUUIDString(),
				attributes);
	}

	public Consumer registerConsumer(final String owner, final String key,
			final String secret, final MultivaluedMap<String, String> attributes) {
		final Consumer c = new Consumer(key, secret, owner, attributes);
		ConsumerRegistry.INSTANCE.registerConsumer(c);
		return c;
	}

	public Set<Consumer> getConsumers(final String owner) {
		return ConsumerRegistry.INSTANCE.getConsumers(owner);
	}

	public String authorizeToken(final Token token,
			final Principal userPrincipal, final Set<String> roles) {
		final Token authorized = token.authorize(userPrincipal, roles, this);
		TokenRegistry.INSTANCE.addRequestToken(authorized);
		final String verifier = newVerifier();
		TokenRegistry.INSTANCE.addVerifier(token.getToken(), verifier);
		return verifier;
	}

	public void revokeAccessToken(final String token, final String principalName) {
		final Token t = (Token) getAccessToken(token);
		if (t != null && t.getPrincipal().getName().equals(principalName)) {
			TokenRegistry.INSTANCE.invalidateAccessToken(token);
		}
	}

	protected String newUUIDString() {
		try {
			return new KeyGenerator().generateNewToken();
		} catch (Exception e) {
			logger.error("Error generating new token", e.getCause(), e);
			final String tmp = UUID.randomUUID().toString();
			return tmp.replaceAll("-", "");
		}
	}
	
	protected String newVerifier() {
		try {
			return new KeyGenerator().generateNewOTPHashKey();
		} catch (Exception e) {
			logger.error("Error generating new verifier token", e.getCause(), e);
			final String tmp = UUID.randomUUID().toString();
			return tmp.replaceAll("-", "");
		}
	}

	@Override
	public Token getRequestToken(final String token) {
		return TokenRegistry.INSTANCE.serviceRequestToken(token);
	}

	@Override
	public OAuth1Token newRequestToken(final String consumerKey,
			final String callbackUrl, final Map<String, List<String>> attributes) {
		final Token rt = new Token(newUUIDString(), newUUIDString(),
				consumerKey, callbackUrl, attributes);
		rt.setExpiry(new TTL(2, TimeUnit.HOURS));
		rt.setTimeOfIssue(System.currentTimeMillis());
		TokenRegistry.INSTANCE.addRequestToken(rt);
		return rt;
	}

	@Override
	public OAuth1Token newAccessToken(final OAuth1Token requestToken,
			final String verifier) {
		logger.info("Verifier received: " + verifier);
		String expectedVerifier = TokenRegistry.INSTANCE
				.removeVerifier(requestToken.getToken());
		logger.info("Expected verifier: " + expectedVerifier);
		if (verifier == null || requestToken == null
				|| !verifier.equals(expectedVerifier)) {
			return null;
		}
		Token token = TokenRegistry.INSTANCE
				.invalidateRequestToken(requestToken.getToken());
		if (token == null) {
			return null;
		}
		String refreshToken = newUUIDString();
		AccessToken at = new AccessToken(newUUIDString(), refreshToken,
				token, refreshToken);
		at.setExpiry(new TTL(2, TimeUnit.HOURS));
		at.setTimeOfIssue(System.currentTimeMillis());
		TokenRegistry.INSTANCE.addAccessToken(at);
		return at;
	}

	public void addAccessToken(final String token, final String secret,
			final String consumerKey, final String callbackUrl,
			final Principal principal, final Set<String> roles,
			final MultivaluedMap<String, String> attributes) {
		String refreshToken = newUUIDString();
		AccessToken accessToken = new AccessToken(token, secret, consumerKey,
				callbackUrl, principal, roles, attributes, refreshToken);
		accessToken.setExpiry(new TTL(2, TimeUnit.HOURS));
		accessToken.setTimeOfIssue(System.currentTimeMillis());
		TokenRegistry.INSTANCE.addAccessToken(accessToken);
	}

	@Override
	public OAuth1Token getAccessToken(final String token) {
		return TokenRegistry.INSTANCE.getAccessToken(token);
	}
}