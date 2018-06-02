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

public final class AylienArticleTextSummarizer {

	private static final Logger LOG = LoggerFactory
			.getLogger(AylienArticleTextSummarizer.class);

	private String resolveAylienRequestUrl_GET(String url) {
		return NLPApiConstants.AYLIEN_SUMMARY_API_URL + "?" + "sentences_number=" + 4 + "&" + "url="
				+ url;
	}

	public TextSummarization summarize(String url) throws ClientProtocolException,
			IOException, OgCrawlException {
		TextSummarization aylienResponse = null;
		String GET_URL = resolveAylienRequestUrl_GET(url);
		HttpGet GET = new HttpGet(GET_URL);
		GET.addHeader("X-AYLIEN-TextAPI-Application-Key", NLPApiConstants.AYLIEN_API_KEY);
		GET.addHeader("X-AYLIEN-TextAPI-Application-ID", NLPApiConstants.AYLIEN_API_APP_ID);
		HttpResponse response = new HttpRequestExecutor().GET(GET);
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode == 200) { // HTTP OK
			LOG.info(LogTags.TEXT_SUMMARIZATION_SUCCESS + "Successfully executed text summarization for content @ " + url);
			String textSummary = ResponseUtil.getResponseBodyContent(response);
			LOG.debug(textSummary);
			aylienResponse = JSONUtil.deserialize(textSummary,
					TextSummarization.class);
		} else {
			LOG.info(LogTags.TEXT_SUMMARIZATION_ERROR + "Failed to summarize for content @ " + url);
			LOG.error(LogTags.TEXT_SUMMARIZATION_ERROR + "HTTP Response Code = " + responseCode);
		}
		return aylienResponse;
	}
}
