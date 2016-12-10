package com.pack.pack.ml.rest.api.slf4j;

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.bridge.SLF4JBridgeHandler;


/**
 * 
 * @author Saurav
 *
 */
public class LogBridgeListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
       Logger logger = LogManager.getLogManager().getLogger("");
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            logger.removeHandler(handler);
        }
       	SLF4JBridgeHandler.install();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}