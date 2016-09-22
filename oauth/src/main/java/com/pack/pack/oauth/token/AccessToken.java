package com.pack.pack.oauth.token;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;

/**
 * 
 * @author Saurav
 *
 */
public class AccessToken extends Token {
	
	private String refreshToken;
	
	public AccessToken() {
		super();
	}
	
	public AccessToken(final String token, final String secret,
			final String consumerKey, final String callbackUrl,
			final Principal principal, final Set<String> roles,
			final MultivaluedMap<String, String> attributes, 
			String refreshToken) {
		super(token, secret, consumerKey, callbackUrl, principal, roles, attributes);
		this.refreshToken = refreshToken;
	}

	public AccessToken(String token, String secret, String consumerKey,
			String callbackUrl, Map<String, List<String>> attributes, 
			String refreshToken) {
		super(token, secret, consumerKey, callbackUrl, attributes);
		this.refreshToken = refreshToken;
	}
	
	public AccessToken(final String token, final String secret,
			final Token requestToken, String refreshToken) {
		super(token, secret, requestToken.getConsumer().getKey(), null,
				requestToken.getPrincipal(), requestToken.getRoles(),
				ImmutableMultivaluedMap.<String, String> empty());
		this.refreshToken = refreshToken;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public static AccessToken build(AccessTokenInfo info) {
		if (info == null)
			return null;
		return new AccessToken(info.getToken(), info.getSecret(),
				info.getConsumerKey(), info.getCallbackUrl(),
				info.getPrincipal(), new HashSet<String>(info.getRoles()),
				new MultivaluedStringMap(), info.getRefreshToken());
	}
	
	public AccessTokenInfo convert() {
		AccessTokenInfo info = new AccessTokenInfo();
		info.setToken(this.getToken());
		info.setSecret(this.getSecret());
		info.setConsumerKey(this.consumerKey);
		info.setCallbackUrl(this.callbackUrl);
		if (this.getPrincipal() != null) {
			SimplePrinciple p = new SimplePrinciple();
			p.setName(this.getPrincipal().getName());
			info.setPrincipal(p);
		}
		info.setRoles(new ArrayList<String>(this.getRoles()));
		info.setRefreshToken(this.refreshToken);
		return info;
	}
	
	/*@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		super.writeData(out);
		out.writeUTF(refreshToken);
	}
	
	@Override
	public void readData(ObjectDataInput in) throws IOException {
		super.readData(in);
		refreshToken = in.readUTF();
	}*/
}