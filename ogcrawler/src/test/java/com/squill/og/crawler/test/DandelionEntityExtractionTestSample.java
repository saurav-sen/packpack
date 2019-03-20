package com.squill.og.crawler.test;

import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.entity.extraction.ExtractedEntityResponse;
import com.squill.og.crawler.internal.utils.HttpRequestExecutor;
import com.squill.og.crawler.internal.utils.ResponseUtil;
import com.squill.og.crawler.text.summarizer.NLPApiConstants;
import com.squill.utils.JSONUtil;

public class DandelionEntityExtractionTestSample {
	
	private static final Logger LOG = LoggerFactory.getLogger(DandelionEntityExtractionTestSample.class);
	
	private static String resolveDandelionRequestUrl_GET(String text) throws Exception {
		StringBuilder url = new StringBuilder(NLPApiConstants.DANDELION_ENTITY_EXTRACTION_API_URL);
		url.append("?");
		url.append("min_confidence=0.6");
		url.append("&");
		url.append("text=").append(URLEncoder.encode(text, "UTF-8"));
		url.append("&");
		url.append("social=False");
		url.append("&");
		url.append("top_entities=4");
		url.append("&");
		url.append("include=image%2Cabstract%2Ctypes%2Ccategories%2Clod");
		url.append("&");
		url.append("token=").append(NLPApiConstants.DANDELION_API_KEY);
		return url.toString();
	}

	public static void main(String[] args) throws Exception {
		String text = "CBSE Class 10 maths paper leak: Board decides against holding re-examination";
		System.out.println(URLEncoder.encode(text, "UTF-8"));
		
		String GET_URL = resolveDandelionRequestUrl_GET(text);
		HttpGet GET = new HttpGet(GET_URL);
		HttpResponse response = new HttpRequestExecutor().GET(GET);
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode == 200) { // HTTP OK
			LOG.debug("SUCCESS");
			String dandelionResponseText = ResponseUtil.getResponseBodyContent(response);
			LOG.debug(dandelionResponseText);
			ExtractedEntityResponse extractedEntityResponseModel = JSONUtil.deserialize(dandelionResponseText,
					ExtractedEntityResponse.class);
		} else {
			LOG.debug("FAILED");
		}
	}

}
