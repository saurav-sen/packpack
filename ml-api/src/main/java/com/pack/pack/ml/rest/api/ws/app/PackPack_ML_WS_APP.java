package com.pack.pack.ml.rest.api.ws.app;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * 
 * @author Saurav
 *
 */
public class PackPack_ML_WS_APP extends ResourceConfig {
	
	public PackPack_ML_WS_APP() {
		packages(true, "com.pack.pack.ml.rest.api");
	}
}