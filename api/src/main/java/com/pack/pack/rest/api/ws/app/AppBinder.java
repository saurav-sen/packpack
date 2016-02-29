package com.pack.pack.rest.api.ws.app;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.oauth1.OAuth1Provider;

import com.pack.pack.rest.api.security.oauth1.OAuth10ProviderFactory;

/**
 * 
 * @author Saurav
 *
 */
public class AppBinder extends AbstractBinder {

	@Override
	protected void configure() {
		bindFactory(OAuth10ProviderFactory.class).to(OAuth1Provider.class).in(
				Singleton.class);
	}
}