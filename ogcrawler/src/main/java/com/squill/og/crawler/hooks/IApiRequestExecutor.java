package com.squill.og.crawler.hooks;

import com.squill.og.crawler.model.web.JRssFeeds;

public interface IApiRequestExecutor {

	public JRssFeeds execute(String webApiUniqueID);
}
