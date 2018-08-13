package com.squill.og.crawler.hooks;

import com.squill.og.crawler.text.summarizer.ArticleText;
import com.squill.services.exception.OgCrawlException;

public interface IArticleTextExtractor {

	public ArticleText extractArticle(String url, String title,
			String description) throws OgCrawlException;
}
