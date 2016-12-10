package com.pack.pack.ml.rest.api.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 
 * @author Saurav
 *
 */
public class AppContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ClassificationEngine.INSTANCE.start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ClassificationEngine.INSTANCE.stop();
	}
}