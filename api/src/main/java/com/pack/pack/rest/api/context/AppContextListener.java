package com.pack.pack.rest.api.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.pack.pack.event.IEventListener;
import com.pack.pack.event.MsgEvent;
import com.pack.pack.services.registry.EventManager;

/**
 * 
 * @author Saurav
 *
 */
public class AppContextListener implements ServletContextListener, IEventListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			EventManager.INSTANCE.registerListener(this);
		} catch (InterruptedException e) {
			e.printStackTrace();
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