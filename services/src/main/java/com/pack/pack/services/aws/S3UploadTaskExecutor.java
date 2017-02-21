package com.pack.pack.services.aws;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Saurav
 *
 */
public class S3UploadTaskExecutor {
	
	public static S3UploadTaskExecutor INSTANCE = new S3UploadTaskExecutor();
	
	private BlockingQueue<UploadTask> taskQueue;
	
	private static Logger LOG = LoggerFactory.getLogger(S3UploadTaskExecutor.class);
	
	private boolean isRunning = false;

	private S3UploadTaskExecutor() {
	}
	
	public void start() {
		isRunning = true;
		taskQueue = new LinkedBlockingQueue<UploadTask>();
		Thread t = new Thread(new Uploader());
		t.start();
	}
	
	public void execute(UploadTask task) {
		if(!isRunning) {
			throw new RuntimeException("S3UploadTaskExecutor is not initialized to be used.");
		}
		try {
			taskQueue.put(task);
		} catch (InterruptedException e) {
			LOG.info(e.getMessage(), e);
		}
	}
	
	public void stop() {
		isRunning = false;
		try {
			taskQueue.put(new UploadTask() {
				
				@Override
				public void execute() throws Exception {
					// Do nothing. This is a dummy task to signal the end.
				}
			});
		} catch (InterruptedException e) {
			LOG.debug(e.getMessage(), e);
		}
	}
	
	private class Uploader implements Runnable {
		@Override
		public void run() {
			while(isRunning) {
				try {
					UploadTask task = taskQueue.take();
					task.execute();
				} catch (Exception e) {
					LOG.info(e.getMessage(), e);
				}
			}
		}
	}
}