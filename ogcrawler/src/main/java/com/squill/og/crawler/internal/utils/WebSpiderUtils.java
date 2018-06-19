package com.squill.og.crawler.internal.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import com.squill.og.crawler.ICrawlSchedule;
import com.squill.og.crawler.ILink;
import com.squill.og.crawler.IRobotScope;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.hooks.IArticleTextSummarizer;
import com.squill.og.crawler.hooks.IGeoLocationResolver;
import com.squill.og.crawler.hooks.IHtmlContentHandler;
import com.squill.og.crawler.hooks.ITaxonomyResolver;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
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
			URL robotsUrl = new URL(domainUrl + "/" + "robots.txt");
			BaseRobotRules rules = RobotUtils.getRobotRules(fetcher, parser, robotsUrl);
			List<String> sitemaps = rules.getSitemaps();
			IRobotScope robotScope = webSite.getRobotScope();
			if(robotScope != null) {
				robotScope.setRobotRules(rules);
			}
			if(!sitemaps.isEmpty()) {
				//System.out.println();
				webSite.disablePageLinkExtractor();
				return parseSiteMaps(sitemaps, new URL(domainUrl + "/"), webSite);
			}
			if(rules.isAllowNone()) {
				return Collections.emptyList();
			}
			else {
				List<ILink> links = new ArrayList<ILink>();
				links.add(new HyperLink(domainUrl, webSite));
				return links;
			}
		}
		HttpRequestExecutor executor = new HttpRequestExecutor();
		String html = executor.GET0(domainUrl, "");
		HtmlPage page = ResponseUtil.getParseableHtml(html, domainUrl);
		PageLinkExtractor extractor = new PageLinkExtractor();
		return extractor.extractAllPageLinks(page, webSite);
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
	
	public static void main(String[] args) throws MalformedURLException, Exception {
		System.out.println(CoreConstants.SQUILL_ROBOT.getUserAgentString());
		IWebSite w = new IWebSite() {
			
			@Override
			public boolean isUploadIndependently() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public String getUniqueId() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IWebLinkTrackerService getTrackerService() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ICrawlSchedule getSchedule() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ITaxonomyResolver getTaxonomyResolver() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IGeoLocationResolver getTargetLocationResolver() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IArticleTextSummarizer getArticleTextSummarizer() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean shouldCheckRobotRules() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isPageLinkExtractorEnabled() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public IRobotScope getRobotScope() {
				// TODO Auto-generated method stub
				return new IRobotScope() {
					
					@Override
					public void setRobotRules(BaseRobotRules robotRules) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public boolean isScopedSiteMapUrl(String sitemapUrl) {
						// TODO Auto-generated method stub
						return true;
					}
					
					@Override
					public boolean isScoped(String link) {
						// TODO Auto-generated method stub
						return true;
					}
					
					@Override
					public int getDefaultCrawlDelay() {
						// TODO Auto-generated method stub
						return 0;
					}
					
					@Override
					public List<? extends ILink> getAnyLeftOverLinks() {
						// TODO Auto-generated method stub
						return Collections.emptyList();
					}
				};
			}
			
			@Override
			public String getDomainUrl() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IHtmlContentHandler getContentHandler() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void enablePageLinkExtractor() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void disablePageLinkExtractor() {
				// TODO Auto-generated method stub
				
			}
		};
		parseSiteMap("https://www.siliconindia.com/sitemapnews.xml", new URL("https://www.siliconindia.com"), w);
	}
	
	private static List<ILink> parseSiteMap(String siteMap, URL domainUrl, IWebSite webSite) throws Exception {
		List<ILink> result = new LinkedList<ILink>();
		IRobotScope robotScope = webSite.getRobotScope();
		if(!robotScope.isScopedSiteMapUrl(siteMap))
			return result;
		URL url = new URL(siteMap);
		//DefaultHttpClient client = new DefaultHttpClient();
		HttpGet GET = new HttpGet(siteMap);
		HttpContext HTTP_CONTEXT = new BasicHttpContext();
		HTTP_CONTEXT.setAttribute(CoreProtocolPNames.USER_AGENT, 
				CoreConstants.SQUILL_ROBOT_USER_AGENT_STRING);
		//HttpResponse response = client.execute(GET, HTTP_CONTEXT);
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
						//if(asm2.getUrl().getPath().contains("hospital-directory")) {
						//if(robotScope != null && robotScope.isScoped(asm2.getUrl().getPath())) {
						LOG.debug(asm2.getUrl().toString());
						if(robotScope != null && robotScope.isScopedSiteMapUrl(asm2.getUrl().toString())) {
							URL url2 = asm2.getUrl();
							/*HttpClient client2 = new DecompressingHttpClient(new DefaultHttpClient());
							GET = new HttpGet(url2.toURI());
							response = client2.execute(GET);*/
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
									//System.out.println(c);
									gzipContent.append(c);
								}
								reader.close();
								//System.out.println(asm2.getUrl());
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
					if(robotScope.isScoped(urlPath)) {
						ILink link = new HyperLink(urlPath, webSite);
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
					ILink link = new HyperLink(urlPath, root);
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
		
		public HyperLink(String url, IWebSite root) {
			this.url = url;
			this.root = root;
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