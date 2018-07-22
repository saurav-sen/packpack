package com.pack.pack.markup.gen;

import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.pack.pack.model.web.JSharedFeed;
import com.pack.pack.services.redis.RedisCacheService;
import com.pack.pack.services.redis.UrlShortener;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.SystemPropertyUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 
 * @author Saurav
 *
 */
public class SharedExternalLinkPageGenerator implements IMarkupGenerator {
	
	private static final Map<String, String> logoMap = new HashMap<String, String>();
	
	static {
		logoMap.put("timesofindia.indiatimes.com", "times-of-india-logo.png");
		logoMap.put("www.nytimes.com", "New_York_Times_logo_variation.jpg");
		logoMap.put("www.thehindu.com", "thehindu-icon.png");
		logoMap.put("talksport.com", "talksport_400x400.jpg");
		logoMap.put("www.espncricinfo.com", "espncricinfo-6301-630x400.jpg");
		logoMap.put("espncricinfo.com", "espncricinfo-6301-630x400.jpg");
		logoMap.put("www.newscientist.com", "newscientisthero34_1476186101.jpg");
		logoMap.put("newscientist.com", "newscientisthero34_1476186101.jpg");
		logoMap.put("news.nationalgeographic.com", "national-geographic.svg.png");
		logoMap.put("www.nationalgeographic.com", "national-geographic.svg.png");
		logoMap.put("www.aljazeera.com", "aljazeera.jpg");
		logoMap.put("aljazeera.com", "aljazeera.jpg");
	}

	@Override
	public <T> void generate(String entityId, IMarkup markup) throws Exception {
		RedisCacheService cacheService = ServiceRegistry.INSTANCE
				.findService(RedisCacheService.class);
		JSharedFeed sharedFeed = UrlShortener.readShortUrlInfo(entityId);
		if(sharedFeed == null) {
			// TODO -- generate 404 error page here (With nice message stating that, it has expired).
			return;
		}
		/*String json = cacheService.getFromCache(entityId, String.class);
		JRssFeed feed = JSONUtil.deserialize(json, JRssFeed.class);*/
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
	}
	
	private void generateProxyPage(Map<String, Object> dataModel, IMarkup markup)
			throws Exception {
		StringWriter writer = null;
		Configuration cfg = new Configuration();
		try {
			cfg.setClassForTemplateLoading(SharedExternalLinkPageGenerator.class,
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
