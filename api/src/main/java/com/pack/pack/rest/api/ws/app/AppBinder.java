package com.pack.pack.rest.api.ws.app;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.oauth1.signature.OAuth1Signature;
import org.glassfish.jersey.oauth1.signature.OAuth1SignatureMethod;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.oauth1.OAuth1Provider;

import com.pack.pack.rest.api.security.oauth1.OAuth10ProviderFactory;
import com.pack.pack.rest.api.security.oauth1.OAuth10SignatureFactory;
import com.pack.pack.rest.api.security.oauth1.OAuth10SignatureMethodFactory;

/**
 * 
 * @author Saurav
 *
 */
public class AppBinder extends AbstractBinder {

	@Override
	protected void configure() {
		bindFactory(OAuth10ProviderFactory.class).to(OAuth1Provider.class).in(
				RequestScoped.class);
		/*bindFactory(OAuth10SignatureFactory.class).to(OAuth1Signature.class)
				.in(RequestScoped.class);
		bindFactory(OAuth10SignatureMethodFactory.class).to(
				OAuth1SignatureMethod.class).in(RequestScoped.class);*/
	}
}