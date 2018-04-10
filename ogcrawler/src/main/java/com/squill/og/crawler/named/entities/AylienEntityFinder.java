package com.squill.og.crawler.named.entities;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.internal.utils.HttpRequestExecutor;
import com.squill.og.crawler.internal.utils.JSONUtil;
import com.squill.og.crawler.internal.utils.ResponseUtil;
import com.squill.og.crawler.text.summarizer.NLPApiConstants;
import com.squill.services.exception.OgCrawlException;

public class AylienEntityFinder {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(AylienEntityFinder.class);

	private static String resolveAylienRequestUrl_GET(String url) {
		return NLPApiConstants.AYLIEN_ENTITIES_API_URL + "?" + "url="
				+ url;
	}

	public AylienEntitiesResponse findNames(String url) throws ClientProtocolException,
			IOException, OgCrawlException {
		AylienEntitiesResponse aylienResponse = null;
		String GET_URL = resolveAylienRequestUrl_GET(url);
		HttpGet GET = new HttpGet(GET_URL);
		GET.addHeader("X-AYLIEN-TextAPI-Application-Key", NLPApiConstants.AYLIEN_API_KEY);
		GET.addHeader("X-AYLIEN-TextAPI-Application-ID", NLPApiConstants.AYLIEN_API_APP_ID);
		HttpResponse response = new HttpRequestExecutor().GET(GET);
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode == 200) { // HTTP OK
			LOG.debug("SUCCESS");
			String textSummary = ResponseUtil.getResponseBodyContent(response);
			LOG.debug(textSummary);
			aylienResponse = JSONUtil.deserialize(textSummary,
					AylienEntitiesResponse.class);
		} else {
			LOG.debug("FAILED");
		}
		return aylienResponse;
	}
}
