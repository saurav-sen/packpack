package com.squill.og.crawler.test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.squill.og.crawler.internal.utils.JSONUtil;
import com.squill.og.crawler.iptc.subjectcodes.SubjectCode;
import com.squill.og.crawler.iptc.subjectcodes.SubjectCodeRelationship;
import com.squill.og.crawler.iptc.subjectcodes.SubjectCodes;

public class PrimaryIptcCodeIdentifier {
	
	private PrimaryIptcCodeIdentifier() {
	}

	public static void main(String[] args) throws Exception {
		String resolveConfigFile = "D:\\Saurav\\packpack\\ogcrawler\\src\\conf\\iptc_subject_codes.json";
		String json = new String(Files.readAllBytes(Paths.get(resolveConfigFile)));
		SubjectCodes subjectCodes = JSONUtil.deserialize(json, SubjectCodes.class, false);
		List<SubjectCode> codes = subjectCodes.getCodes();
		List<SubjectCode> result = new ArrayList<SubjectCode>();
		for(SubjectCode code : codes) {
			List<SubjectCodeRelationship> links = code.getRelationships();
			String parentLink = null;
			for(SubjectCodeRelationship link : links) {
				if(SubjectCodeRelationship.PARENT.equals(link.getRelationship())) {
					parentLink = link.getId();
				}
			}
			if(parentLink == null) {
				result.add(code);
			}
		}
		SubjectCodes c = new SubjectCodes();
		c.getCodes().addAll(result);
		resolveConfigFile = "D:\\Saurav\\packpack\\ogcrawler\\src\\conf\\iptc_subject_codes-primary.json";
		Files.write(Paths.get(resolveConfigFile), JSONUtil.serialize(c, false).getBytes());
	}
}
