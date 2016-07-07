package com.squill.og.crawler.internal;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Saurav
 *
 */
public class ControlledInstantiator implements ThreadFactory {
	
	private AtomicInteger count = new AtomicInteger(0);
	
	private int MAX = 10;

	@Override
	public Thread newThread(Runnable r) {
		Runnable runnable = new RunnableWrapper(this, r);
		Thread newThread = new Thread(runnable);
		while(count.get() >= MAX) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		count.incrementAndGet();
		return newThread;
	}
	
	public void notifyExecutionCompleted() {
		count.decrementAndGet();
	}

	private class RunnableWrapper implements Runnable {
		
		private ControlledInstantiator factory;
		private Runnable runnable;
		
		public RunnableWrapper(ControlledInstantiator factory, Runnable r) {
			this.factory = factory;
			this.runnable = r;
		}
		
		@Override
		public void run() {
			try {
				if(runnable != null) {
					runnable.run();
				}
			} finally {
				factory.notifyExecutionCompleted();
			}
		}
	}
}