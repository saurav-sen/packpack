package com.squill.og.crawler.text.summarizer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.services.ext.text.summerize.DefaultSentenceFinder;
import com.pack.pack.services.ext.text.summerize.ParseMode;
import com.pack.pack.services.ext.text.summerize.WebDocumentParser;
import com.pack.pack.util.SystemPropertyUtil;
import com.squill.feed.web.model.JRssFeed;
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
	
	private boolean tryInHouse(String url) {
		try {
			URL urlNet = new URL(url);
			return !urlNet.getHost().contains("aljazeera.com");// && !urlNet.getHost().contains("nytimes.com");
		} catch (MalformedURLException e) {
			return false;
		}
	}

	@Override
	public ArticleText extractArticle(String url, String title,
			String description, String summaryText) throws OgCrawlException {
		ArticleText articleText = null;
		
		if (tryInHouse(url)) {
			JRssFeed json = new WebDocumentParser(url).setParseMode(
					ParseMode.STRICT).parse();
			if (json != null) {
				articleText = new ArticleText();
				articleText.setTitle(json.getOgTitle());
				articleText.setArticle(json.getFullArticleText());
				articleText.setHtmlSnippet(json.getHtmlSnippet());
				articleText.setAylienBased(false);
				return articleText;
			}
		}
		
		try {
			articleText = new AylienArticleTextExtractor().extract(url);
			articleText.setAylienBased(true);
			String text = articleText.getArticle();
			String openNlpConfDir = SystemPropertyUtil.getOpenNlpConfDir();
			if ((text == null || text.trim().isEmpty())
					&& openNlpConfDir != null) {
				String[] sentences = new DefaultSentenceFinder(openNlpConfDir)
						.findSentences(text);
				if (sentences != null && sentences.length > 0) {
					text = sentences[0];
				} else {
					text = null;
				}
			}
			/*articleText.setTitle(articleText1.getTitle());
			articleText.setArticle(articleText1.getArticle());*/
		} catch (ClientProtocolException e) {
			LOG.error(LogTags.ARTICLE_EXTRACTION_ERROR + e.getMessage(), e);
			throw new OgCrawlException("", e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(LogTags.ARTICLE_EXTRACTION_ERROR + e.getMessage(), e);
			throw new OgCrawlException("", e.getMessage(), e);
		}
		LOG.info(LogTags.ARTICLE_EXTRACTION_SUCCESS + "Successfully extracted article text");
		return articleText;
	}
}
