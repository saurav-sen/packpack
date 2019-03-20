package com.squill.og.crawler.test;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.squill.og.crawler.internal.utils.EncryptionUtil;
import com.squill.og.crawler.model.WebSpiderTracker;
import com.squill.utils.JSONUtil;

/**
 * 
 * @author Saurav
 *
 */
public class DefaultWebLinkTrackerServiceTest {
	
	private static final String KEY_PREFIX = "TRACK_";

	public static void main(String[] args) throws Exception {
		String key = KEY_PREFIX + EncryptionUtil.generateMD5HashKey("https://www.nytimes.com/interactive/2018/06/02/us/politics/trump-legal-documents.html", false, false);
		RedisClient client2 = RedisClient.create("redis://13.127.38.35");
		StatefulRedisConnection<String,String> connection2 = client2.connect();
		RedisCommands<String,String> sync2 = connection2.sync();
		String json = sync2.get(key);
		System.out.println(json);
		WebSpiderTracker tracker = JSONUtil.deserialize(json, WebSpiderTracker.class);
		System.out.println(tracker.getArticleSummaryText());
	}
}
