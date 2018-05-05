package com.pack.pack.test.util;

import com.pack.pack.util.GeoLocationUtil;

public class GeoLocationUtilTest {

	public static void main(String[] args) {
		int distance1 = GeoLocationUtil.distance(28.7041, 17.3850, 77.1025, 78.4867, 0, 0);
		System.out.println("Arial distance from Delhi to Hyderabad = " + (int) distance1 + " (km)");
		int distance2 = GeoLocationUtil.distance(22.5726, 17.3850, 88.3639, 78.4867, 0, 0);
		System.out.println("Arial distance from Kolkata to Hyderabad = " + (int) distance2 + " (km)");
		int result = distance1 - distance2;
		if(result == 0) {
			System.out.println("Equal Distance");
		} else if(result < 0) {
			System.out.println("Delhi is nearer to Hyderabad than Kolkata");
			System.out.println("Actual Difference in Kilometers = " + Math.abs(result));
		} else if(result > 0) {
			System.out.println("Kolkata is nearer to Hyderabad than Delhi");
			System.out.println("Actual Difference = " + Math.abs(result) + " (km)");
		}
		result = result / 200;
		if(result == 0) {
			System.out.println("Ignoring 200 km distance, they are at EQUAL distance");
		}
	}
}
