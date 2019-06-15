package com.squill.utils;

import java.util.Calendar;

/**
 * 
 * @author Saurav
 *
 */
public class DateTimeUtil {
	
	private static final String DEFAULT_DATE_SEPARATOR = "/";
	
	private DateTimeUtil() {
	}
	
	public static String today() {
		return today(DEFAULT_DATE_SEPARATOR);
	}
	
	public static String yesterday() {
		return yesterday(DEFAULT_DATE_SEPARATOR);
	}
	
	public static String toDateString(int dd, int mm, int yyyy) {
		return toDateString(dd, mm, yyyy, DEFAULT_DATE_SEPARATOR);
	}

	public static String today(String separator) {
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		String dd = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
		String mm = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
		String yyyy = String.valueOf(year);
		return dd + separator + mm + separator + yyyy;
	}
	
	public static String yesterday(String separator) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		String dd = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
		String mm = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
		String yyyy = String.valueOf(year);
		return dd + separator + mm + separator + yyyy;
	}
	
	public static String toDateString(int dd, int mm, int yyyy, String separator) {
		StringBuilder date = new StringBuilder();
		if(dd < 10) {
			date.append("0");
		}
		date.append(String.valueOf(dd));
		date.append(separator);
		if(mm < 10) {
			date.append("0");
		}
		date.append(String.valueOf(mm));
		date.append(separator);
		date.append(String.valueOf(yyyy));
		return date.toString();
	}
}
