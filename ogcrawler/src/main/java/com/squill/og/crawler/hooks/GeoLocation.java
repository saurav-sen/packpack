package com.squill.og.crawler.hooks;

public interface GeoLocation {

	public double getLongitude();

	public double getLatitude();

	public String getPlaceName();

	public String getCountryCode();
}