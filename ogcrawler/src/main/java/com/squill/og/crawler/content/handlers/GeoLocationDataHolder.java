package com.squill.og.crawler.content.handlers;

import java.util.HashMap;
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

	private Map<String, GeoLocation> placeNameVsGeoLocation = new HashMap<String, GeoLocation>();

	public static final GeoLocationDataHolder INSTANCE = new GeoLocationDataHolder();

	private GeoLocationDataHolder() {
	}

	public GeoLocation getGeoLocationByPlaceName(String placeName) {
		return placeNameVsGeoLocation.get(placeName);
	}

	public void addInfoOfGeoLocation(String placeName, GeoLocation geoLocation) {
		placeNameVsGeoLocation.put(placeName, geoLocation);
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
		return result;
	}
}
