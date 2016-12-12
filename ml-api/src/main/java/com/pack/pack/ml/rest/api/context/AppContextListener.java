package com.pack.pack.ml.rest.api.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Saurav
 *
 */
public class AppContextListener implements ServletContextListener {
	
	private static Logger LOG = LoggerFactory.getLogger(AppContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LOG.info("Initializing ML Api Context");
		ClassificationEngine.INSTANCE.start();
		LOG.info("Initialized ClassificationEngine");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		LOG.info("Destroying ML Api Context");
		ClassificationEngine.INSTANCE.stop();
		LOG.info("Stopped ClassificationEngine");
	}
}