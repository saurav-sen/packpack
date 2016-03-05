package com.pack.pack.rest.api.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.pack.pack.event.IEventListener;
import com.pack.pack.event.MsgEvent;
import com.pack.pack.oauth.registry.TokenRegistry;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
public class AppContextListener implements ServletContextListener, IEventListener {
	
	//private static Logger logger = LoggerFactory.getLogger(AppContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			//logger.info("Starting service-registry");
			SystemPropertyUtil.init();
			ServiceRegistry.INSTANCE.init();
			TokenRegistry.INSTANCE.start();
			//logger.info("Started service-registry, successfully");
			//logger.info("Starting event-manager & registering generic listener");
			//EventManager.INSTANCE.registerListener(new EventListener());
			//logger.info("Started event-manager & registered generic listener, successfully");
		} catch (Exception e) {
			//logger.error("Failed to start application: " + e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		TokenRegistry.INSTANCE.stop();
	}

	@Override
	public void handleEvent(MsgEvent event) {
		// TODO Auto-generated method stub
		
	}
}