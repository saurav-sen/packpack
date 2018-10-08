package com.pack.pack.services.ext.text.summerize.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Assert;
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
		
		/*json = new WebDocumentParser()
				.parse("https://timesofindia.indiatimes.com/india/472-Maoist-surrendered-this-year-highest-in-3-years-Govt/articleshow/45276053.cms");
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);

		json = new WebDocumentParser()
				.parse("https://www.thehindu.com/news/national/other-states/police-preventing-people-from-joining-hunger-strike-says-hardik-patel/article24778711.ece?homepage=true");
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser()
				.parse("https://phys.org/news/2018-09-japan-space-robots-asteroid-survey.html");
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);*/
		
		json = new WebDocumentParser()
				.parse("https://www.newscientist.com/article/mg23931960-200-10-mysteries-of-the-universe-how-did-it-all-begin/");
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		/*json = new WebDocumentParser()
				.parse("http://forums.makingmoneywithandroid.com/marketing-methods/31813-cpimobi-scam-alert.html");
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);*/
		
		/*

		
		
		json = new WebDocumentParser()
				.parse("https://qz.com/quartzy/1392005/malaysias-1mdb-scandal-the-hollywood-celebrities-connected-to-jho-low/");
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);

		json = new WebDocumentParser()
				.parse("https://timesofindia.indiatimes.com/city/hyderabad/eye-of-the-matter-25-in-hyderabad-suffer-blurred-vision/articleshow/65816961.cms");
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil
				.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);

		json = new WebDocumentParser()
				.parse("https://timesofindia.indiatimes.com/business/india-business/maharashtra-to-raze-illegal-bungalows-of-nirav-modi-choksi/articleshow/65494911.cms");
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil
				.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);

		json = new WebDocumentParser()
				.parse("https://www.thehindu.com/news/national/other-states/police-preventing-people-from-joining-hunger-strike-says-hardik-patel/article24778711.ece?homepage=true");
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil
				.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser()
				.parse("https://qz.com/india/1370598/google-amazon-and-paytm-want-to-invest-in-indias-future-retail/");
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);*/
	}
	
	public static void main(String[] args) throws IOException {
		String t = new String(Files.readAllBytes(Paths.get("C:\\Users\\CipherCloud\\Desktop\\template.txt")));
		String v = new String(Files.readAllBytes(Paths.get("C:\\Users\\CipherCloud\\Desktop\\value.txt")));
		t = t.replaceAll(Pattern.quote("NEWSFULLTEXT"), Matcher.quoteReplacement(v.replaceAll(" +", " ")));
		System.out.println(t);
	}
}