package com.squill.og.crawler.hooks;

import com.squill.og.crawler.model.web.JRssFeed;

public interface IGeoLocationResolver {

	public GeoLocation[] resolveGeoLocation(String linkUrl, String domainUrl,
			JRssFeed feed);
}
