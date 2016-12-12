package com.pack.pack.ml.rest.api.context;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import com.pack.pack.model.web.JRssFeeds;

/**
 * 
 * @author Saurav
 *
 */
public class ClassificationEngine {
	
	public static final ClassificationEngine INSTANCE = new ClassificationEngine();
	
	private ExecutorService executorsPool;
	
	private static final Logger LOG = LoggerFactory.getLogger(ClassificationEngine.class);
	
	private ClassificationEngine() {
	}

	public void start() {
		executorsPool = Executors.newCachedThreadPool();
	}
	
	public void stop() {
		boolean stopped = false;
		try {
			stopped = executorsPool.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage(), e);
		}
		if(!stopped) {
			executorsPool.shutdown();
		}
	}
	
	public void submitFeeds(final JRssFeeds feeds, final FeedStatusListener listener) {
		Future<JRssFeeds> status = executorsPool.submit(new ClassifierTask(feeds));
		Thread statusReader = new Thread(new Runnable() {
			
			@Override
			public void run() {
				long timeout = 2*60*60*1000;
				long counter = 0;
				while(!status.isDone() && counter < timeout) {
					try {
						Thread.sleep(100);
						counter = counter + 100;
					} catch (InterruptedException e) {
						LOG.error(e.getMessage(), e);
					}
				}
				if(counter >= timeout) {
					LOG.debug("Timed Out while trying to classify feeds");
					listener.failed(feeds);
				} else {
					try {
						JRssFeeds newFeeds = status.get();
						listener.completed(newFeeds);
					} catch (InterruptedException e) {
						LOG.error(e.getMessage(), e);
						listener.failed(feeds);
					} catch (ExecutionException e) {
						LOG.error(e.getMessage(), e);
						listener.failed(feeds);
					}
				}
			}
		});
		statusReader.start();
	}
	
	private class ClassifierTask implements Callable<JRssFeeds> {
		
		private JRssFeeds feeds;
		
		ClassifierTask(JRssFeeds feeds) {
			this.feeds = feeds;
		}
		
		@Override
		public JRssFeeds call() throws Exception {
			// TODO Auto-generated method stub
			File csvFile = null;
			CSVLoader csvLoader = new CSVLoader();
			csvLoader.setSource(csvFile);
			Instances data = csvLoader.getDataSet();
			
			File arffFile = null;
			ArffSaver arffSaver = new ArffSaver();
			arffSaver.setInstances(data);
			arffSaver.setFile(arffFile);
			arffSaver.setDestination(arffFile);
			arffSaver.writeBatch();
			
			return null;
		}
	}
}