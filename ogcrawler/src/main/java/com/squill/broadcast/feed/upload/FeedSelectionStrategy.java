package com.squill.broadcast.feed.upload;

import java.util.List;
import java.util.Map;

import com.squill.feed.web.model.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
public interface FeedSelectionStrategy {

	public void applyStrategy(Map<String, List<JRssFeed>> feedsMap);

	public Map<String, List<JRssFeed>> getFinalSelection();
}