package com.squill.og.crawler;

import com.squill.og.crawler.hooks.IArticleTextSummarizer;
import com.squill.og.crawler.hooks.IGeoLocationResolver;
import com.squill.og.crawler.hooks.ITaxonomyResolver;

public interface IWebCrawlable extends ICrawlable {
	
	public IGeoLocationResolver getTargetLocationResolver();

	public ITaxonomyResolver getTaxonomyResolver();

	public IArticleTextSummarizer getArticleTextSummarizer();
}
