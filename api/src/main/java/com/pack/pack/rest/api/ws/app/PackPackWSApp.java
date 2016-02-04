package com.pack.pack.rest.api.ws.app;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.message.listeners.EventListener;
import com.pack.pack.services.registry.EventManager;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
public class PackPackWSApp extends ResourceConfig {
	
	private static Logger logger = LoggerFactory.getLogger(PackPackWSApp.class);

	public PackPackWSApp() {
		logger.info("Starting application");
		packages(true, "com.pack.pack.rest.api");
		try {
			ServiceRegistry.INSTANCE.init();
			EventManager.INSTANCE.registerListener(new EventListener());
			logger.info("Application initialized successfully");
		} catch (Exception e) {
			logger.error("Failed to start application: " + e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}