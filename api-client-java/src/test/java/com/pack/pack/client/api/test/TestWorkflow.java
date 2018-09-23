package com.pack.pack.client.api.test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestWorkflow {
	
	public static final String BASE_URL = "http://api.squill.in/api/";
	public static final String BASE_URL_2 = "http://www.squill.in/mlapi/";
	
	//public static final String BASE_URL = "http://192.168.35.12:8080/api/";
	//public static final String BASE_URL_2 = "http://192.168.35.12:8080/news/";
	
	private ExecutorService executors = Executors.newCachedThreadPool();
	
	private int initialNumOfSessions;
	private int incrementBy; 
	private int maxNumOfSessions; 
	private int longivityInHours;
	
	private TestWorkflow(int initialNumOfSessions, int incrementBy, int maxNumOfSessions, int longivityInHours) {
		this.initialNumOfSessions = initialNumOfSessions;
		this.incrementBy = incrementBy;
		this.maxNumOfSessions = maxNumOfSessions;
		this.longivityInHours = longivityInHours;
	}
	
	private void execute() {
		long startTime = System.currentTimeMillis();
		long longivityInMillis = longivityInHours * 60 * 60 * 1000;
		int count = 0;
		List<Future<?>> list = new LinkedList<Future<?>>();
		while(count < initialNumOfSessions) {
			Future<?> f = executors.submit(new TestExecutor(new TestSession(count, BASE_URL, BASE_URL_2)));
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count++;
			list.add(f);
		}
		long endTime = System.currentTimeMillis();
		while((endTime - startTime) < longivityInMillis) {
			while(count < maxNumOfSessions) {
				try {
					Thread.sleep(2 * 60 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int i = 0;
				while(i < incrementBy) {
					Future<?> f = executors.submit(new TestExecutor(new TestSession(count, BASE_URL, BASE_URL_2)));
					list.add(f);
				}
				count = count + incrementBy;
			}
			int index = 0;
			for(Future<?> f : list) {
				if(f.isDone()) {
					list.remove(index);
					count--;
					index++;
				}
			}
		}
		while(!list.isEmpty()) {
			int index = 0;
			for(Future<?> f : list) {
				if(f.isDone()) {
					list.remove(index);
					count--;
					index++;
				}
			}
		}
		
		System.out.println("DONE");
	}
	
	private class TestExecutor implements Runnable {
		
		TestSession session;
		
		TestExecutor(TestSession session) {
			this.session = session;
		}
		
		public void run() {
			for (int i = 0; i < 1000; i++) {
				try {
					new TestSessionExecutor().execute(session);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	public static void main(String[] args) {
		new TestWorkflow(100, 50, 500, 2).execute();
	}
}
