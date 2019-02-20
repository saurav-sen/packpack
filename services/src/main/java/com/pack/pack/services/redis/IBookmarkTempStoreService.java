package com.pack.pack.services.redis;

import com.squill.feed.web.model.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
public interface IBookmarkTempStoreService {

	public JRssFeed getStoredBookmarkIfAny(String link);

	public void storeNewBookmark(String link, JRssFeed feed);
}
