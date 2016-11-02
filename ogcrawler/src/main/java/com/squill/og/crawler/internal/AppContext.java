package com.squill.og.crawler.internal;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Saurav
 *
 */
public class AppContext {
	
	public static final AppContext INSTANCE = new AppContext();
	
	private Lock lock = new ReentrantReadWriteLock().readLock();
	
	private ApplicationContext appContext;
	
	private boolean initializedBeans = false;

	private AppContext() {
	}
	
	public AppContext init() {
		if(lock.tryLock()) {
			try {
				if(!initializedBeans) {
					appContext = new ClassPathXmlApplicationContext("META-INF/beans.xml");
					if(appContext == null) {
						throw new RuntimeException("Error initializing spring "
								+ "context for repository services");
					}
					((ClassPathXmlApplicationContext)appContext).registerShutdownHook();
					initializedBeans = true;
				}
			} finally {
				lock.unlock();
			}
		}
		return this;
	}
	
	public <T> T findService(Class<T> serviceClass) {
		if(appContext == null) {
			throw new RuntimeException("Error initializing spring "
					+ "context services");
		}
		return appContext.getBean(serviceClass);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T findService(String id, Class<T> serviceClass) {
		if(appContext == null) {
			throw new RuntimeException("Error initializing spring "
					+ "context services");
		}
		return (T)appContext.getBean(id);
	}
}