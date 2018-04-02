package com.squill.og.crawler.content.handlers;

import com.squill.og.crawler.hooks.GeoLocation;
import com.squill.og.crawler.hooks.IGeoLocationResolver;
import com.squill.og.crawler.model.web.JRssFeed;

public class DefaultGeoLocationResolver implements IGeoLocationResolver {

	@Override
	public GeoLocation[] resolveGeoLocation(String linkUrl, String domainUrl,
			JRssFeed feed) {
		// TODO Auto-generated method stub
		return new GeoLocation[0];
	}

}
