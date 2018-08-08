package com.pack.pack.markup.gen;

import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.pack.pack.model.web.JSharedFeed;
import com.pack.pack.util.SystemPropertyUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 
 * @author Saurav
 *
 */
public class SharedExternalLinkPageGenerator implements IMarkupGenerator {
	
	@Override
	public <T> void generate(String entityId, IMarkup markup) throws Exception {
		throw new UnsupportedOperationException(
				"Generation based upon ID alone us not supported");
	}
	
	@Override
	public <T> void generate(T object, IMarkup markup) throws Exception {
		JSharedFeed sharedFeed = (JSharedFeed) object;
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("ogTitle", sharedFeed.getTitle());
		dataModel.put("ogDescription", sharedFeed.getDescription());
		dataModel.put("ogImage", sharedFeed.getImageLink());
		dataModel.put("ogUrl", sharedFeed.getActualUrl());
		dataModel.put("summaryText", sharedFeed.getSummaryText());
		URL url = new URL(sharedFeed.getActualUrl());
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
		generateSquillPage(dataModel, markup);
	}

	/*@Override
	public <T> void generate(String entityId, IMarkup markup, JSharedFeed feed) throws Exception {
		JSharedFeed sharedFeed = UrlShortener.readShortUrlInfo(entityId);
		if(sharedFeed == null) {
			// TODO -- generate 404 error page here (With nice message stating that, it has expired).
			return;
		}
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("ogTitle", sharedFeed.getTitle());
		dataModel.put("ogDescription", sharedFeed.getDescription());
		dataModel.put("ogImage", sharedFeed.getImageLink());
		dataModel.put("ogUrl", sharedFeed.getActualUrl());
		dataModel.put("summaryText", sharedFeed.getSummaryText());
		URL url = new URL(sharedFeed.getActualUrl());
		String logo = logoMap.get(url.getHost());
		dataModel.put("logo", logo);
		String jsBaseURL = SystemPropertyUtil.getJSBaseURL();
		if(jsBaseURL == null) {
			jsBaseURL = "http://www.squill.co.in";
		}
		if(jsBaseURL.endsWith("/")) {
			jsBaseURL = jsBaseURL.substring(0, jsBaseURL.length() - 1);
		}
		dataModel.put("jsBaseUrl", jsBaseURL);
		generateProxyPage(dataModel, markup);
	}*/
	
	private void generateSquillPage(Map<String, Object> dataModel, IMarkup markup)
			throws Exception {
		StringWriter writer = null;
		Configuration cfg = new Configuration();
		try {
			cfg.setClassForTemplateLoading(SharedExternalLinkPageGenerator.class,
					"/com/pack/pack/markup/external/page");
			Template template = cfg.getTemplate("squill_share_page.ftl");

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
