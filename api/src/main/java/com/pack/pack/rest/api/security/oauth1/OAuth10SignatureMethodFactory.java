package com.pack.pack.rest.api.security.oauth1;

import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.oauth1.signature.HmaSha1Method;
import org.glassfish.jersey.oauth1.signature.OAuth1SignatureMethod;
import org.jvnet.hk2.annotations.Service;

/**
 * 
 * @author Saurav
 *
 */
//@Service
public class OAuth10SignatureMethodFactory implements Factory<OAuth1SignatureMethod> {

	@Override
	public OAuth1SignatureMethod provide() {
		return new HmaSha1Method();
	}

	@Override
	public void dispose(OAuth1SignatureMethod instance) {
	}
}