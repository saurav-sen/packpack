package com.squill.og.crawler.content.handlers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.feed.web.model.JConcept;
import com.squill.feed.web.model.JRssFeed;
import com.squill.og.crawler.entity.extraction.DandelionEntityExtractor;
import com.squill.og.crawler.hooks.GeoLocation;
import com.squill.og.crawler.hooks.IGeoLocationResolver;

@Component("defaultGeoLocationResolver")
@Scope("prototype")
public class DefaultGeoLocationResolver implements IGeoLocationResolver {

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultGeoLocationResolver.class);
	
	@Override
	public GeoLocation[] resolveGeoLocations(String linkUrl, String domainUrl,
			JRssFeed feed) {
		String ogTitle = feed.getOgTitle();
		try {
			if (ogTitle != null && !ogTitle.trim().isEmpty()) {
				List<JConcept> concepts = new DandelionEntityExtractor()
						.extractConcepts(feed.getOgTitle());

				List<GeoLocation> geoLocationTags = new ArrayList<GeoLocation>();
				DbpediaGeoLocationReader dbpediaBasedLocationResolver = new DbpediaGeoLocationReader();
				if (concepts != null && !concepts.isEmpty()) {
					feed.setConcepts(concepts);
					geoLocationTags = dbpediaBasedLocationResolver
							.resolveGeoLocationTags(concepts);
					if (geoLocationTags == null || geoLocationTags.isEmpty()) {
						String urlId = domainUrl;
						if(urlId == null || urlId.trim().isEmpty()) {
							urlId = linkUrl;
						}
						geoLocationTags.addAll(resolveBasedUponServerUrl(urlId, dbpediaBasedLocationResolver));
					}
				} else {
					String urlId = domainUrl;
					if(urlId == null || urlId.trim().isEmpty()) {
						urlId = linkUrl;
					}
					geoLocationTags.addAll(resolveBasedUponServerUrl(urlId, dbpediaBasedLocationResolver));
				}
				
				dbpediaBasedLocationResolver.dispose();
				return geoLocationTags != null ? geoLocationTags
						.toArray(new GeoLocation[geoLocationTags.size()])
						: new GeoLocation[0];
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return new GeoLocation[0];
	}
	
	private List<GeoLocation> resolveBasedUponServerUrl(String serverUrl, DbpediaGeoLocationReader dbpediaBasedLocationResolver) {
		List<GeoLocation> geoLocationTags = new LinkedList<GeoLocation>();
		String urlId = serverUrl;
		String[] places = GeoLocationDataHolder.INSTANCE
				.getTargetDefaultPlacesByDomainUrl(urlId);
		if (places != null) {
			for (String place : places) {
				List<GeoLocation> list = dbpediaBasedLocationResolver
						.resolveGeoLocationsForPlaceByName(place);
				if(list != null) {
					geoLocationTags.addAll(list);
				}
			}
		}
		return geoLocationTags;
	}
}
