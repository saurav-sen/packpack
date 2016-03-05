package com.pack.pack.oauth.registry;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.pack.pack.oauth.token.Consumer;

/**
 * 
 * @author Saurav
 *
 */
public class ConsumerRegistry {

	private static final ConcurrentHashMap<String, Consumer> consumerByConsumerKey = new ConcurrentHashMap<String, Consumer>(
			10);
	
	public static final ConsumerRegistry INSTANCE = new ConsumerRegistry();
	
	private ConsumerRegistry() {
	}
	
	public Consumer getConsumer(final String consumerKey) {
		return consumerByConsumerKey.get(consumerKey);
	}
	
	public void registerConsumer(Consumer consumer) {
		consumerByConsumerKey.put(consumer.getKey(), consumer);
	}
	
	public Set<Consumer> getConsumers(final String owner) {
		final Set<Consumer> result = new HashSet<Consumer>();
		for (final Consumer consumer : consumerByConsumerKey.values()) {
			if (consumer.getOwner().equals(owner)) {
				result.add(consumer);
			}
		}
		return result;
	}
}