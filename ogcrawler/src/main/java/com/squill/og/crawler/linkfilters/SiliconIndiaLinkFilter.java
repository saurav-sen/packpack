package com.squill.og.crawler.linkfilters;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Saurav
 *
 */
@Component("siliconIndiaLinkFilter")
@Scope("prototype")
public class SiliconIndiaLinkFilter extends DailyFixedSizeLinkFilter {
	
	public SiliconIndiaLinkFilter() {
		super(2);
	}

	@Override
	public boolean isScopedSitemapUrl(String sitemapUrl) {
		return true;
	}
	
	@Override
	protected String todayKeySuffix() {
		return "silicon_india";
	}
}
