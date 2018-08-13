package com.squill.og.crawler.text.summarizer;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.internal.utils.HttpRequestExecutor;
import com.squill.og.crawler.internal.utils.JSONUtil;
import com.squill.og.crawler.internal.utils.ResponseUtil;
import com.squill.og.crawler.rss.LogTags;
import com.squill.services.exception.OgCrawlException;

public class AylienArticleTextExtractor {

	private static final Logger LOG = LoggerFactory
			.getLogger(AylienArticleTextExtractor.class);

	public ArticleText extract(String url) throws ClientProtocolException,
			IOException, OgCrawlException {
		ArticleText aylienResponse = null;
		String GET_URL = NLPApiConstants.AYLIEN_ARTICLE_EXTRACTOR_API_URL;
		HttpGet GET = new HttpGet(GET_URL);
		GET.addHeader("X-AYLIEN-TextAPI-Application-Key",
				NLPApiConstants.AYLIEN_API_KEY);
		GET.addHeader("X-AYLIEN-TextAPI-Application-ID",
				NLPApiConstants.AYLIEN_API_APP_ID);
		HttpResponse response = new HttpRequestExecutor().GET(GET);
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode == 200) { // HTTP OK
			LOG.info(LogTags.ARTICLE_EXTRACTION_ERROR
					+ "Successfully executed article text for content @ " + url);
			String articleExtractResponseBody = ResponseUtil
					.getResponseBodyContent(response);
			LOG.debug(articleExtractResponseBody);
			aylienResponse = JSONUtil.deserialize(articleExtractResponseBody,
					ArticleText.class);
		} else {
			LOG.info(LogTags.ARTICLE_EXTRACTION_ERROR
					+ "Failed to extract article text for content @ " + url);
			LOG.error(LogTags.ARTICLE_EXTRACTION_ERROR
					+ "HTTP Response Code = " + responseCode);
		}
		return aylienResponse;
	}
}
