package com.pack.pack.markup.gen;

import java.util.HashMap;
import java.util.Map;

import com.pack.pack.model.web.JTopic;

/**
 * 
 * @author Saurav
 *
 */
public class MarkupGenerator {

	public static final MarkupGenerator INSTANCE = new MarkupGenerator();

	private static Map<String, IMarkupGenerator> generatorsMap = new HashMap<String, IMarkupGenerator>();
	static {
		generatorsMap.put(JTopic.class.getName(), new TopicPageGenerator());
	}

	MarkupGenerator() {
	}

	public <T> void generateAndUpload(String entityId, Class<T> type)
			throws Exception {
		IMarkupGenerator generator = generatorsMap.get(type.getName());
		if (generator != null) {
			generator.generateAndUpload(entityId);
		}
	}
}