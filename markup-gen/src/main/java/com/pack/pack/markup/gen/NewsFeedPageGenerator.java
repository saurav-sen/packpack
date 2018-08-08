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
	
	@Override
	public <T> void generate(String entityId, IMarkup markup) throws Exception {
		throw new UnsupportedOperationException(
				"Generation based upon ID alone us not supported");
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
		dataModel.put("fullText", feed.getFullArticleText());
		URL url = new URL(feed.getOgUrl());
		String logo = LogoMap.get(url.getHost());
		dataModel.put("logo", logo);
		String jsBaseURL = SystemPropertyUtil.getExternalSharedLinkBaseUrl();
		if(jsBaseURL == null) {
			jsBaseURL = SystemPropertyUtil.getJSBaseURL();
		}
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
			Template template = cfg.getTemplate("squill_page.ftl");

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
