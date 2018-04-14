package com.squill.og.crawler;

import com.squill.og.crawler.hooks.IApiRequestExecutor;

public interface IWebApi extends IWebCrawlable {

	public IApiRequestExecutor getApiExecutor();
}
