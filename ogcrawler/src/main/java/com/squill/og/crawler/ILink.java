package com.squill.og.crawler;

import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public interface ILink {

	public String getUrl();
	
	public List<String> getTags();
}