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

	/*public static void main(String[] args) {
		String str = "601, EDEN C, Casa Paradiso, Sanath Nagar";
		str = str.replaceAll("[^A-Za-z0-9]", " ");
		System.out.println(str);
	}*/
}