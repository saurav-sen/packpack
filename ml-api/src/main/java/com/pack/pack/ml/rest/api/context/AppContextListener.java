package com.pack.pack.ml.rest.api.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
public class AppContextListener implements ServletContextListener {

	private static Logger LOG = LoggerFactory
			.getLogger(AppContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LOG.info("Initializing ML Api Context");
		SystemPropertyUtil.init();
		ServiceRegistry.INSTANCE.init();
		LOG.info("Initialized ClassificationEngine");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		LOG.info("Destroying ML Api Context");
		LOG.info("Stopped ClassificationEngine");
	}
}