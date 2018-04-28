package com.squill.og.crawler.hooks;

import java.util.List;
import java.util.Map;

import com.squill.feed.web.model.JRssFeed;

public interface IApiRequestExecutor {

	public Map<String, List<JRssFeed>> execute(String webApiUniqueID);
}
