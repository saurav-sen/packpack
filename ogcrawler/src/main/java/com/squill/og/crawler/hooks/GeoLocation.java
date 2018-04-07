package com.squill.og.crawler.hooks;

public class GeoLocation {

	private double longitude;

	private double latitude;

	public GeoLocation(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}
}