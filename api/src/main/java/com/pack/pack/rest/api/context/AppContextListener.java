package com.pack.pack.rest.api.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.event.IEventListener;
import com.pack.pack.event.MsgEvent;
import com.pack.pack.message.listeners.EventListener;
import com.pack.pack.services.registry.EventManager;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
public class AppContextListener implements ServletContextListener, IEventListener {
	
	private static Logger logger = LoggerFactory.getLogger(AppContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			logger.info("Starting service-registry");
			ServiceRegistry.INSTANCE.init();
			logger.info("Started service-registry, successfully");
			logger.info("Starting event-manager & registering generic listener");
			EventManager.INSTANCE.registerListener(new EventListener());
			logger.info("Started event-manager & registered generic listener, successfully");
		} catch (InterruptedException e) {
			logger.error("Failed to start application: " + e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

	@Override
	public void handleEvent(MsgEvent event) {
		// TODO Auto-generated method stub
		
	}
}