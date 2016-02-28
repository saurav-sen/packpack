package com.pack.pack.rest.api.security.oauth1;

import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.server.oauth1.OAuth1Provider;
import org.jvnet.hk2.annotations.Service;

/**
 * 
 * @author Saurav
 *
 */
//@Service
public class OAuth10ProviderFactory implements Factory<OAuth1Provider> {

	@Override
	public OAuth1Provider provide() {
		return new OAuth10SecurityProvider();
	}

	@Override
	public void dispose(OAuth1Provider instance) {
	}
}