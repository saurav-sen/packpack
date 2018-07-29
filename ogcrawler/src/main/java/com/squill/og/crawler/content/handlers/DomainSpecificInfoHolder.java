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
				"11000000", "politics", new GeoLocation[] { new GeoLocation(23.2599, 77.4126) })); // Bhopal (India)
		domainVsDefaultTgtPlace.put("thehindu.com", new DomainInfo(
				"11000000", "politics", new GeoLocation[] { new GeoLocation(23.2599, 77.4126) })); // Bhopal (India)
		domainVsDefaultTgtPlace.put("timesofindia.com", new DomainInfo(
				"11000000", "politics", new GeoLocation[] { new GeoLocation(23.2599, 77.4126) })); // Bhopal (India)
		domainVsDefaultTgtPlace.put("timesofindia.indiatimes.com",
				new DomainInfo("11000000", "politics", new GeoLocation[] { new GeoLocation(23.2599, 77.4126) })); // Bhopal (India)
		domainVsDefaultTgtPlace.put("espncricinfo.com", new DomainInfo(
				"15000000", "sport", new GeoLocation[] { new GeoLocation(23.2599, 77.4126), new GeoLocation(51.508530, -0.076132),
						new GeoLocation(33.738045, 73.084488), new GeoLocation(6.927079, 79.861244), 
						new GeoLocation(-33.865143, 151.209900), new GeoLocation(-33.640480, 19.009657) })); 
		// Bhopal, London, Islamabad, Colombo, Sydney (Australia), Wellington (South Africa)
		domainVsDefaultTgtPlace.put("talksport.com", new DomainInfo(
				"15000000", "sport", new GeoLocation[] {new GeoLocation(-15.790669, -47.892967), new GeoLocation(44.402393, 86.154785), 
						new GeoLocation(54.687157, 25.279652), new GeoLocation(40.052059, -86.470642)})); 
		// Brasil (Brazil), Xinjiang (Center Of Asia), Vilnius, Lithuania (Center Of Europe), Lebanon, Kansas (Center Of America)
		domainVsDefaultTgtPlace.put("aljazeera.com", new DomainInfo(
				"11000000", "politics", new GeoLocation[] { new GeoLocation(24.952171, 51.587822) })); // Qatar
		domainVsDefaultTgtPlace.put("newscientist.com", new DomainInfo(
				"13000000", "science and technology", new GeoLocation[] {new GeoLocation(44.402393, 86.154785), 
						new GeoLocation(54.687157, 25.279652), new GeoLocation(40.052059, -86.470642)})); 
		// Xinjiang (Center Of Asia), Vilnius, Lithuania (Center Of Europe), Lebanon, Kansas (Center Of America)
		domainVsDefaultTgtPlace.put("news.nationalgeographic.com", new DomainInfo(
				"13000000", "science and technology", new GeoLocation[] {new GeoLocation(44.402393, 86.154785), 
						new GeoLocation(54.687157, 25.279652), new GeoLocation(40.052059, -86.470642)}));
		// Xinjiang (Center Of Asia), Vilnius, Lithuania (Center Of Europe), Lebanon, Kansas (Center Of America)
		
		domainVsDefaultTgtPlace.put("time.com", new DomainInfo(
				"13000000", "science and technology", new GeoLocation[] {new GeoLocation(44.402393, 86.154785), 
						new GeoLocation(54.687157, 25.279652), new GeoLocation(40.052059, -86.470642), 
						new GeoLocation(40.730610, -73.935242), new GeoLocation(37.733795, -122.446747)}));
		// Xinjiang (Center Of Asia), Vilnius, Lithuania (Center Of Europe), Lebanon, Kansas (Center Of America), New York (USA), San Francisco (USA)
		
		domainVsDefaultTgtPlace.put("nytimes.com", new DomainInfo(
				"13000000", "science and technology", new GeoLocation[] {new GeoLocation(44.402393, 86.154785), 
						new GeoLocation(54.687157, 25.279652), new GeoLocation(40.052059, -86.470642), 
						new GeoLocation(40.730610, -73.935242), new GeoLocation(37.733795, -122.446747)}));
		// Xinjiang (Center Of Asia), Vilnius, Lithuania (Center Of Europe), Lebanon, Kansas (Center Of America), New York (USA), San Francisco (USA)
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
	
	public GeoLocation[] getTargetDefaultPlacesByDomainUrl(String domainUrl) {
		DomainInfo domainInfo = getDomainInfoByDomainUrl(domainUrl);
		if (domainInfo == null)
			return new GeoLocation[0];
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
		
		private GeoLocation[] geoLocations;
		
		DomainInfo(String defaultTaxonomyId, String defaultTaxonomyLabel, GeoLocation[] geoLocations) {
			this.defaultTaxonomyId = defaultTaxonomyId;
			this.defaultTaxonomyLabel = defaultTaxonomyLabel;
			this.geoLocations = geoLocations;
		}

		String getDefaultTaxonomyId() {
			return defaultTaxonomyId;
		}

		String getDefaultTaxonomyLabel() {
			return defaultTaxonomyLabel;
		}
		
		GeoLocation[] getTargetPlaces() {
			return geoLocations;
		}
	}
}
