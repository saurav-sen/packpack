package com.squill.broadcast.feed.upload;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.util.SystemPropertyUtil;
import com.squill.broadcast.feed.upload.model.Strategies;
import com.squill.broadcast.feed.upload.model.Strategy;

/**
 * 
 * @author Saurav
 *
 */
public class FeedSelector {

	private static final Logger ERROR = LoggerFactory
			.getLogger(FeedSelector.class);

	public static final FeedSelector INSTANCE = new FeedSelector();

	private Map<String, Class<?>> strategiesMap = new HashMap<String, Class<?>>();

	private FeedSelector() {
		
	}
	
	public void load() {
		//readSelectionStrategies();
		strategiesMap.put("default", RandomFeedSelectionStrategy.class);
		strategiesMap.put("noFilter", NoFilterFeedSelectionStrategy.class);
	}

	public FeedSelectionStrategy createNewStrategy(String name) {
		try {
			Class<?> implClass = strategiesMap.get(name);
			if (implClass == null) {
				return null;
			}
			Object newInstance = implClass.newInstance();
			if (!(newInstance instanceof FeedSelectionStrategy)) {
				ERROR.error(implClass.getName()
						+ " should either implement interface "
						+ FeedSelectionStrategy.class.getName()
						+ " or extend any of its sub-class otherwise");
				return null;
			}
			return (FeedSelectionStrategy) newInstance;
		} catch (InstantiationException e) {
			ERROR.error(e.getMessage(), e);
			return null;
		} catch (IllegalAccessException e) {
			ERROR.error(e.getMessage(), e);
			return null;
		}
	}

	private void readSelectionStrategies() {
		try {
			String location = SystemPropertyUtil.getFeedSelectionStrategyConfigFileLocation();
			File file = new File(location);
			JAXBContext jaxbInstance = JAXBContext.newInstance(
					Strategies.class, Strategy.class);
			Unmarshaller unmarshaller = jaxbInstance.createUnmarshaller();
			Strategies strategies = (Strategies) unmarshaller.unmarshal(file);
			List<Strategy> strategyList = strategies.getStrategy();
			for (Strategy strategy : strategyList) {
				String name = strategy.getName();
				String implClassName = strategy.getImplementation();
				Class<?> implClass = Class.forName(implClassName);
				strategiesMap.put(name, implClass);
			}
		} catch (JAXBException e) {
			ERROR.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			ERROR.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch(Throwable e) {
			ERROR.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
}