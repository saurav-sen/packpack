package com.pack.pack.services.ext.text.summerize.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kohlschutter.boilerpipe.BoilerpipeExtractor;
import com.kohlschutter.boilerpipe.extractors.CommonExtractors;
import com.kohlschutter.boilerpipe.sax.HTMLHighlighter;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.services.ext.text.summerize.WebDocumentParser;
import com.pack.pack.util.SystemPropertyUtil;
import com.squill.feed.web.model.JRssFeed;

public class WebDocumentParserTest {
	
	private static final Logger $LOG = LoggerFactory
			.getLogger(WebDocumentParserTest.class);
	
	public static void main(String[] args) throws Exception {
		SystemPropertyUtil.setDefaultOpenNlpConfDir("D:\\Saurav\\packpack\\ogcrawler\\src\\conf");
		
		final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;
		final HTMLHighlighter hh = HTMLHighlighter.newHighlightingInstance();
		
		PrintWriter out = new PrintWriter("D:/Saurav/boilerpipe/tmp/highlighted.html", "UTF-8");
	    out.println("<base href=\"" + "http://time.com/5482842/time-top-10-photos-2018-sudan-northern-white-rhino/" + "\" >");
	    out.println("<meta http-equiv=\"Content-Type\" content=\"text-html; charset=utf-8\" />");
	    out.println(hh.process(new URL("http://time.com/5482842/time-top-10-photos-2018-sudan-northern-white-rhino/"), extractor));
	}

	@Test
	public void test() throws Exception {
		JRssFeed json = null;
		
		SystemPropertyUtil.setDefaultOpenNlpConfDir("D:\\Saurav\\packpack\\ogcrawler\\src\\conf");
		
		final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;
		final HTMLHighlighter hh = HTMLHighlighter.newHighlightingInstance();
	    // final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstance();
		
		/*URL url = new URL(
				"http://blog.openshift.com/day-18-boilerpipe-article-extraction-for-java-developers/");

		System.out.println(ArticleExtractor.INSTANCE.getText(url));*/
		
		
		json = new WebDocumentParser(
				"https://indianexpress.com/article/sports/football/la-liga/real-betis-vs-barcelona-lionel-messi-standing-ovation-video-5631269/")
				.parse();
		$LOG.info(json.getArticleSummaryText());
		$LOG.info(json.getFullArticleText());
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser(
				"https://www.nationalgeographic.com/animals/2019/03/psychology-of-why-people-enter-wild-animal-enclosures-at-zoos.html")
				.parse();
		$LOG.info(json.getArticleSummaryText());
		$LOG.info(json.getFullArticleText());
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser(
				"https://theprint.in/opinion/narendra-modis-junking-of-the-economy-is-like-nero-fiddling-while-rome-burned/201599/")
				.parse();
		$LOG.info(json.getArticleSummaryText());
		$LOG.info(json.getFullArticleText());
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser(
				"https://squillnews.quora.com/A-battle-of-citizenship-that-everyone-fought-in-Assam")
				.parse();
		$LOG.info(json.getArticleSummaryText());
		$LOG.info(json.getFullArticleText());
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser(
				"https://timesofindia.indiatimes.com/sports/cricket/news/icc-wants-say-in-ipl-policy-matters-bcci-says-its-a-domestic-league/articleshow/68248317.cms")
				.parse();
		$LOG.info(json.getArticleSummaryText());
		$LOG.info(json.getFullArticleText());
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser(
				"https://www.nytimes.com/2019/02/23/style/weighted-blankets-sleep.html")
				.parse();
		$LOG.info(json.getArticleSummaryText());
		$LOG.info(json.getFullArticleText());
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser(
				"https://www.aljazeera.com/news/2019/02/lebanon-website-shames-employers-accused-maid-abuse-190221092631522.html")
				.parse();
		$LOG.info(json.getArticleSummaryText());
		$LOG.info(json.getFullArticleText());
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser(
				"https://www.aljazeera.com/news/2019/02/war-afghan-peace-talks-set-kick-doha-190225082154207.html")
				.parse();
		$LOG.info(json.getArticleSummaryText());
		$LOG.info(json.getFullArticleText());
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser(
				"https://timesofindia.indiatimes.com/india/kashmiri-youths-were-in-touch-with-pulwama-attack-mastermind/articleshow/68143847.cms")
				.parse();
		$LOG.info(json.getArticleSummaryText());
		$LOG.info(json.getFullArticleText());
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser(
				"https://qz.com/india/1533408/indigo-ceo-ronojoy-dutta-is-an-iit-and-united-airlines-alumnus/")
				.parse();
		$LOG.info(json.getArticleSummaryText());
		$LOG.info(json.getFullArticleText());
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser(
				"https://thewire.in/rights/sajjan-kumar-1984-violence-sikhs")
				.parse();
		$LOG.info(json.getArticleSummaryText());
		$LOG.info(json.getFullArticleText());
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getOgTitle() != null);

