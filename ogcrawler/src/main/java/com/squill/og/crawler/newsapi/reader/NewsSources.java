package com.squill.og.crawler.newsapi.reader;

import java.util.LinkedList;
import java.util.List;

public class NewsSources {
	
	private String providerName;
	
	private String providerDescription;
	
	private String providerUrl;

	private List<NewsSource> sources;

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getProviderDescription() {
		return providerDescription;
	}

	public void setProviderDescription(String providerDescription) {
		this.providerDescription = providerDescription;
	}

	public String getProviderUrl() {
		return providerUrl;
	}

	public void setProviderUrl(String providerUrl) {
		this.providerUrl = providerUrl;
	}
	
	public List<NewsSource> getSources() {
		if(sources == null) {
			sources = new LinkedList<NewsSource>();
		}
		return sources;
	}
}
