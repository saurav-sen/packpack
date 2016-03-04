package com.pack.pack.oauth.token;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
import org.glassfish.jersey.server.oauth1.OAuth1Provider;

/**
 * 
 * @author Saurav
 *
 */
public class AccessToken extends Token {
	
	private String refreshToken;
	
	public AccessToken(final String token, final String secret,
			final String consumerKey, final String callbackUrl,
			final Principal principal, final Set<String> roles,
			final MultivaluedMap<String, String> attributes,
			final OAuth1Provider provider, String refreshToken) {
		super(token, secret, consumerKey, callbackUrl, principal, roles, attributes, provider);
		this.refreshToken = refreshToken;
	}

	public AccessToken(String token, String secret, String consumerKey,
			String callbackUrl, Map<String, List<String>> attributes,
			OAuth1Provider provider, String refreshToken) {
		super(token, secret, consumerKey, callbackUrl, attributes, provider);
		this.refreshToken = refreshToken;
	}
	
	public AccessToken(final String token, final String secret,
			final Token requestToken, OAuth1Provider provider, 
			String refreshToken) {
		super(token, secret, requestToken.getConsumer().getKey(), null,
				requestToken.getPrincipal(), requestToken.getRoles(),
				ImmutableMultivaluedMap.<String, String> empty(), provider);
		this.refreshToken = refreshToken;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}