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

import com.squill.feed.web.model.JConcept;
import com.squill.og.crawler.internal.utils.HttpRequestExecutor;
import com.squill.og.crawler.internal.utils.ResponseUtil;
import com.squill.og.crawler.rss.LogTags;
import com.squill.og.crawler.text.summarizer.NLPApiConstants;
import com.squill.utils.JSONUtil;

public class DandelionEntityExtractor {
	
	private static final Logger LOG = LoggerFactory.getLogger(DandelionEntityExtractor.class);
	
	private String resolveDandelionRequestUrl_GET(String text) throws Exception {
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
	
	private JConcept convert(EntityAnnotation annotation, String parentContent) {
		JConcept concept = new JConcept();
		concept.setId(String.valueOf(annotation.getId()));
		concept.setSpot(annotation.getSpot());
		String confidence = annotation.getConfidence();
		if(confidence != null) {
			concept.setConfidence(Double.parseDouble(confidence.trim()));
		}
		concept.setStartIndex(annotation.getStart());
		concept.setEndIndex(annotation.getEnd());
		concept.setOntologyTypes(annotation.getTypes());
		Lod lod = annotation.getLod();
		if(lod != null) {
			String dbpediaRef = lod.getDbpedia();
			concept.setDbpediaRef(dbpediaRef);
		}
		concept.setContent(annotation.getTitle());
		concept.setParentContent(parentContent);
		return concept;
	}
	
	public List<JConcept> extractConcepts(String text) throws Exception {
		List<JConcept> concepts = new ArrayList<JConcept>();
		String GET_URL = resolveDandelionRequestUrl_GET(text);
		HttpGet GET = new HttpGet(GET_URL);
		HttpResponse response = new HttpRequestExecutor().GET(GET);
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode == 200) { // HTTP OK
			LOG.info(LogTags.CONCEPT_EXTRACTION_SUCCESS + " Successfully extracted concepts for \"" + text + "\"");
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
				JConcept concept = convert(annotation, text);
				concepts.add(concept);
			}
			topEntities.clear();
			annotationsMap.clear();
		} else {
			LOG.info(LogTags.CONCEPT_EXTRACTION_ERROR + " failed to extract concepts for \"" + text + "\"");
			LOG.error(LogTags.CONCEPT_EXTRACTION_ERROR + " HTTP Response Code = " + responseCode);
		}
		return concepts;
	}
}
