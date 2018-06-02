package com.squill.og.crawler.text.summarizer;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.og.crawler.hooks.IArticleTextSummarizer;
import com.squill.og.crawler.rss.LogTags;
import com.squill.services.exception.OgCrawlException;

@Component("defaultArticleTextSummarizer")
@Scope("prototype")
public class DefaultArticleTextSummarizer implements IArticleTextSummarizer {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultArticleTextSummarizer.class);

	@Override
	public TextSummarization summarize(String url, String title,
			String description) throws OgCrawlException {
		TextSummarization summarizedText = null;
		try {
			summarizedText = new AylienArticleTextSummarizer().summarize(url);
		} catch (ClientProtocolException e) {
			LOG.error(LogTags.TEXT_SUMMARIZATION_ERROR + e.getMessage(), e);
			throw new OgCrawlException("", e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(LogTags.TEXT_SUMMARIZATION_ERROR + e.getMessage(), e);
			throw new OgCrawlException("", e.getMessage(), e);
		}
		LOG.info(LogTags.TEXT_SUMMARIZATION_SUCCESS + "Successfully summarized text");
		return summarizedText;
	}

}
