package com.squill.og.crawler;

import java.util.List;

import com.squill.crawlercommons.robots.BaseRobotRules;
import com.squill.crawlercommons.sitemaps.SiteMapURL;

/**
 * 
 * @author Saurav
 *
 */
public interface IRobotScope {
	
	public boolean isScoped(String link);
	
	public boolean isScopedSiteMap(SiteMapURL siteMapURL);
	
	public boolean isScopedSiteMapUrl(String sitemapUrl);
	
	public List<? extends ILink> getAnyLeftOverLinks();
	
	public int getDefaultCrawlDelay();
	
	public void setRobotRules(BaseRobotRules robotRules);
}