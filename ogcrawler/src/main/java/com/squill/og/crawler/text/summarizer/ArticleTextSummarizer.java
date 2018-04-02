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
import com.squill.services.exception.PackPackException;

public final class ArticleTextSummarizer {

	// private static final String DEEP_API_KEY =
	// "d0eb5617-e7f2-4422-b1a3-e16794709086";

	private static final Logger LOG = LoggerFactory
			.getLogger(ArticleTextSummarizer.class);

	private static String resolveAylienRequestUrl_GET(String url) {
		return AylienConstants.AYLIEN_SUMMARY_API_URL + "?" + "sentences_number=" + 4 + "&" + "url="
				+ url;
	}

	public AylienSummarization summarize(String url) throws ClientProtocolException,
			IOException, PackPackException {
		AylienSummarization aylienResponse = null;
		String GET_URL = resolveAylienRequestUrl_GET(url);
		HttpGet GET = new HttpGet(GET_URL);
		GET.addHeader("X-AYLIEN-TextAPI-Application-Key", AylienConstants.AYLIEN_API_KEY);
		GET.addHeader("X-AYLIEN-TextAPI-Application-ID", AylienConstants.AYLIEN_API_APP_ID);
		HttpResponse response = new HttpRequestExecutor().GET(GET);
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode == 200) { // HTTP OK
			LOG.debug("SUCCESS");
			String textSummary = ResponseUtil.getResponseBodyContent(response);
			LOG.debug(textSummary);
			aylienResponse = JSONUtil.deserialize(textSummary,
					AylienSummarization.class);
		} else {
			LOG.debug("FAILED");
		}
		return aylienResponse;
	}
}
