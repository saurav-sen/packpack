package com.pack.pack.rss.services;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.model.UserLocation;
import com.pack.pack.services.couchdb.UserLocationRepositoryService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.GeoLocationUtil;
import com.squill.feed.web.model.JGeoTag;
import com.squill.feed.web.model.JRssFeed;

public class RssFeedComparator implements Comparator<JRssFeed> {
	
	private static final Logger LOG = LoggerFactory.getLogger(RssFeedComparator.class);
	
	//private String userId;
	
	private UserLocation location;
	
	RssFeedComparator(String userId) {
		//this.userId = userId;
		try {
			UserLocationRepositoryService locationService = ServiceRegistry.INSTANCE
					.findService(UserLocationRepositoryService.class);
			location = locationService.findUserLocationById(userId);
		} catch (PackPackException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	private int compareRecentness(JRssFeed o1, JRssFeed o2) {
		long l = o2.getBatchId() - o1.getBatchId();
		if(l == 0) {
			return 0;
		}
		if(l > 0) {
			return 1;
		}
		return -1;
	}
	
	private int compareDistances(JRssFeed o1, JRssFeed o2) {
		List<JGeoTag> geoTags1 = o1.getGeoTags();
		List<JGeoTag> geoTags2 = o2.getGeoTags();
		double longitude = Double.parseDouble(location.getLongitude());
		double latitude = Double.parseDouble(location.getLatitude());
		
		int minDistance1 = Integer.MAX_VALUE;
		for(JGeoTag geoTag1 : geoTags1) {
			int distance = GeoLocationUtil.distance(latitude, geoTag1.getLatitude(), longitude, geoTag1.getLongitude());
			if(distance < minDistance1) {
				minDistance1 = distance;
			}
		}
		
		int minDistance2 = Integer.MAX_VALUE;
		for(JGeoTag geoTag2 : geoTags2) {
			int distance = GeoLocationUtil.distance(latitude, geoTag2.getLatitude(), longitude, geoTag2.getLongitude());
			if(distance < minDistance2) {
				minDistance2 = distance;
			}
		}
		return (minDistance1 - minDistance2) / 200;
	}
	
	@Override
	public int compare(JRssFeed o1, JRssFeed o2) {
		try {
			int result = compareRecentness(o1, o2);
			if(result == 0) {
				result = compareDistances(o1, o2);
			}
			return result;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}
}
