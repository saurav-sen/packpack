package com.pack.pack.feed.selection.strategy.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.pack.pack.feed.selection.strategy.FeedSelectionStrategy;
import com.pack.pack.model.web.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
public class RandomFeedSelectionStrategy implements FeedSelectionStrategy {

	private Map<String, List<JRssFeed>> randomFeeds = new HashMap<String, List<JRssFeed>>();
	
	@Override
	public void applyStrategy(Map<String, List<JRssFeed>> feedsMap) {
		if(feedsMap == null || feedsMap.isEmpty()) {
			return;
		}
		Iterator<String> itr = feedsMap.keySet().iterator();
		while(itr.hasNext()) {
			String key = itr.next();
			List<JRssFeed> list = feedsMap.get(key);
			if(list == null || list.isEmpty()) {
				continue;
			}
			List<JRssFeed> random10 = randomizeSelection(list, 10);
			List<JRssFeed> list2 = randomFeeds.get(key);
			if(list2 == null) {
				list2 = random10;
				randomFeeds.put(key, list2);
			} else {
				list2.addAll(random10);
			}
		}
	}
	
	private List<JRssFeed> randomizeSelection(List<JRssFeed> originalItems, int maxSelection) {
		int len = originalItems.size();
		int max = len < maxSelection ? len : maxSelection;
		List<JRssFeed> result = new LinkedList<JRssFeed>();
		for(int i=0; i < max; i++) {
			int rand = Math.abs(new Random().nextInt()) % len;
			JRssFeed feed = originalItems.get(rand);
			result.add(feed);
		}
		return result;
	}

	@Override
	public Map<String, List<JRssFeed>> getFinalSelection() {
		Iterator<String> itr = randomFeeds.keySet().iterator();
		while(itr.hasNext()) {
			String key = itr.next();
			List<JRssFeed> list = randomFeeds.get(key);
			list = randomizeSelection(list, 20);
		}
		return randomFeeds;
	}
}