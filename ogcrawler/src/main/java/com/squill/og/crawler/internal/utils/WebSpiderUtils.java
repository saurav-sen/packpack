package com.squill.og.crawler.internal.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.squill.og.crawler.ILink;
import com.squill.og.crawler.IRobotScope;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.internal.HtmlPage;
import com.squill.og.crawler.internal.PageLinkExtractor;

import crawlercommons.fetcher.http.BaseHttpFetcher;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.RobotUtils;
import crawlercommons.robots.SimpleRobotRulesParser;
import crawlercommons.sitemaps.AbstractSiteMap;
import crawlercommons.sitemaps.SiteMap;
import crawlercommons.sitemaps.SiteMapIndex;
import crawlercommons.sitemaps.SiteMapParser;
import crawlercommons.sitemaps.SiteMapURL;

/**
 * 
 * @author Saurav
 *
 */
public class WebSpiderUtils {

	public static List<? extends ILink> parseCrawlableURLs(IWebSite webSite) throws Exception {
		String domainUrl = webSite.getDomainUrl();
		if(webSite.shouldCheckRobotRules()) {
			BaseHttpFetcher fetcher = RobotUtils.createFetcher(CoreConstants.SQUILL_ROBOT, 1);
			SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
			URL robotsUrl = new URL(domainUrl + "/" + "robots.txt");
			BaseRobotRules rules = RobotUtils.getRobotRules(fetcher, parser, robotsUrl);
			List<String> sitemaps = rules.getSitemaps();
			if(!sitemaps.isEmpty()) {
				//System.out.println();
				return parseSiteMap(sitemaps, new URL(domainUrl + "/"), webSite);
			}
			if(!rules.isAllowAll()) {
				return Collections.emptyList();
			}
		}
		HttpRequestExecutor executor = new HttpRequestExecutor();
		String html = executor.GET(domainUrl, "");
		HtmlPage page = ResponseUtil.getParseableHtml(html, domainUrl);
		PageLinkExtractor extractor = new PageLinkExtractor();
		return extractor.extractAllPageLinks(page, null);
		/*DefaultHttpClient client = new DefaultHttpClient();
		HttpGet getRobotRules = new HttpGet("http://www.medindia.net/robots.txt");
		getRobotRules.setHeader("User-agent", "Mozilla");
		HttpResponse response = client.execute(getRobotRules);
		if(response.getStatusLine().getStatusCode() == 200) {
			String content = EntityUtils.toString(response.getEntity());
			System.out.println(content);
		}
		return Collections.emptyList();*/
	}
	
	private static List<ILink> parseSiteMap(List<String> sitemaps, URL domainUrl, IWebSite webSite) throws Exception {
		String siteMap = sitemaps.get(0);
		URL url = new URL(siteMap);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet GET = new HttpGet(siteMap);
		HttpContext HTTP_CONTEXT = new BasicHttpContext();
		HTTP_CONTEXT.setAttribute(CoreProtocolPNames.USER_AGENT, 
				CoreConstants.SQUILL_ROBOT_USER_AGENT_STRING);
		HttpResponse response = client.execute(GET, HTTP_CONTEXT);
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
						IRobotScope robotScope = webSite.getRobotScope();
						//if(asm2.getUrl().getPath().contains("hospital-directory")) {
						if(robotScope != null && robotScope.isScoped(asm2.getUrl().getPath())) {
							URL url2 = asm2.getUrl();
							HttpClient client2 = new DecompressingHttpClient(new DefaultHttpClient());
							GET = new HttpGet(url2.toURI());
							response = client2.execute(GET);
							if(response.getStatusLine().getStatusCode() == 200) {
								InputStream inStream = null;
								if(response.getEntity().getContentType().getValue().startsWith("text/xml")) {
									inStream = response.getEntity().getContent();
								} else {
									GZIPInputStream gzipStream = new GZIPInputStream(response.getEntity().getContent());
									inStream = gzipStream;
								}
								BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
								StringBuilder gzipContent = new StringBuilder();
								String c = null;
								while((c = reader.readLine()) != null) {
									//System.out.println(c);
									gzipContent.append(c);
								}
								reader.close();
								//System.out.println(asm2.getUrl());
								return parseSiteMapText(gzipContent.toString(), domainUrl);
							}
						}						
					}
				}
			}
		}
		return Collections.emptyList();
	}
	
	private static List<ILink> parseSiteMapText(String content, URL domainUrl) throws Exception {
		List<ILink> result = new ArrayList<ILink>();
		SiteMapParser parser = new SiteMapParser();
		String contentType = "text/xml";
		AbstractSiteMap siteMap = parser.parseSiteMap(contentType, content.getBytes(), domainUrl);
		if(!siteMap.isIndex()) {
			SiteMap sm = (SiteMap)siteMap;
			Collection<SiteMapURL> urls = sm.getSiteMapUrls();
			if(urls != null && !urls.isEmpty()) {
				Iterator<SiteMapURL> itr = urls.iterator();
				while(itr.hasNext()) {
					SiteMapURL url = itr.next();
					String urlPath = url.getUrl().toString().trim();
					ILink link = new HyperLink(urlPath);
					result.add(link);
				}
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(CoreConstants.SQUILL_ROBOT.getUserAgentString());
		/*List<String> urls = WebSpiderUtils.parseCrawlableURLs("http://www.medindia.net");
		if(!urls.isEmpty()) {
			for(String url : urls) {
				System.out.println(url);
			}
		}*/
	}
	
	private static class HyperLink implements ILink {
		
		private String url;
		private List<String> tags;
		
		public HyperLink(String url) {
			this.url = url;
		}

		@Override
		public String getUrl() {
			return url;
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