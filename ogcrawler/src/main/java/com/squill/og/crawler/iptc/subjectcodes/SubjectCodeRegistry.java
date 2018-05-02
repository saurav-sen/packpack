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
import com.squill.og.crawler.internal.utils.JSONUtil;
import com.squill.services.exception.OgCrawlException;

public final class SubjectCodeRegistry {

	public static final SubjectCodeRegistry INSTANCE = new SubjectCodeRegistry();
	
	private static final Logger LOG = LoggerFactory.getLogger(SubjectCodeRegistry.class);
	
	private Map<String, SubjectCode> registry = new HashMap<String, SubjectCode>();
	
	private static Map<String, JRssFeedType> subjectCodeVsSquillType = new HashMap<String, JRssFeedType>();
	static {
		subjectCodeVsSquillType.put("13000000", JRssFeedType.NEWS_SCIENCE_TECHNOLOGY);
		subjectCodeVsSquillType.put("15000000", JRssFeedType.NEWS_SPORTS);
		//subjectCodeVsSquillType.put("12000000", value); // religion and belief (Spirituality)
	}
	
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
			List<SubjectCodeLink> links = code.getLinks();
			SubjectCodeLink selfLink = null;
			for(SubjectCodeLink link : links) {
				if("self".equals(link.getRel())) {
					selfLink = link;
				} else if("parent".equals(link.getLink())) {
					code.setParentLink(link.getLink());
				}
			}
			if(selfLink == null)
				continue;
			registry.put(selfLink.getLink(), code);
		}
	}
	
	private String resolveConfigFile() {
		String property = System.getProperty(SystemPropertyKeys.WEB_CRAWLERS_CONFIG_DIR);
		if(!property.endsWith(File.separator)) {
			property = property + File.separator;
		}
		return property + "iptc-subject-codes.json";
	}
	
	private SubjectCode resolveRootSubjectCode(String link) {
		SubjectCode subjectCode = registry.get(link);
		String parentLink = null;
		while((parentLink = subjectCode.getParentLink()) != null) {
			subjectCode = registry.get(parentLink);
		}
		return subjectCode;
	}
	
	public JRssFeedType resolveSquillFeedType(JTaxonomy taxonomy) {
		String link = taxonomy.getParentRefUrl() != null ? taxonomy.getParentRefUrl() : taxonomy.getRefUri();
		SubjectCode subjectCode = resolveRootSubjectCode(link);
		return subjectCodeVsSquillType.get(subjectCode.getId());
	}
}