	   /* PrintWriter out = new PrintWriter("D:/Saurav/boilerpipe/tmp/highlighted.html", "UTF-8");
	    out.println("<base href=\"" + "https://www.quora.com/What-is-the-use-of-replacing-one-party-with-an-earlier-party-that-was-replaced-for-the-same-reason-of-failure-and-incompetence-And-with-the-same-despised-leaders-do-elections-really-serve-the-purpose-What-can-be" + "\" >");
	    out.println("<meta http-equiv=\"Content-Type\" content=\"text-html; charset=utf-8\" />");
	    out.println(hh.process(new URL("https://www.quora.com/What-is-the-use-of-replacing-one-party-with-an-earlier-party-that-was-replaced-for-the-same-reason-of-failure-and-incompetence-And-with-the-same-despised-leaders-do-elections-really-serve-the-purpose-What-can-be"), extractor));
		*/
		json = new WebDocumentParser(
				"https://www.quora.com/What-is-the-use-of-replacing-one-party-with-an-earlier-party-that-was-replaced-for-the-same-reason-of-failure-and-incompetence-And-with-the-same-despised-leaders-do-elections-really-serve-the-purpose-What-can-be")
				.parse();
		$LOG.info(json.getArticleSummaryText());
		$LOG.info(json.getFullArticleText());
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getOgTitle() != null);
		
		json = new WebDocumentParser("https://goo.gl/images/eD7vMa").parse();
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue("https://goo.gl/images/eD7vMa".equals(json.getOgUrl()));
		
		json = new WebDocumentParser(
				"http://www.fortworthastro.com/images/bigdipperdirections.gif")
				.parse();
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue("http://www.fortworthastro.com/images/bigdipperdirections.gif".equals(json.getOgImage()));
		
		json = new WebDocumentParser(
				"https://www.financialexpress.com/money/half-of-indias-atms-may-shut-down-by-march-heres-why/1388841/")
				.parse();
		$LOG.info(json.getArticleSummaryText());
		$LOG.info(json.getFullArticleText());
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser(
				"https://www.news18.com/news/buzz/2611-mumbai-indians-pay-tribute-to-undying-spirit-of-city-on-tenth-anniversary-of-terror-attacks-1950577.html")
				.parse();
		$LOG.info(json.getArticleSummaryText());
		$LOG.info(json.getFullArticleText());
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser(
				"https://www.ndtv.com/india-news/jet-airways-passenger-detained-at-kolkata-airport-he-was-reportedly-heard-threatening-to-blow-up-the-1953322")
				.parse();
		$LOG.info(json.getArticleSummaryText());
		$LOG.info(json.getFullArticleText());
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser(
				"https://www.boomlive.in/cyclonegaja-old-video-of-waves-engulfing-bridge-resurfaces-as-rameswaram/")
				.parse();
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);	
		
		/*json = new WebDocumentParser("https://timesofindia.indiatimes.com/india/472-Maoist-surrendered-this-year-highest-in-3-years-Govt/articleshow/45276053.cms")
				.parse();
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);

		json = new WebDocumentParser("https://www.thehindu.com/news/national/other-states/police-preventing-people-from-joining-hunger-strike-says-hardik-patel/article24778711.ece?homepage=true")
				.parse();
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser("https://phys.org/news/2018-09-japan-space-robots-asteroid-survey.html")
				.parse();
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);*/
		
		json = new WebDocumentParser(
				"https://www.newscientist.com/article/mg23931960-200-10-mysteries-of-the-universe-how-did-it-all-begin/")
				.parse();
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);

		json = new WebDocumentParser(
				"https://beautypageants.indiatimes.com/miss-international/i-was-molested-by-a-priest/articleshow/66604021.cms")
				.parse();
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		/*json = new WebDocumentParser("http://forums.makingmoneywithandroid.com/marketing-methods/31813-cpimobi-scam-alert.html")
				.parse();
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);*/
		
		/*

		
		
		json = new WebDocumentParser("https://qz.com/quartzy/1392005/malaysias-1mdb-scandal-the-hollywood-celebrities-connected-to-jho-low/")
				.parse();
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);

		json = new WebDocumentParser("https://timesofindia.indiatimes.com/city/hyderabad/eye-of-the-matter-25-in-hyderabad-suffer-blurred-vision/articleshow/65816961.cms")
				.parse();
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil
				.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);

		json = new WebDocumentParser("https://timesofindia.indiatimes.com/business/india-business/maharashtra-to-raze-illegal-bungalows-of-nirav-modi-choksi/articleshow/65494911.cms")
				.parse();
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil
				.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);

		json = new WebDocumentParser("https://www.thehindu.com/news/national/other-states/police-preventing-people-from-joining-hunger-strike-says-hardik-patel/article24778711.ece?homepage=true")
				.parse();
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil
				.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);
		
		json = new WebDocumentParser("https://qz.com/india/1370598/google-amazon-and-paytm-want-to-invest-in-indias-future-retail/")
				.parse();
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(json)));
		Assert.assertTrue(json.getFullArticleText() != null);*/
	}
	
	/*public static void main(String[] args) throws IOException {
		String t = new String(Files.readAllBytes(Paths.get("C:\\Users\\CipherCloud\\Desktop\\template.txt")));
		String v = new String(Files.readAllBytes(Paths.get("C:\\Users\\CipherCloud\\Desktop\\value.txt")));
		t = t.replaceAll(Pattern.quote("NEWSFULLTEXT"), Matcher.quoteReplacement(v.replaceAll(" +", " ")));
		System.out.println(t);
	}*/
}