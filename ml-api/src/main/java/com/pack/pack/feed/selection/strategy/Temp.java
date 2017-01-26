package com.pack.pack.feed.selection.strategy;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.pack.pack.feed.selection.model.Strategies;
import com.pack.pack.feed.selection.model.Strategy;

public class Temp {

	public static void main(String[] args) {
		try {
			Map<String, Class<?>> strategiesMap = new HashMap<String, Class<?>>();
			String location = "D:/Saurav/VM/packpack/apache-tomcat-7.0.69/conf/feed-selection-strategy.xml";
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
			System.out.println();
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
