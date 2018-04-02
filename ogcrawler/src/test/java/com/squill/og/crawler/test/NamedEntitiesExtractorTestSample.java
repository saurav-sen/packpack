package com.squill.og.crawler.test;

import com.squill.og.crawler.app.Startup;
import com.squill.og.crawler.internal.utils.JSONUtil;
import com.squill.og.crawler.named.entities.NamedEntities;
import com.squill.og.crawler.named.entities.NamedEntitiesExtractor;

public class NamedEntitiesExtractorTestSample {

	public static void main(String[] args) throws Exception {
		System.setProperty(Startup.WEB_CRAWLERS_CONFIG_DIR,
				"D:\\Saurav\\packpack\\ogcrawler\\src\\conf");
//		NamedEntities extractNamedEntities = NamedEntitiesExtractor
//				.init()
//				.extractNamedEntitiesFromUrl(
//						"https://timesofindia.indiatimes.com/city/kolkata/youths-defy-state-directive-carry-swords-at-hanuman-jayanti-rally/articleshow/63562823.cms");//"http://www.thehindu.com/news/national/mamata-kcr-meet-kicks-off-federal-front-process/article23295989.ece");
		NamedEntities extractNamedEntities = NamedEntitiesExtractor
				.init()
				.extractNamedEntitiesFromText(
						"IIT-Kharagpur builds bio-toilet, wins PM Narendra Modi's Swachh Bharat award");
		String json = JSONUtil.serialize(extractNamedEntities);
		System.out.println();
		System.out.println("========================================== RESULT ==========================================");
		System.out.println(json);
		System.out.println("============================================================================================");
	}
}
