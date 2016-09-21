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

import com.pack.pack.oauth.registry.ConsumerRegistry;
import com.pack.pack.security.util.OAuth1Util;


/**
 * 
 * @author Saurav
 *
 */
public class Token implements OAuth1Token {

	private String token;
	private String secret;
	private String consumerKey;
	private String callbackUrl;
	private Principal principal;
	private Set<String> roles;
	private MultivaluedMap<String, String> attribs;
	
	private TTL expiry;
	private long timeOfIssue;
	
	public Token() {
	}

	public Token(final String token, final String secret,
			final String consumerKey, final String callbackUrl,
			final Principal principal, final Set<String> roles,
			final MultivaluedMap<String, String> attributes) {
		this.token = token;
		this.secret = secret;
		this.consumerKey = consumerKey;
		this.callbackUrl = callbackUrl;
		this.principal = principal;
		this.roles = roles;
		this.attribs = attributes;
	}

	public Token(final String token, final String secret,
			final String consumerKey, final String callbackUrl,
			final Map<String, List<String>> attributes) {
		this(token, secret, consumerKey, callbackUrl, null, Collections
				.<String> emptySet(),
				new ImmutableMultivaluedMap<String, String>(
						OAuth1Util.getImmutableMap(attributes)));
	}

	public Token(final String token, final String secret,
			final Token requestToken) {
		this(token, secret, requestToken.getConsumer().getKey(), null,
				requestToken.principal, requestToken.roles,
				ImmutableMultivaluedMap.<String, String> empty());
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
		return ConsumerRegistry.INSTANCE.getConsumer(consumerKey);
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
		Token t = new Token(token, secret, consumerKey, callbackUrl,
				principal, roles == null ? Collections.<String> emptySet()
						: new HashSet<String>(roles), attribs);
		t.setExpiry(getExpiry());
		t.setTimeOfIssue(getTimeOfIssue());
		return t;
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

	/*@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeUTF(token);
		out.writeUTF(secret);
		out.writeUTF(consumerKey);
		out.writeUTF(callbackUrl);
		String str = principal != null ? (principal.getName() + "") : "NULL";
		out.writeUTF(str);
		int len = roles != null ? roles.size() : 0;
		out.writeInt(len);
		if(roles != null) {
			for(String role : roles) {
				out.writeUTF(role);
			}
		}
		out.writeObject(expiry);
		out.writeLong(timeOfIssue);
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		token = in.readUTF();
		secret = in.readUTF();
		consumerKey = in.readUTF();
		callbackUrl = in.readUTF();
		String username = in.readUTF();
		if(!"NULL".equals(username)) {
			principal = new Principal() {
				
				@Override
				public String getName() {
					return username;
				}
			};
		}
		int len = in.readInt();
		if(len < 0) {
			len = 0;
		}
		roles = new HashSet<String>(len);
		int count = 0;
		while(count < len) {
			String role = in.readUTF();
			roles.add(role);
			count++;
		}
		expiry = in.readObject();
		timeOfIssue = in.readLong();
	}*/
}