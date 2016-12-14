package com.pack.pack.ml.rest.api.context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.unsupervised.attribute.StringToWordVector;

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
	
	private FilteredClassifier classifier;
	
	private boolean initializationStatus = false;
	
	private static Lock lock_0 = new ReentrantLock();
	
	private ClassificationEngine() {
	}

	public void start() {
		reInitialize();
		executorsPool = Executors.newCachedThreadPool();
		LOG.info("======== ClassificationEngine Started Successfully =========");
	}
	
	public void reInitialize() {
		loadClassifier();
		if(!initializationStatus) {
			throw new RuntimeException("**ERROR:: Failed to initialize ClassificationEngine");
		}
	}
	
	private void loadClassifier() {
		String fileName = "";
		LOG.info("======== Loading Classifier From Stored Model @ " + fileName + " ========");
		ObjectInputStream inStream = null;
		try {
			inStream = new ObjectInputStream(new FileInputStream(new File(fileName)));
			classifier = (FilteredClassifier)inStream.readObject();
		} catch (FileNotFoundException e) {
			LOG.error("Problem Loading Classifier", e);
			return;
		} catch (IOException e) {
			LOG.error("Problem Loading Classifier", e);
			return;
		} catch (ClassNotFoundException e) {
			LOG.error("Problem Loading Classifier", e);
			return;
		} finally {
			try {
				if(inStream != null) {
					inStream.close();
				}
			} catch (IOException e) {
				LOG.debug(e.getMessage(), e);
			}
		}
		initializationStatus = true;
		LOG.info("======== Successfully Classifier ========");
	}
	
	public void trainEngine() {
		LOG.info("Trying to train ClassificationEngine");
		while(!lock_0.tryLock()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				LOG.debug(e.getMessage(), e);
			}
		}
		BufferedReader buffReader = null;
		ObjectOutputStream outStream = null;
		try {
			String fileName = "";
			buffReader = new BufferedReader(new FileReader(new File(fileName)));
			ArffReader arffReader = new ArffReader(buffReader);
			Instances trainedDataSet = arffReader.getData();
			LOG.info("===== Loaded trained dataset @ " + fileName + " =====");
			trainedDataSet.setClassIndex(0);
			
			StringToWordVector stringFilter = new StringToWordVector();
			stringFilter.setAttributeIndices("last");
			FilteredClassifier classifier = new FilteredClassifier();
			classifier.setFilter(stringFilter);
			classifier.setClassifier(new NaiveBayes());
			
			Evaluation eval = new Evaluation(trainedDataSet);
			eval.crossValidateModel(classifier, trainedDataSet, 4, new Random(1));
			
			LOG.info(eval.toSummaryString());
			LOG.info(eval.toClassDetailsString());
			LOG.info("===== Evaluation on trained dataset completed =====");
			
			classifier.buildClassifier(trainedDataSet);
			LOG.info("===== Training classifier done successfully =====");
			
			fileName = "";
			outStream = new ObjectOutputStream(new FileOutputStream(new File(fileName)));
			outStream.writeObject(classifier);
 			System.out.println("===== Saved trained classifier model @ " + fileName + " =====");
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if(buffReader != null) {
					buffReader.close();
				}
			} catch (IOException e) {
				LOG.error("Problem reading training ARFF file", e);
			}
			
			try {
				if(outStream != null) {
					outStream.close();
				}
			} catch (IOException e) {
				LOG.error("Problem saving trained classifier model", e);
			}
			
			lock_0.unlock();
		}
		LOG.info("Successfully trained ClassificationEngine");
	}
	
	public void stop() {
		if(!initializationStatus) {
			// Nothing to stop
			return;
		}
		boolean stopped = false;
		try {
			stopped = executorsPool.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage(), e);
		}
		if(!stopped) {
			executorsPool.shutdown();
		}
		LOG.info("======== ClassificationEngine Stopped Successfully =========");
	}
	
	public void submitFeeds(final JRssFeeds feeds, final FeedStatusListener listener) {
		if(!initializationStatus) {
			throw new RuntimeException("**ERROR:: Failed to initialize ClassificationEngine");
		}
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