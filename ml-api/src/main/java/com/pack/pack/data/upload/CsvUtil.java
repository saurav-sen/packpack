package com.pack.pack.data.upload;

import com.pack.pack.model.web.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
public class CsvUtil {
	
	public static final String CSV_COL_SEPARATOR = ",";
	public static final String CSV_ROW_SEPARATOR = "\n";
	
	public static final String EMPTY_SPACE = " ";

	private CsvUtil() {
	}
	
	public static String toString(JRssFeed feed) {
		String text = new StringBuilder().append(feed.getOgUrl())
				.append(EMPTY_SPACE).append(feed.getOgTitle())
				.toString();
		return new StringBuilder().append(feed.getOgType())
				.append(CSV_COL_SEPARATOR).append(text)
				.append(CSV_ROW_SEPARATOR).toString();
	}
	
	public static JRssFeed fromString(String csvLineText) {
		String line = csvLineText.replaceAll("[\n\r]", "");
		String[] arr = line.split(CSV_COL_SEPARATOR);
		if(arr.length == 2) {
			String ogType = arr[0];
			String text = arr[1];
			String[] arr2 = text.split(EMPTY_SPACE);
			if(arr.length == 2) {
				String ogUrl = arr2[0];
				String ogTitle = arr2[1];
				JRssFeed feed = new JRssFeed();
				feed.setOgUrl(ogUrl);
				feed.setOgTitle(ogTitle);
				feed.setOgType(ogType);
				return feed;
			}
			return null;
		}
		return null;
	}
}