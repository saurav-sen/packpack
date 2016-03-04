package com.pack.pack.oauth.token;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.server.oauth1.OAuth1Consumer;

import com.pack.pack.security.util.OAuth1Util;

/**
 * 
 * @author Saurav
 *
 */
public class Consumer implements OAuth1Consumer {

	private final String key;
	private final String secret;
	private final String owner;
	private final MultivaluedMap<String, String> attributes;

	public Consumer(final String key, final String secret,
			final String owner, final Map<String, List<String>> attributes) {
		this.key = key;
		this.secret = secret;
		this.owner = owner;
		this.attributes = OAuth1Util.getImmutableMap(attributes);
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getSecret() {
		return secret;
	}

	public String getOwner() {
		return owner;
	}

	public MultivaluedMap<String, String> getAttributes() {
		return attributes;
	}

	@Override
	public Principal getPrincipal() {
		return null;
	}

	@Override
	public boolean isInRole(final String role) {
		return false;
	}
}