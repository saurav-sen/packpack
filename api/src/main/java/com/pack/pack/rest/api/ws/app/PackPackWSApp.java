package com.pack.pack.rest.api.ws.app;

import org.glassfish.jersey.server.ResourceConfig;

import com.pack.pack.rest.api.security.oauth1.OAuth10SupportFeature;

/**
 * 
 * @author Saurav
 *
 */
public class PackPackWSApp extends ResourceConfig {
	
	public PackPackWSApp() {
		register(new AppBinder());
		//register(new OAuth10SupportFeature());
		packages(true, "com.pack.pack.rest.api");
	}
}