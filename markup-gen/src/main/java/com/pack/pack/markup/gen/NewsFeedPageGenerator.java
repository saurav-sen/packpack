package com.pack.pack.markup.gen;

import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.pack.pack.util.SystemPropertyUtil;
import com.squill.feed.web.model.JRssFeed;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class NewsFeedPageGenerator implements IMarkupGenerator {

	private static final Map<String, String> logoMap = new HashMap<String, String>();

	static {
		logoMap.put("timesofindia.indiatimes.com", "times-of-india-logo.png");
		logoMap.put("www.nytimes.com", "New_York_Times_logo_variation.jpg");
		logoMap.put("www.time.com", "time_dot_com.png");
		logoMap.put("www.thehindu.com", "thehindu-icon.png");
		logoMap.put("talksport.com", "talksport_400x400.jpg");
		logoMap.put("www.espncricinfo.com", "espncricinfo-6301-630x400.jpg");
		logoMap.put("espncricinfo.com", "espncricinfo-6301-630x400.jpg");
		logoMap.put("www.newscientist.com", "newscientisthero34_1476186101.jpg");
		logoMap.put("newscientist.com", "newscientisthero34_1476186101.jpg");
		logoMap.put("news.nationalgeographic.com",
				"national-geographic.svg.png");
		logoMap.put("www.nationalgeographic.com", "national-geographic.svg.png");
		logoMap.put("www.aljazeera.com", "aljazeera.jpg");
		logoMap.put("aljazeera.com", "aljazeera.jpg");
	}

	@Override
	public <T> void generate(T object, IMarkup markup) throws Exception {
		JRssFeed feed = (JRssFeed) object;
		generateFullPage(feed, markup);
	}

	private void generateFullPage(JRssFeed feed, IMarkup markup)
			throws Exception {
		if (feed == null) {
			return;
		}
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("ogTitle", feed.getOgTitle());
		dataModel.put("ogDescription", feed.getOgDescription());
		dataModel.put("ogImage", feed.getOgImage());
		dataModel.put("ogUrl", feed.getOgUrl());
		dataModel.put("summaryText", feed.getFullArticleText());
		URL url = new URL(feed.getOgUrl());
		String logo = logoMap.get(url.getHost());
		dataModel.put("logo", logo);
		String jsBaseURL = SystemPropertyUtil.getJSBaseURL();
		if (jsBaseURL == null) {
			jsBaseURL = SystemPropertyUtil.DOMAIN_BASE_URL_DEFAULT;
		}
		if (jsBaseURL.endsWith("/")) {
			jsBaseURL = jsBaseURL.substring(0, jsBaseURL.length() - 1);
		}
		dataModel.put("jsBaseUrl", jsBaseURL);
		generateProxyPage(dataModel, markup);
	}

	private void generateProxyPage(Map<String, Object> dataModel, IMarkup markup)
			throws Exception {
		StringWriter writer = null;
		Configuration cfg = new Configuration();
		try {
			cfg.setClassForTemplateLoading(
					SharedExternalLinkPageGenerator.class,
					"/com/pack/pack/markup/external/page");
			Template template = cfg.getTemplate("proxy_page.ftl");

			writer = new StringWriter();
			template.process(dataModel, writer);
			writer.flush();
			markup.setContent(writer.toString());
			markup.setContentType("text/html");
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
