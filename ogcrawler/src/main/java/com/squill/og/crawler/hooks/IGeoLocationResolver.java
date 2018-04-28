package com.squill.og.crawler.hooks;

import com.squill.feed.web.model.JRssFeed;

public interface IGeoLocationResolver {

	public GeoLocation[] resolveGeoLocations(String linkUrl, String domainUrl,
			JRssFeed feed);
}
