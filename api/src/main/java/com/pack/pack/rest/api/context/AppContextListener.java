package com.pack.pack.rest.api.context;

import java.util.UUID;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.pack.pack.oauth.registry.TokenRegistry;
import com.pack.pack.services.aws.S3UploadTaskExecutor;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
public class AppContextListener implements ServletContextListener {
	
	public static void main(String[] args) {
		System.out.println(UUID.randomUUID());
	}
	
	//private static Logger logger = LoggerFactory.getLogger(AppContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			//logger.info("Starting service-registry");
			SystemPropertyUtil.init();
			ServiceRegistry.INSTANCE.init();
			TokenRegistry.INSTANCE.start();
			S3UploadTaskExecutor.INSTANCE.start();
			//logger.info("Started service-registry, successfully");
			//logger.info("Starting event-manager & registering generic listener");
			//logger.info("Started event-manager & registered generic listener, successfully");
		} catch (Exception e) {
			//logger.error("Failed to start application: " + e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		S3UploadTaskExecutor.INSTANCE.stop();
		ServiceRegistry.INSTANCE.stop();
	}
}