package com.pack.pack.services.registry;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.pack.pack.event.IEventListener;
import com.pack.pack.event.MsgEvent;

/**
 * 
 * @author Saurav
 *
 */
public class EventManager {
	
	public static final EventManager INSTANCE = new EventManager();
	
	private List<IEventListener> listeners = new LinkedList<IEventListener>();
	
	private Lock lock = new ReentrantReadWriteLock().readLock();
	
	private EventManager() {
	}

	public void fireEvent(MsgEvent event) throws InterruptedException {
		if(listeners.isEmpty()) {
			return;
		}
		while(lock.tryLock(1000, TimeUnit.MILLISECONDS)) {
			try {
				Iterator<IEventListener> itr = listeners.iterator();
				while(itr.hasNext()) {
					IEventListener eventListener = itr.next();
					eventListener.handleEvent(event);
				}
			} finally {
				lock.unlock();
			}
		}
		
	}
	
	public void registerListener(IEventListener listener) throws InterruptedException {
		if(listener == null) {
			return;
		}
		boolean bool = false;
		while(!bool) {
			bool = lock.tryLock(1000, TimeUnit.MILLISECONDS);
		}
		try {
			listeners.add(listener);
		} finally {
			lock.unlock();
		}
	}
}