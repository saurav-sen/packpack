package com.pack.pack.ml.rest.api.context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.unsupervised.attribute.StringToWordVector;

import com.pack.pack.data.upload.CsvUtil;
import com.pack.pack.data.upload.FeedUploadUtil;
import com.pack.pack.model.web.FeedClassifier;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.JRssFeeds;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
public class ClassificationEngine {

	public static final ClassificationEngine INSTANCE = new ClassificationEngine();

	private ExecutorService executorsPool;

	private static final Logger LOG = LoggerFactory
			.getLogger(ClassificationEngine.class);

	private FilteredClassifier classifier;

	private boolean initializationStatus = false;

	private static Lock classifierModelLock = new ReentrantLock();

	private static Lock csvTrainingDataUpdateLock = new ReentrantLock();

	private static final String OG_CLASSIFIER = "og_classifier";

	private static final String CSV_FILE_EXTENSION = ".csv";
	private static final String ARFF_FILE_EXTENSION = ".arff";
	private static final String DAT_FILE_EXTENSION = ".dat";

	private static final String CLASSIFIER_INSTANCE_FILE_NAME = OG_CLASSIFIER
			+ DAT_FILE_EXTENSION;
	private static final String ARFF_FILE_NAME = OG_CLASSIFIER
			+ ARFF_FILE_EXTENSION;
	private static final String CSV_FILE_NAME = OG_CLASSIFIER
			+ CSV_FILE_EXTENSION;

	private static final String UNDERSCORE = "_";
	
	private ClassificationEngine() {
		executorsPool = Executors.newCachedThreadPool();
	}

	public void start() {
		reInitialize();
		LOG.info("======== ClassificationEngine Started Successfully =========");
	}

	public void reInitialize() {
		loadClassifier();
		if (!initializationStatus) {
			throw new RuntimeException(
					"**ERROR:: Failed to initialize ClassificationEngine");
		}
	}

