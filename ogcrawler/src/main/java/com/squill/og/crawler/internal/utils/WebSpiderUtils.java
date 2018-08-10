package com.squill.og.crawler.internal.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.crawlercommons.fetcher.http.BaseHttpFetcher;
import com.squill.crawlercommons.robots.BaseRobotRules;
import com.squill.crawlercommons.robots.RobotUtils;
import com.squill.crawlercommons.robots.SimpleRobotRulesParser;
import com.squill.crawlercommons.sitemaps.AbstractSiteMap;
import com.squill.crawlercommons.sitemaps.SiteMap;
import com.squill.crawlercommons.sitemaps.SiteMapIndex;
import com.squill.crawlercommons.sitemaps.SiteMapParser;
import com.squill.crawlercommons.sitemaps.SiteMapURL;
import com.squill.og.crawler.ILink;
import com.squill.og.crawler.IRobotScope;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.internal.HtmlPage;
import com.squill.og.crawler.internal.PageLinkExtractor;

/**
 * 
 * @author Saurav
 *
 */
public class WebSpiderUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(WebSpiderUtils.class);

	public static List<? extends ILink> parseCrawlableURLs(IWebSite webSite) throws Exception {
		String domainUrl = webSite.getDomainUrl();
		webSite.enablePageLinkExtractor();
		if(webSite.shouldCheckRobotRules()) {
			BaseHttpFetcher fetcher = RobotUtils.createFetcher(CoreConstants.SQUILL_ROBOT, 1);
			SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
			String url = domainUrl;
			if(!url.endsWith("/")) {
				url = url + "/";
			}
			url = url + "robots.txt";
			URL robotsUrl = new URL(url);
			BaseRobotRules rules = RobotUtils.getRobotRules(fetcher, parser, robotsUrl);
			List<String> sitemaps = rules.getSitemaps();
			IRobotScope robotScope = webSite.getRobotScope();
			if(robotScope != null) {
				robotScope.setRobotRules(rules);
			}
			if(!sitemaps.isEmpty()) {
				webSite.disablePageLinkExtractor();
				return parseSiteMaps(sitemaps, new URL(domainUrl + "/"), webSite);
			}
			if(rules.isAllowNone()) {
				return Collections.emptyList();
			}
			else {
				List<ILink> links = new ArrayList<ILink>();
				links.add(new HyperLink(domainUrl, webSite, -1));
				return links;
			}
		} else {
			HttpRequestExecutor executor = new HttpRequestExecutor();
			String html = executor.GET0(domainUrl, "");
			HtmlPage page = ResponseUtil.getParseableHtml(html, domainUrl);
			PageLinkExtractor extractor = new PageLinkExtractor();
			return extractor.extractAllPageLinks(page, webSite);
		}
	}
	
	private static List<ILink> parseSiteMaps(List<String> sitemaps,
			URL domainUrl, IWebSite webSite) throws Exception {
		List<ILink> result = new LinkedList<ILink>();
		Iterator<String> itr = sitemaps.iterator();
		while (itr.hasNext()) {
			String siteMap = itr.next();
			result.addAll(parseSiteMap(siteMap, domainUrl, webSite));
		}
		return result;
	}
	
	private static List<ILink> parseSiteMap(String siteMap, URL domainUrl, IWebSite webSite) throws Exception {
		List<ILink> result = new LinkedList<ILink>();
		IRobotScope robotScope = webSite.getRobotScope();
		if(!robotScope.isScopedSiteMapUrl(siteMap))
			return result;
		URL url = new URL(siteMap);
		HttpGet GET = new HttpGet(siteMap);
		HttpContext HTTP_CONTEXT = new BasicHttpContext();
		HTTP_CONTEXT.setAttribute(CoreProtocolPNames.USER_AGENT, 
				CoreConstants.SQUILL_ROBOT_USER_AGENT_STRING);
		HttpResponse response = new HttpRequestExecutor().GET(GET, HTTP_CONTEXT);
		if(response.getStatusLine().getStatusCode() == 200) {
			String content = EntityUtils.toString(response.getEntity());
			String contentType = "text/xml";
			SiteMapParser siteMapParser = new SiteMapParser();
			AbstractSiteMap asm = siteMapParser.parseSiteMap(contentType, content.getBytes(), url);
			if(asm.isIndex()) {
				SiteMapIndex smi = (SiteMapIndex)asm;
				Collection<AbstractSiteMap> siteMaps = smi.getSitemaps();
				if(siteMaps != null && !siteMaps.isEmpty()) {
					Iterator<AbstractSiteMap> itr = siteMaps.iterator();
					while(itr.hasNext()) {
						AbstractSiteMap asm2 = itr.next();
						LOG.debug(asm2.getUrl().toString());
						if(robotScope != null && robotScope.isScopedSiteMapUrl(asm2.getUrl().toString())) {
							URL url2 = asm2.getUrl();
							GET = new HttpGet(url2.toURI());
							response = new HttpRequestExecutor().GET(GET, true);
							if(response.getStatusLine().getStatusCode() == 200) {
								InputStream inStream = null;
								String contentTypeValue = response.getEntity().getContentType().getValue();
								if (contentTypeValue.contains("text/xml")
										|| contentTypeValue
												.contains("application/xml")
										|| contentTypeValue
												.contains("application/x-xml")
										|| contentTypeValue
												.contains("application/atom+xml")
										|| contentTypeValue
												.contains("application/rss+xml")) {
									inStream = response.getEntity().getContent();
								} else {
									inStream = new GZIPInputStream(response.getEntity().getContent());
								}
								BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
								StringBuilder gzipContent = new StringBuilder();
								String c = null;
								while((c = reader.readLine()) != null) {
									gzipContent.append(c);
								}
								reader.close();
								result.addAll(parseSiteMapContent(gzipContent.toString(), contentTypeValue, domainUrl, webSite));
							}
						}						
					}
				}
			} else if(asm.isProcessed()) {
				SiteMap asm2 = (SiteMap)asm;
				Iterator<SiteMapURL> itr = asm2.getSiteMapUrls().iterator();
				while(itr.hasNext()) {
					SiteMapURL siteMapURL = itr.next();
					String urlPath = siteMapURL.getUrl().toString().trim();
					if (robotScope.isScoped(urlPath)
							&& robotScope.isScopedSiteMap(siteMapURL)) {
						long lastModified = 0;
						Date date = siteMapURL.getLastModified();
						if(date != null) {
							lastModified = date.getTime();
						}
						ILink link = new HyperLink(urlPath, webSite, lastModified);
						result.add(link);
					}
				}
			}
		}
		return result;
	}
	
	private static List<ILink> parseSiteMapContent(String content, String contentType, URL domainUrl, IWebSite root) throws Exception {
		List<ILink> result = new LinkedList<ILink>();
		SiteMapParser parser = new SiteMapParser(false);
		AbstractSiteMap siteMap = parser.parseSiteMap(contentType, content.getBytes(), domainUrl);
		if(!siteMap.isIndex()) {
			SiteMap sm = (SiteMap)siteMap;
			Collection<SiteMapURL> urls = sm.getSiteMapUrls();
			if(urls != null && !urls.isEmpty()) {
				Iterator<SiteMapURL> itr = urls.iterator();
				while(itr.hasNext()) {
					SiteMapURL url = itr.next();
					String urlPath = url.getUrl().toString().trim();
					long lastModified = 0;
					Date date = url.getLastModified();
					if(date != null) {
						lastModified = date.getTime();
					}
					ILink link = new HyperLink(urlPath, root, lastModified);
					result.add(link);
				}
			}
		}
		return result;
	}
	
	private static class HyperLink implements ILink {
		
		private String url;
		private List<String> tags;
		
		private IWebSite root;
		
		private long lastModified;
		
		public HyperLink(String url, IWebSite root, long lastModified) {
			this.url = HtmlUtil.cleanIllegalCharacters4mUrl(url);
			this.root = root;
			this.lastModified = lastModified;
		}
		
		@Override
		public long getLastModified() {
			return lastModified;
		}
		
		@Override
		public String getUrl() {
			return url;
		}
		
		@Override
		public IWebSite getRoot() {
			return root;
		}

		@Override
		public List<String> getTags() {
			if(tags == null) {
				tags = new ArrayList<String>(3);
			}
			return tags;
		}
	}
}