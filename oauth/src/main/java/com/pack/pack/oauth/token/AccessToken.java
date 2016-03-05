package com.pack.pack.oauth.token;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

/**
 * 
 * @author Saurav
 *
 */
public class AccessToken extends Token implements DataSerializable {
	
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
	
	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		super.writeData(out);
		out.writeUTF(refreshToken);
	}
	
	@Override
	public void readData(ObjectDataInput in) throws IOException {
		super.readData(in);
		refreshToken = in.readUTF();
	}
}