package com.pack.pack.services.ext.text.summerize.test;

import org.apache.commons.lang.StringEscapeUtils;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.ext.text.summerize.WebDocumentParser;
import com.squill.feed.web.model.JRssFeed;

public class WebDocumentParserTest {

	public static void main(String[] args) throws PackPackException {
		JRssFeed json = new WebDocumentParser()
				.parse("https://timesofindia.indiatimes.com/city/hyderabad/fir-against-rainbow-hospital-for-taking-parking-fee-from-patients/articleshow/65018230.cms");
		System.out.println(StringEscapeUtils.unescapeJava(JSONUtil
				.serialize(json)));
	}
}