package com.squill.og.crawler.article.taxonomy;

import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.internal.utils.HttpRequestExecutor;
import com.squill.og.crawler.internal.utils.JSONUtil;
import com.squill.og.crawler.internal.utils.ResponseUtil;
import com.squill.og.crawler.text.summarizer.NLPApiConstants;

public class AylienTaxonomyClassifier {
	
	private static final Logger LOG = LoggerFactory.getLogger(AylienTaxonomyClassifier.class);

	public AylienTaxonomyResponse classifyText(String text) throws Exception {
		AylienTaxonomyResponse aylienResponse = null;
		String encodeText = URLEncoder.encode(text, "UTF-8");
		String GET_URL = NLPApiConstants.AYLIEN_TAXONOMY_CLASSIFICATION_API_URL + "?" + "text=" + encodeText;
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
					AylienTaxonomyResponse.class);
		} else {
			LOG.debug("FAILED");
		}
		return aylienResponse;
	}
	
	public AylienTaxonomyResponse classifyUrl(String linkUrl) throws Exception {
		AylienTaxonomyResponse aylienResponse = null;
		String GET_URL = NLPApiConstants.AYLIEN_TAXONOMY_CLASSIFICATION_API_URL + "?" + "url=" + linkUrl;
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
					AylienTaxonomyResponse.class);
		} else {
			LOG.debug("FAILED");
		}
		return aylienResponse;
	}
}
