package com.squill.og.crawler.text.summarizer;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.og.crawler.hooks.IArticleTextSummarizer;
import com.squill.services.exception.OgCrawlException;

@Component("defaultArticleTextSummarizer")
@Scope("prototype")
public class DefaultArticleTextSummarizer implements IArticleTextSummarizer {

	@Override
	public TextSummarization summarize(String url, String title,
			String description) throws OgCrawlException {
		TextSummarization summarizedText = null;
		try {
			summarizedText = new AylienArticleTextSummarizer().summarize(url);
		} catch (ClientProtocolException e) {
			throw new OgCrawlException("", e.getMessage(), e);
		} catch (IOException e) {
			throw new OgCrawlException("", e.getMessage(), e);
		}
		return summarizedText;
	}

}
