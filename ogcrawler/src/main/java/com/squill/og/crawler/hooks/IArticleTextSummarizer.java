package com.squill.og.crawler.hooks;

import com.squill.og.crawler.text.summarizer.TextSummarization;
import com.squill.services.exception.OgCrawlException;

public interface IArticleTextSummarizer extends IArticleTextExtractor {

	public TextSummarization summarize(String url, String title,
			String description) throws OgCrawlException;
}
