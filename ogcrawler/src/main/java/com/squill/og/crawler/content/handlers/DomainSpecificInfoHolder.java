package com.squill.og.crawler.content.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.squill.feed.web.model.JTaxonomy;
import com.squill.og.crawler.hooks.GeoLocation;

public class DomainSpecificInfoHolder {

	private static final Map<String, DomainInfo> domainVsDefaultTgtPlace = new HashMap<String, DomainInfo>();
	static {
		domainVsDefaultTgtPlace.put("hindu.com", new DomainInfo(
				"11000000", "politics", new String[] { "Bhopal" }));
		domainVsDefaultTgtPlace.put("timesofindia.com", new DomainInfo(
				"11000000", "politics", new String[] { "Bhopal" }));
		domainVsDefaultTgtPlace.put("timesofindia.indiatimes.com",
				new DomainInfo("11000000", "politics", new String[] { "Bhopal" }));
		domainVsDefaultTgtPlace.put("espncricinfo.com", new DomainInfo(
				"15000000", "sport", new String[] { "Bhopal", "London",
						"Islamabad", "Colombo" }));
		domainVsDefaultTgtPlace.put("talksport.com", new DomainInfo(
				"15000000", "sport", new String[0]));
		domainVsDefaultTgtPlace.put("aljazeera.com", new DomainInfo(
				"11000000", "politics", new String[] { "Qatar" }));
		domainVsDefaultTgtPlace.put("newscientist.com", new DomainInfo(
				"13000000", "science and technology", new String[0]));
		domainVsDefaultTgtPlace.put("news.nationalgeographic.com", new DomainInfo(
				"13000000", "science and technology", new String[0]));
	}

	private Map<String, List<GeoLocation>> entityNameVsGeoLocation = new HashMap<String, List<GeoLocation>>();

	public static final DomainSpecificInfoHolder INSTANCE = new DomainSpecificInfoHolder();

	private DomainSpecificInfoHolder() {
	}

	public List<GeoLocation> getGeoLocationByEntityName(String entityName) {
		return entityNameVsGeoLocation.get(entityName);
	}

	public void addInfoOfGeoLocation(String entityName, GeoLocation geoLocation) {
		List<GeoLocation> geoLocations = getGeoLocationByEntityName(entityName);
		if(geoLocations == null) {
			geoLocations = new ArrayList<GeoLocation>();
			entityNameVsGeoLocation.put(entityName, geoLocations);
		}
		geoLocations.add(geoLocation);
	}
	
	public void addInfoOfGeoLocations(String entityName, List<GeoLocation> geoLocations) {
		entityNameVsGeoLocation.put(entityName, geoLocations);
	}
	
	private DomainInfo getDomainInfoByDomainUrl(String domainUrl) {
		return domainVsDefaultTgtPlace.get(filterDomainUrl(domainUrl));
	}
	
	public String[] getTargetDefaultPlacesByDomainUrl(String domainUrl) {
		DomainInfo domainInfo = getDomainInfoByDomainUrl(domainUrl);
		if (domainInfo == null)
			return new String[0];
		return domainInfo.getTargetPlaces();
	}
	
	public JTaxonomy getDefaultTaxonomyByDomainUrl(String domainUrl) {
		DomainInfo domainInfo = getDomainInfoByDomainUrl(domainUrl);
		if (domainInfo == null)
			return null;
		JTaxonomy jTaxonomy = new JTaxonomy();
		jTaxonomy.setId(domainInfo.getDefaultTaxonomyId());
		jTaxonomy.setName(domainInfo.getDefaultTaxonomyLabel());
		return jTaxonomy;
	}

	private String filterDomainUrl(String domainUrl) {
		String result = domainUrl;
		if (result.indexOf("http://") >= 0) {
			result = result.substring("http://".length());
		} else if (result.indexOf("https://") >= 0) {
			result = result.substring("https://".length());
		}

		if (result.indexOf("www.") >= 0) {
			result = result.substring("www.".length());
		}
		int index = result.indexOf("/");
		if(index >= 0) {
			result = result.substring(0, index);
		}
		return result;
	}
	
	private static class DomainInfo {
		
		private String defaultTaxonomyId;
		
		private String defaultTaxonomyLabel;
		
		private String[] targetPlaces;
		
		DomainInfo(String defaultTaxonomyId, String defaultTaxonomyLabel, String[] targetPlaces) {
			this.defaultTaxonomyId = defaultTaxonomyId;
			this.defaultTaxonomyLabel = defaultTaxonomyLabel;
			this.targetPlaces = targetPlaces;
		}

		String getDefaultTaxonomyId() {
			return defaultTaxonomyId;
		}

		String getDefaultTaxonomyLabel() {
			return defaultTaxonomyLabel;
		}
		
		String[] getTargetPlaces() {
			return targetPlaces;
		}
	}
}
