package com.pack.pack.services.registry;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.redis.RedisCacheService;

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
					appContext = new ClassPathXmlApplicationContext(getServicesXmlPath());
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
	
	private String getServicesXmlPath() {
		if(System.getProperty("service.registry.test") != null) {
			return "META-INF/services_test.xml.txt";
		}
		return "META-INF/services.xml";
	}
	
	public <T> T findService(Class<T> serviceClass) {
		if(appContext == null) {
			throw new RuntimeException("Error initializing spring "
					+ "context for repository services");
		}
		return appContext.getBean(serviceClass);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T findCompositeService(Class<T> serviceInterface) throws PackPackException {
		try {
			String pkgName = serviceInterface.getCanonicalName().substring(0, serviceInterface.getCanonicalName().lastIndexOf(".") + 1);
			String name = serviceInterface.getName();
			name = name.substring(pkgName.length() + 1) + "Impl";
			name = pkgName + "services." + name;
			Class<T> class1 = (Class<T>)Class.forName(name);
			return findService(class1);
		} catch (ClassNotFoundException e) {
			throw new PackPackException("", e.getMessage(), e);
		}
	}
	
	public void stop() {
		RedisCacheService service = findService(RedisCacheService.class);
		if(service != null) {
			service.dispose();
		}
	}
}