package com.squill.og.crawler.content.handlers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.entity.extraction.Concept;
import com.squill.og.crawler.entity.extraction.DandelionEntityExtractor;
import com.squill.og.crawler.hooks.GeoLocation;
import com.squill.og.crawler.hooks.IGeoLocationResolver;
import com.squill.og.crawler.model.web.JRssFeed;

public class DefaultGeoLocationResolver implements IGeoLocationResolver {

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultGeoLocationResolver.class);

	@Override
	public GeoLocation[] resolveGeoLocation(String linkUrl, String domainUrl,
			JRssFeed feed) {
		String ogTitle = feed.getOgTitle();
		try {
			if (ogTitle != null && !ogTitle.trim().isEmpty()) {
				List<Concept> concepts = new DandelionEntityExtractor()
						.extractConcepts(feed.getOgTitle());

				DbpediaGeoLocationReader dbpediaBasedLocationResolver = new DbpediaGeoLocationReader();
				if (concepts != null && !concepts.isEmpty()) {
					feed.setConcepts(concepts);
					List<GeoLocation> geoLocationTags = dbpediaBasedLocationResolver
							.resolveGeoLocationTags(concepts);
					if (geoLocationTags == null || geoLocationTags.isEmpty()) {
						String[] places = GeoLocationDataHolder.INSTANCE
								.getTargetDefaultPlacesByDomainUrl(domainUrl);
						if (places != null) {
							for (String place : places) {
								GeoLocation geoLocationTag = GeoLocationDataHolder.INSTANCE
										.getGeoLocationByPlaceName(place);
								if (geoLocationTag == null) {
									geoLocationTag = dbpediaBasedLocationResolver
											.resolveGeoLocationsForPlaceByName(place);
									GeoLocationDataHolder.INSTANCE
											.addInfoOfGeoLocation(place,
													geoLocationTag);
								}
								geoLocationTags.add(geoLocationTag);
							}
						}
					}
					return geoLocationTags != null ? geoLocationTags
							.toArray(new GeoLocation[geoLocationTags.size()])
							: new GeoLocation[0];
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return new GeoLocation[0];
	}
}