	private void loadClassifier() {
		String classifierInstanceFilePath = SystemPropertyUtil
				.getMlWorkingDirectory()
				+ File.separator
				+ CLASSIFIER_INSTANCE_FILE_NAME;
		LOG.info("======== Loading Classifier From Stored Model @ "
				+ CLASSIFIER_INSTANCE_FILE_NAME + " ========");
		ObjectInputStream inStream = null;
		classifierModelLock.lock();
		try {
			inStream = new ObjectInputStream(new FileInputStream(new File(
					classifierInstanceFilePath)));
			classifier = (FilteredClassifier) inStream.readObject();
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
				if (inStream != null) {
					inStream.close();
				}
			} catch (Exception e) {
				LOG.debug(e.getMessage(), e);
			}
			classifierModelLock.unlock();
		}
		initializationStatus = true;
		LOG.info("======== Successfully Classifier ========");
	}

	public void buildClassifier() {
		BufferedReader buffReader = null;
		ObjectOutputStream outStream = null;
		classifierModelLock.lock();
		try {
			String csvFilePath = SystemPropertyUtil.getMlWorkingDirectory()
					+ File.separator + CSV_FILE_NAME;
			File csvFile = new File(csvFilePath);
			CSVLoader csvLoader = new CSVLoader();
			csvLoader.setSource(csvFile);
			Instances data = csvLoader.getDataSet();

			String arffFilePath = SystemPropertyUtil.getMlWorkingDirectory()
					+ File.separator + ARFF_FILE_NAME;
			File arffFile = new File(arffFilePath);
			if (arffFile.exists()) {
				arffFile.delete();
			}
			ArffSaver arffSaver = new ArffSaver();
			arffSaver.setInstances(data);
			arffSaver.setFile(arffFile);
			arffSaver.setDestination(arffFile);
			arffSaver.writeBatch();

			buffReader = new BufferedReader(new FileReader(new File(
					arffFilePath)));
			ArffReader arffReader = new ArffReader(buffReader);
			Instances trainedDataSet = arffReader.getData();
			LOG.info("===== Loaded trained dataset @ " + ARFF_FILE_NAME
					+ " =====");
			trainedDataSet.setClassIndex(0);

			StringToWordVector stringFilter = new StringToWordVector();
			stringFilter.setAttributeIndices("last");
			FilteredClassifier classifier = new FilteredClassifier();
			classifier.setFilter(stringFilter);
			classifier.setClassifier(new NaiveBayes());

			Evaluation eval = new Evaluation(trainedDataSet);
			eval.crossValidateModel(classifier, trainedDataSet, 4,
					new Random(1));

			LOG.info(eval.toSummaryString());
			LOG.info(eval.toClassDetailsString());
			LOG.info("===== Evaluation on trained dataset completed =====");

			classifier.buildClassifier(trainedDataSet);
			LOG.info("===== Training classifier done successfully =====");

			String classifierInstanceFilePath = SystemPropertyUtil
					.getMlWorkingDirectory()
					+ File.separator
					+ CLASSIFIER_INSTANCE_FILE_NAME;
			outStream = new ObjectOutputStream(new FileOutputStream(new File(
					classifierInstanceFilePath)));
			outStream.writeObject(classifier);
			LOG.info("===== Saved trained classifier model @ "
					+ CLASSIFIER_INSTANCE_FILE_NAME + " =====");
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if (buffReader != null) {
					buffReader.close();
				}
			} catch (Exception e) {
				LOG.error("Problem reading training ARFF file", e);
			}

			try {
				if (outStream != null) {
					outStream.close();
				}
			} catch (Exception e) {
				LOG.error("Problem saving trained classifier model", e);
			}

			classifierModelLock.unlock();
		}
		LOG.info("Successfully trained ClassificationEngine");
	}

	public void stop() {
		/*if (!initializationStatus) {
			// Nothing to stop
			return;
		}*/
		/*boolean stopped = false;
		try {
			stopped = executorsPool.awaitTermination(3, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage(), e);
		}
		if (!stopped) {*/
			executorsPool.shutdownNow();
		//}
		LOG.info("======== ClassificationEngine Stopped Successfully =========");
	}

	public void submitFeeds(final JRssFeeds feeds,
			final FeedStatusListener listener) {
		if (!initializationStatus) {
			throw new RuntimeException(
					"**ERROR:: Failed to initialize ClassificationEngine");
		}
		Future<JRssFeeds> status = executorsPool.submit(new ClassifierTask(
				feeds));
		Thread statusReader = new Thread(new Runnable() {

			@Override
			public void run() {
				long timeout = 2 * 60 * 60 * 1000;
				long counter = 0;
				while (!status.isDone() && counter < timeout) {
					try {
						Thread.sleep(100);
						counter = counter + 100;
					} catch (InterruptedException e) {
						LOG.error(e.getMessage(), e);
					}
				}
				if (counter >= timeout) {
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

	public void uploadPreClassifiedFeeds(final JRssFeeds feeds,
			final FeedStatusListener listener) {
		Future<JRssFeeds> status = executorsPool
				.submit(new PreClassifiedFeedUploadTask(feeds));
		Thread statusReader = new Thread(new Runnable() {

			@Override
			public void run() {
				long timeout = 2 * 60 * 60 * 1000;
				long counter = 0;
				while (!status.isDone() && counter < timeout) {
					try {
						Thread.sleep(100);
						counter = counter + 100;
					} catch (InterruptedException e) {
						LOG.error(e.getMessage(), e);
					}
				}
				if (counter >= timeout) {
					LOG.debug("Timed Out while trying to upload pre-classified feeds");
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

	private void updateCsvTrainingData(List<JRssFeed> feeds) {
		if (feeds == null || feeds.isEmpty())
			return;
		FileWriter csvFileWriter = null;
		csvTrainingDataUpdateLock.lock();
		try {
			Calendar calendar = Calendar.getInstance();
			String csvFileName = new StringBuilder()
					.append(FeedUploadUtil.PRE_CLASSIFIED_FILE_PREFIX)
					.append(calendar.get(Calendar.DAY_OF_MONTH))
					.append(UNDERSCORE).append(calendar.get(Calendar.MONTH))
					.append(UNDERSCORE).append(calendar.get(Calendar.YEAR))
					.append(CSV_FILE_EXTENSION).toString();
			String csvFilePath = SystemPropertyUtil.getMlWorkingDirectory()
					+ File.separator + csvFileName;
			csvFileWriter = new FileWriter(csvFilePath, true);
			for (JRssFeed feed : feeds) {
				/*String text = new StringBuilder().append(feed.getOgUrl())
						.append(EMPTY_SPACE).append(feed.getOgTitle())
						.toString();
				csvFileWriter.append(feed.getOgType())
						.append(CSV_COL_SEPARATOR).append(text)
						.append(CSV_ROW_SEPARATOR);*/
				String text = CsvUtil.toString(feed);
				if(text != null) {
					csvFileWriter.append(text);
				}
			}
			csvFileWriter.flush();
		} catch (IOException e) {
			LOG.error("**ERROR::Failed Updating Training DataSet in CSV");
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if (csvFileWriter != null) {
					csvFileWriter.close();
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			csvTrainingDataUpdateLock.unlock();
		}
	}

	private class ClassifierTask implements Callable<JRssFeeds> {

		private JRssFeeds feeds;

		ClassifierTask(JRssFeeds feeds) {
			this.feeds = feeds;
		}

		@Override
		public JRssFeeds call() throws Exception {
			List<JRssFeed> list = feeds.getFeeds();
			if (list != null && !list.isEmpty()) {
				List<String> classes = new ArrayList<String>();
				FeedClassifier[] feedClassifiers = FeedClassifier.values();
				for (FeedClassifier feedClassifier : feedClassifiers) {
					classes.add(feedClassifier.name());
				}

				Attribute classAttr = new Attribute("class", classes);
				Attribute textAttr = new Attribute("text");
				ArrayList<Attribute> attrList = new ArrayList<Attribute>(4);
				attrList.add(classAttr);
				attrList.add(textAttr);

				int len = list.size();

				Instances to_classify_Instances = new Instances("to_classify",
						attrList, len);
				to_classify_Instances.setClassIndex(0);

				for (int i = 0; i < len; i++) {
					JRssFeed feed = list.get(i);
					String text = new StringBuilder().append(feed.getOgUrl())
							.append(CsvUtil.EMPTY_SPACE).append(feed.getOgTitle())
							.toString();

					Instance dataInstance = new DenseInstance(2);
					dataInstance.setValue(textAttr, text);
					dataInstance.setDataset(to_classify_Instances);

					to_classify_Instances.add(dataInstance);
				}
				for (int i = 0; i < len; i++) {
					double prediction = classifier
							.classifyInstance(to_classify_Instances.instance(i));
					String predictedClass = to_classify_Instances
							.classAttribute().value((int) prediction);
					JRssFeed feed = list.get(i);
					feed.setOgType(predictedClass);
				}
				updateCsvTrainingData(list);
			}
			return feeds;
		}
	}

	private class PreClassifiedFeedUploadTask implements Callable<JRssFeeds> {

		private JRssFeeds feeds;

		PreClassifiedFeedUploadTask(JRssFeeds feeds) {
			this.feeds = feeds;
		}

		@Override
		public JRssFeeds call() throws Exception {
			List<JRssFeed> list = feeds.getFeeds();
			if (list != null && !list.isEmpty()) {
				updateCsvTrainingData(list);
			}
			return feeds;
		}
	}
}