package com.pack.pack.oauth.token;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
import org.glassfish.jersey.server.oauth1.OAuth1Consumer;
import org.glassfish.jersey.server.oauth1.OAuth1Provider;
import org.glassfish.jersey.server.oauth1.OAuth1Token;

import com.pack.pack.security.util.OAuth1Util;


/**
 * 
 * @author Saurav
 *
 */
public class Token implements OAuth1Token {

	private final String token;
	private final String secret;
	private final String consumerKey;
	private final String callbackUrl;
	private final Principal principal;
	private final Set<String> roles;
	private final MultivaluedMap<String, String> attribs;
	
	private TTL expiry;
	private long timeOfIssue;
	
	private transient OAuth1Provider provider;

	public Token(final String token, final String secret,
			final String consumerKey, final String callbackUrl,
			final Principal principal, final Set<String> roles,
			final MultivaluedMap<String, String> attributes,
			final OAuth1Provider provider) {
		this.token = token;
		this.secret = secret;
		this.consumerKey = consumerKey;
		this.callbackUrl = callbackUrl;
		this.principal = principal;
		this.roles = roles;
		this.attribs = attributes;
		this.provider = provider;
	}

	public Token(final String token, final String secret,
			final String consumerKey, final String callbackUrl,
			final Map<String, List<String>> attributes, OAuth1Provider provider) {
		this(token, secret, consumerKey, callbackUrl, null, Collections
				.<String> emptySet(),
				new ImmutableMultivaluedMap<String, String>(
						OAuth1Util.getImmutableMap(attributes)), provider);
	}

	public Token(final String token, final String secret,
			final Token requestToken, OAuth1Provider provider) {
		this(token, secret, requestToken.getConsumer().getKey(), null,
				requestToken.principal, requestToken.roles,
				ImmutableMultivaluedMap.<String, String> empty(), provider);
	}

	@Override
	public String getToken() {
		return token;
	}

	@Override
	public String getSecret() {
		return secret;
	}

	@Override
	public OAuth1Consumer getConsumer() {
		return provider.getConsumer(consumerKey);
	}

	@Override
	public MultivaluedMap<String, String> getAttributes() {
		return attribs;
	}

	@Override
	public Principal getPrincipal() {
		return principal;
	}

	@Override
	public boolean isInRole(final String role) {
		return roles.contains(role);
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public Token authorize(final Principal principal,
			final Set<String> roles, final OAuth1Provider provider) {
		return new Token(token, secret, consumerKey, callbackUrl,
				principal, roles == null ? Collections.<String> emptySet()
						: new HashSet<String>(roles), attribs, provider);
	}

	public TTL getExpiry() {
		return expiry;
	}

	public void setExpiry(TTL expiry) {
		this.expiry = expiry;
	}

	public long getTimeOfIssue() {
		return timeOfIssue;
	}

	public void setTimeOfIssue(long timeOfIssue) {
		this.timeOfIssue = timeOfIssue;
	}

	public Set<String> getRoles() {
		return roles;
	}
}