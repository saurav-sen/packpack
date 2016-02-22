package com.pack.pack.rest.api.slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * 
 * @author Saurav
 *
 */
public class LogBridgeListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
       /*Logger logger = LogManager.getLogManager().getLogger("");
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            logger.removeHandler(handler);
        }*/
       // SLF4JBridgeHandler.install();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}