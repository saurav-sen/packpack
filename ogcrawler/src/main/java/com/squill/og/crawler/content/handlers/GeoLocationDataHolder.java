package com.squill.og.crawler.content.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.squill.og.crawler.hooks.GeoLocation;

public class GeoLocationDataHolder {

	private static final Map<String, String[]> domainVsDefaultTgtPlace = new HashMap<String, String[]>();
	static {
		domainVsDefaultTgtPlace.put("hindu.com", new String[] { "Bhopal" });
		domainVsDefaultTgtPlace.put("timesofindia.com",
				new String[] { "Bhopal" });
		domainVsDefaultTgtPlace.put("timesofindia.indiatimes.com",
				new String[] { "Bhopal" });
		domainVsDefaultTgtPlace.put("espncricinfo.com", new String[] {
				"Bhopal", "London", "Islamabad", "Colombo" });
		domainVsDefaultTgtPlace.put("aljazeera.com", new String[] { "Qatar" });
	}

	private Map<String, List<GeoLocation>> entityNameVsGeoLocation = new HashMap<String, List<GeoLocation>>();

	public static final GeoLocationDataHolder INSTANCE = new GeoLocationDataHolder();

	private GeoLocationDataHolder() {
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

	public String[] getTargetDefaultPlacesByDomainUrl(String domainUrl) {
		return domainVsDefaultTgtPlace.get(filterDomainUrl(domainUrl));
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
}
