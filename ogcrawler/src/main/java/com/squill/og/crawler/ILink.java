package com.squill.og.crawler;

import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public interface ILink {
	
	public IWebSite getRoot();

	public String getUrl();
	
	public List<String> getTags();
	
	public default long getLastModified() {
		return 0;
	}
}