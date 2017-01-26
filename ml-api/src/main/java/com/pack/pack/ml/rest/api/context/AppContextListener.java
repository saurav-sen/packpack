package com.pack.pack.ml.rest.api.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.data.upload.PeriodicFeedUploader;
import com.pack.pack.feed.selection.strategy.FeedSelector;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
public class AppContextListener implements ServletContextListener {

	private static Logger LOG = LoggerFactory
			.getLogger(AppContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LOG.info("Initializing ML Api Context");
		SystemPropertyUtil.init();
		String mlServerMode = SystemPropertyUtil.getMlServerMode();
		if (mlServerMode != null
				&& SystemPropertyUtil.ML_SERVER_CLASSIFY_MODE
						.equalsIgnoreCase(mlServerMode.trim())) {
			ClassificationEngine.INSTANCE.start();
		}
		FeedSelector.INSTANCE.load();
		PeriodicFeedUploader.INSTANCE.start();
		LOG.info("Initialized ClassificationEngine");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		LOG.info("Destroying ML Api Context");
		ClassificationEngine.INSTANCE.stop();
		PeriodicFeedUploader.INSTANCE.stop();
		LOG.info("Stopped ClassificationEngine");
	}
}