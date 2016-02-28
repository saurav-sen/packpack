package com.pack.pack.rest.api.security.oauth1;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.jersey.oauth1.signature.OAuth1Signature;
import org.jvnet.hk2.annotations.Service;

/**
 * 
 * @author Saurav
 *
 */
//@Service
public class OAuth10SignatureFactory implements Factory<OAuth1Signature> {

	@Override
	public OAuth1Signature provide() {
		ServiceLocator serviceLocator = ServiceLocatorFactory.getInstance()
				.create("default");
		return new OAuth1Signature(serviceLocator);
	}

	@Override
	public void dispose(OAuth1Signature instance) {
	}
}