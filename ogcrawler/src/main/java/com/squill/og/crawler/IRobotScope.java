package com.squill.og.crawler;

import java.util.List;

import crawlercommons.robots.BaseRobotRules;

/**
 * 
 * @author Saurav
 *
 */
public interface IRobotScope {
	
	public boolean isScoped(String link);
	
	public List<? extends ILink> getAnyLeftOverLinks();
	
	public int getDefaultCrawlDelay();
	
	public void setRobotRules(BaseRobotRules robotRules);
}