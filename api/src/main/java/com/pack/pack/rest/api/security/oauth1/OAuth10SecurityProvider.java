package com.pack.pack.rest.api.security.oauth1;

import javax.ws.rs.core.MultivaluedHashMap;

import org.glassfish.jersey.server.oauth1.DefaultOAuth1Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.rest.api.security.OAuthConstants;
import com.pack.pack.rest.api.security.RequestTokenProvider;

/**
 * 
 * @author Saurav
 *
 */
//@Service
public class OAuth10SecurityProvider extends DefaultOAuth1Provider {
	
	private static Logger logger = LoggerFactory.getLogger(RequestTokenProvider.class);

	public OAuth10SecurityProvider() {
		super();
		registerConsumer("packC", OAuthConstants.DEFAULT_CLIENT_KEY,
				OAuthConstants.DEFAULT_CLIENT_SECRET,
				new MultivaluedHashMap<String, String>());
	}
	
	@Override
	public Token getRequestToken(String token) {
		Token t = super.getRequestToken(token);
		logger.info("t=" + t.getToken());
		return t;
	}
}