package com.squill.og.crawler.entity.extraction;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.internal.utils.HttpRequestExecutor;
import com.squill.og.crawler.internal.utils.JSONUtil;
import com.squill.og.crawler.internal.utils.ResponseUtil;
import com.squill.og.crawler.text.summarizer.AylienConstants;

public class DandelionEntityExtractor {
	
	private static final Logger LOG = LoggerFactory.getLogger(DandelionEntityExtractor.class);
	
	private String resolveDandelionRequestUrl_GET(String text) throws Exception {
		StringBuilder url = new StringBuilder(AylienConstants.DANDELION_ENTITY_EXTRACTION_API_URL);
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
		url.append("token=").append(AylienConstants.DANDELION_API_KEY);
		return url.toString();
	}
	
	private Concept convert(EntityAnnotation annotation) {
		return null;
	}
	
	public List<Concept> extractConcepts(String text) throws Exception {
		List<Concept> concepts = new ArrayList<Concept>();
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
			List<EntityAnnotation> annotations = extractedEntityResponseModel.getAnnotations();
			Map<String, EntityAnnotation> annotationsMap = new HashMap<String, EntityAnnotation>();
			for(EntityAnnotation annotation : annotations) {
				annotationsMap.put(String.valueOf(annotation.getId()), annotation);
			}
			List<TopEntity> topEntities = extractedEntityResponseModel.getTopEntities();
			for(TopEntity topEntity : topEntities) {
				String id = String.valueOf(topEntity.getId());
				EntityAnnotation annotation = annotationsMap.remove(id);
				if(annotation == null)
					continue;
				Concept concept = convert(annotation);
				concepts.add(concept);
			}
			topEntities.clear();
			annotationsMap.clear();
		} else {
			LOG.debug("FAILED");
		}
		return concepts;
	}
}
