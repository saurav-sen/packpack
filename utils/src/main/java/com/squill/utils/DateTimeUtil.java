package com.squill.utils;

import java.util.Calendar;

import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 
 * @author Saurav
 *
 */
public class DateTimeUtil {
	
	private static final String DEFAULT_DATE_SEPARATOR = "/";
	
	private static final String[] FORMATS = new String[] {"EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd MMM yyyy HH:mm:ss Z"};
	
	private DateTimeUtil() {
	}
	
	private static long parseDate(String pubDate, String dateFormat) {
		try {
			DateTimeFormatter formatter = DateTimeFormat.forPattern(dateFormat);
			return formatter.parseDateTime(pubDate).getMillis();
		} catch (Exception e) {
			return -1;
		}
	}
	
	public static void main(String[] args) {
		
		System.out.println(parseDate("Thu, 11 Jul 2019 09:39:23 GMT", "EEE, dd MMM yyyy HH:mm:ss zzz"));
		System.out.println(parseDate("Thu, 11 Jul 2019 05:13:44 +0530", "EEE, dd MMM yyyy HH:mm:ss Z"));
		System.out.println(parseDate("Thu, 11 Jul 2019 16:38:06 +0530", "EEE, dd MMM yyyy HH:mm:ss Z"));
		System.out.println(parseDate("Wed, 10 Jul 2019 23:13:10 +0000", "EEE, dd MMM yyyy HH:mm:ss Z"));
		System.out.println(parseDate("Thu, 11 Jul 2019 09:48:59 GMT", "EEE, dd MMM yyyy HH:mm:ss zzz"));
		System.out.println(Instant.parse("2019-07-10T23:30:34+01:00").getMillis());
		System.out.println(Instant.parse("2019-07-10T23:30:34+01:00").getMillis());
		System.out.println(parseDate("Tue, 21 May 2019 11:00:00 GMT", "EEE, dd MMM yyyy HH:mm:ss zzz"));
		System.out.println(parseDate("Thu, 11 Jul 2019 09:48:59 GMT", "EEE, dd MMM yyyy HH:mm:ss zzz"));
		System.out.println(parseDate("Thu, 11 Jul 2019 03:51:52 -0500", "EEE, dd MMM yyyy HH:mm:ss Z"));
	}
	
	public static long parse(String date) {
		long r = -1;
		for(int i=0; i<FORMATS.length; i++) {
			r = parseDate(date, FORMATS[i]);
			if(r > 0) {
				return r;
			}
		}
		try {
			r = Instant.parse(date).getMillis();
			return r;
		} catch (Exception e) {
			return -1;
		}
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
