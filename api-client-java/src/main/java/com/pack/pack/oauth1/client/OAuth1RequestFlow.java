package com.pack.pack.oauth1.client;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public interface OAuth1RequestFlow {

	public String start() throws ClientProtocolException, IOException;

	public String authorize(String requestToken, String username,
			String password) throws ClientProtocolException, IOException;

	public AccessToken finish(String requestToken, String verifierCode)
			throws ClientProtocolException, IOException;
}
