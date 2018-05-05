package com.pack.pack.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
public class GeoLocationUtil {

	private static final Logger LOG = LoggerFactory
			.getLogger(GeoLocationUtil.class);

	private GeoLocationUtil() {
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static GeoLocation resolveGeoLocation(String locality, String city, String country) {
		CloseableHttpClient client = null;
		try {
			HttpClientBuilder builder = HttpClientBuilder.create();
			client = builder.build();
			if(locality == null)
				locality = "";
			else {
				locality = locality.replaceAll("[^A-Za-z0-9]", " ");
			}
			city = city.replaceAll("[^A-Za-z0-9]", "");
			country = country.replaceAll("[^A-Za-z0-9]", "");
			HttpGet GET = new HttpGet(
					"https://maps.googleapis.com/maps/api/geocode/json?address="
							+ locality.replaceAll(" ", "+") + "+" + city + "+" + country + "&key="
							+ SystemPropertyUtil.getGoogleGeoCodingApiKey());
			HttpResponse response = client.execute(GET);
			String json = EntityUtils.toString(response.getEntity());
			Map<String, Object> map = JSONUtil.deserialize(json, Map.class);
			if(map == null || map.isEmpty())
				return null;
			Map<String, Object> location = (Map) ((Map) ((Map) ((List) map
					.get("results")).get(0)).get("geometry")).get("location");
			final double latitude = (Double) location.get("lat");
			final double longitude = (Double) location.get("lng");
			return new GeoLocation() {

				@Override
				public double getLongitude() {
					return longitude;
				}

				@Override
				public double getLatitude() {
					return latitude;
				}
			};
		} catch (ClientProtocolException e) {
			LOG.info(e.getMessage(), e);
		} catch (ParseException e) {
			LOG.info(e.getMessage(), e);
		} catch (IOException e) {
			LOG.info(e.getMessage(), e);
		} catch (PackPackException e) {
			LOG.info(e.getMessage(), e);
		} finally {
			try {
				if (client != null) {
					client.close();
				}
			} catch (IOException e) {
				LOG.trace(e.getMessage(), e);
			}
		}
		return null;
	}

	public interface GeoLocation {

		public double getLongitude();

		public double getLatitude();
	}

	/**
	 * 
	 * Ignoring difference due to altitude/height differences.
	 * 
	 * @param lat1
	 * @param lat2
	 * @param lon1
	 * @param lon2
	 * @return
	 */
	public static int distance(double lat1, double lat2, double lon1,
	        double lon2) {
		return distance(lat1, lat2, lon1, lon2, 0, 0);
	}
	
	/**
	 * 
	 * https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
	 * 
	 * Uses Haversine method as its base (https://en.wikipedia.org/wiki/Haversine_formula)
	 * 
	 * Calculate distance between two points in latitude and longitude taking
	 * into account height difference. If you are not interested in height
	 * difference pass 0.0.
	 * 
	 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
	 * el2 End altitude in meters
	 * @returns Distance in Kilometers.
	 */
	public static int distance(double lat1, double lat2, double lon1,
	        double lon2, double el1, double el2) {

	    final int R = 6371; // Radius of the earth

	    double latDistance = Math.toRadians(lat2 - lat1);
	    double lonDistance = Math.toRadians(lon2 - lon1);
	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c ; // In Kilometers
	    //double distance = R * c * 1000; // convert to meters

	    double height = el1 - el2;

	    distance = Math.pow(distance, 2) + Math.pow(height, 2);

	    return (int) Math.ceil(Math.sqrt(distance));
	}
}