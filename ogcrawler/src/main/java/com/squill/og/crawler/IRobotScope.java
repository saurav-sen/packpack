package com.squill.og.crawler;

import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public interface IRobotScope {

	public boolean isScoped(String link);
	
	public List<? extends ILink> getAnyLeftOverLinks();
	
	public int getDefaultCrawlDelay();
}