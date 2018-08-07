package com.squill.og.crawler.internal.utils;

import java.util.Calendar;

/**
 * 
 * @author Saurav
 *
 */
public class DateTimeUtil {
	
	private DateTimeUtil() {
	}

	public static String today() {
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		String dd = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
		String mm = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
		String yyyy = String.valueOf(year);
		return dd + "/" + mm + "/" + yyyy;
	}
	
	public static String toDateString(int dd, int mm, int yyyy) {
		StringBuilder date = new StringBuilder();
		if(dd < 10) {
			date.append("0");
		}
		date.append(String.valueOf(dd));
		date.append("/");
		if(mm < 10) {
			date.append("0");
		}
		date.append(String.valueOf(mm));
		date.append("/");
		date.append(String.valueOf(yyyy));
		return date.toString();
	}
}