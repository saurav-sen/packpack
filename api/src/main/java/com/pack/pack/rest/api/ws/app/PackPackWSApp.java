package com.pack.pack.rest.api.ws.app;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * 
 * @author Saurav
 *
 */
public class PackPackWSApp extends ResourceConfig {

	public PackPackWSApp() {
		packages(true, "com.pack.pack.rest.api");
	}
}