package com.squill.og.crawler.internal;

import com.squill.og.crawler.IRobotScope;

/**
 *
 * @author Saurav
 * @since 14-Mar-2015
 *
 */
public class PageLinkFilter {
	
	//private List<String> listOfCrawlableDomains = new ArrayList<String>(10);
	
	private IRobotScope contentFilter;
	
	public PageLinkFilter(IRobotScope contentFilter) {
		this.contentFilter = contentFilter;
		/*if(supportedDomainNames != null) {
			listOfCrawlableDomains = Arrays.asList(supportedDomainNames);
		}*/
	}

	public boolean isCrawlable(PageLink pageLink) {
		String str = pageLink.getLink();
		if(contentFilter == null)
			return true;
		return contentFilter.isScoped(str);
		/*if(!str.startsWith(CoreConstants.HTTP))
			return false;
		String str1 = str.replace(CoreConstants.HTTP, "");
		int index = str1.indexOf("/");
		str1 = str1.substring(0, index);
		if(listOfCrawlableDomains.contains(str1))
			return true;
		return false;*/
	}
}