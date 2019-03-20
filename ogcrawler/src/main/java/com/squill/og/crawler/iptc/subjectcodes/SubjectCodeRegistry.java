package com.squill.og.crawler.iptc.subjectcodes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.feed.web.model.JRssFeedType;
import com.squill.feed.web.model.JTaxonomy;
import com.squill.og.crawler.app.SystemPropertyKeys;
import com.squill.services.exception.OgCrawlException;
import com.squill.utils.JSONUtil;

public final class SubjectCodeRegistry {

	public static final SubjectCodeRegistry INSTANCE = new SubjectCodeRegistry();
	
	private static final Logger LOG = LoggerFactory.getLogger(SubjectCodeRegistry.class);
	
	private Map<String, SubjectCode> registry = new HashMap<String, SubjectCode>();
	
	private SubjectCodeRegistry() {
		try {
			init();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (OgCrawlException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	private void init() throws IOException, OgCrawlException {
		String resolveConfigFile = resolveConfigFile();
		String json = new String(Files.readAllBytes(Paths.get(resolveConfigFile)));
		SubjectCodes subjectCodes = JSONUtil.deserialize(json, SubjectCodes.class, false);
		List<SubjectCode> codes = subjectCodes.getCodes();
		for(SubjectCode code : codes) {
			List<SubjectCodeRelationship> relationships = code.getRelationships();
			SubjectCodeRelationship selfRelationship = null;
			for(SubjectCodeRelationship relationship : relationships) {
				if(SubjectCodeRelationship.SELF.equals(relationship.getRelationship())) {
					selfRelationship = relationship;
				} else if(SubjectCodeRelationship.PARENT.equals(relationship.getRelationship())) {
					code.setParentId(relationship.getId());
				}
			}
			if(selfRelationship == null)
				continue;
			registry.put(selfRelationship.getId(), code);
		}
	}
	
	private String resolveConfigFile() {
		String property = System.getProperty(SystemPropertyKeys.WEB_CRAWLERS_CONFIG_DIR);
		if(!property.endsWith(File.separator)) {
			property = property + File.separator;
		}
		return property + "iptc_subject_codes.json";
	}
	
	private SubjectCode resolveRootSubjectCode(String id) {
		SubjectCode subjectCode = registry.get(id);
		String parentId = null;
		while((parentId = subjectCode.getParentId()) != null) {
			subjectCode = registry.get(parentId);
		}
		return subjectCode;
	}
	
	public JRssFeedType resolveSquillFeedType(JTaxonomy taxonomy) {
		String id = taxonomy.getId();
		SubjectCode subjectCode = resolveRootSubjectCode(id);
		return JRssFeedType.valueOf(subjectCode.getSquillType().toUpperCase());
	}
}
