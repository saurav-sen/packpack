package com.pack.pack.feed.selection.strategy;

import java.util.List;
import java.util.Map;

import com.pack.pack.model.web.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
public interface FeedSelectionStrategy {

	public void applyStrategy(Map<String, List<JRssFeed>> feedsMap);

	public Map<String, List<JRssFeed>> getFinalSelection();
}