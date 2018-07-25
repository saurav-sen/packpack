package com.squill.og.crawler.content.handlers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;
import com.squill.og.crawler.hooks.GeoLocation;
import com.squill.og.crawler.hooks.IGeoLocationResolver;

/**
 * 
 * @author Saurav
 *
 */
@Component("basicGeoLocationResolver")
@Scope("prototype")
public class BasicGeoLocationResolver implements IGeoLocationResolver {

	@Override
	public boolean canResolve(String linkUrl, String domainUrl, JRssFeed feed) {
		if (feed == null)
			return false;
		String feedType = feed.getFeedType();
		return JRssFeedType.NEWS.name().equals(feedType)
				|| JRssFeedType.NEWS_SPORTS.name().equals(feedType)
				|| JRssFeedType.NEWS_SCIENCE_TECHNOLOGY.name().equals(feedType)
				|| JRssFeedType.REFRESHMENT.name().equals(feedType);
	}

	@Override
	public GeoLocation[] resolveGeoLocations(String linkUrl, String domainUrl,
			JRssFeed feed) {
		List<GeoLocation> geoLocationTags = new ArrayList<GeoLocation>();
		DbpediaGeoLocationReader dbpediaBasedLocationResolver = new DbpediaGeoLocationReader();
		String urlId = domainUrl;
		if (urlId == null || urlId.trim().isEmpty()) {
			urlId = linkUrl;
		}
		geoLocationTags.addAll(resolveBasedUponServerUrl(urlId,
				dbpediaBasedLocationResolver));
		if (geoLocationTags != null && !geoLocationTags.isEmpty()) {
			return geoLocationTags.toArray(new GeoLocation[geoLocationTags
					.size()]);
		}
		return new GeoLocation[0];
	}

	private List<GeoLocation> resolveBasedUponServerUrl(String serverUrl,
			DbpediaGeoLocationReader dbpediaBasedLocationResolver) {
		List<GeoLocation> geoLocationTags = new LinkedList<GeoLocation>();
		String urlId = serverUrl;
		String[] places = DomainSpecificInfoHolder.INSTANCE
				.getTargetDefaultPlacesByDomainUrl(urlId);
		if (places != null) {
			for (String place : places) {
				List<GeoLocation> list = dbpediaBasedLocationResolver
						.resolveGeoLocationsForPlaceByName(place);
				if (list != null) {
					geoLocationTags.addAll(list);
				}
			}
		}
		return geoLocationTags;
	}
}
