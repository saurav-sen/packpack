package com.pack.pack.feed.selection.strategy.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pack.pack.feed.selection.strategy.FeedSelectionStrategy;
import com.pack.pack.model.web.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
public class NoFilterFeedSelectionStrategy implements FeedSelectionStrategy {
	
	private Map<String, List<JRssFeed>> allFeeds = new HashMap<String, List<JRssFeed>>();

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
			List<JRssFeed> list2 = allFeeds.get(key);
			if(list2 == null) {
				list2 = new LinkedList<JRssFeed>();
				allFeeds.put(key, list2);
			}
			list2.addAll(list);
		}
	}

	@Override
	public Map<String, List<JRssFeed>> getFinalSelection() {
		return allFeeds;
	}

}
