package com.pack.pack.services.ext.text.summerize.test;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.ext.text.summerize.WebDocumentParser;
import com.squill.feed.web.model.JRssFeed;

public class WebDocumentParserTest {
	
	private static final Logger $LOG = LoggerFactory
			.getLogger(WebDocumentParserTest.class);

	@Test
	public void test() throws PackPackException {
		JRssFeed json = null;
		json = new WebDocumentParser()
				.parse("https://content.magicbricks.com/property-news/apartment-sizes-shrink-in-7-major-cities-in-5-years/101039.html?fromSite=toi&utm_source=toi&utm_medium=referral&utm_campaign=toi-mb-whitelabe");
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil
				.serialize(json)));

		json = new WebDocumentParser()
				.parse("https://timesofindia.indiatimes.com/business/india-business/maharashtra-to-raze-illegal-bungalows-of-nirav-modi-choksi/articleshow/65494911.cms");
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil
				.serialize(json)));

		json = new WebDocumentParser()
				.parse("https://www.thehindu.com/news/national/other-states/police-preventing-people-from-joining-hunger-strike-says-hardik-patel/article24778711.ece?homepage=true");
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil
				.serialize(json)));
	}
}