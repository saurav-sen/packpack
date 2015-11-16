package com.pack.pack.services.registry;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Saurav
 *
 */
public class ServiceRegistry {

	public static final ServiceRegistry INSTANCE = new ServiceRegistry();
	
	private ApplicationContext appContext;
	
	private Lock lock = new ReentrantReadWriteLock().readLock();
	
	private boolean initializedServices = false;
	
	private ServiceRegistry() {
	}
	
	public void init() {
		if(lock.tryLock()) {
			try {
				if(!initializedServices) {
					appContext = new ClassPathXmlApplicationContext("META-INF/services.xml");
					if(appContext == null) {
						throw new RuntimeException("Error initializing spring "
								+ "context for repository services");
					}
					((ClassPathXmlApplicationContext)appContext).registerShutdownHook();
					initializedServices = true;
				}
			} finally {
				lock.unlock();
			}
		}
	}
	
	public <T> T findService(Class<T> serviceClass) {
		if(appContext == null) {
			throw new RuntimeException("Error initializing spring "
					+ "context for repository services");
		}
		return appContext.getBean(serviceClass);
	}
}