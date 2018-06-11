package com.squill.og.crawler.hooks;

import com.squill.feed.web.model.JRssFeed;

public interface IGeoLocationResolver {

	public boolean canResolve(String linkUrl, String domainUrl, JRssFeed feed);

	public GeoLocation[] resolveGeoLocations(String linkUrl, String domainUrl,
			JRssFeed feed);
}
