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
		/*String text = new StringBuilder().append(feed.getOgUrl())
				.append(EMPTY_SPACE).append(feed.getOgTitle())
				.toString();
		return new StringBuilder().append(feed.getOgType())
				.append(CSV_COL_SEPARATOR).append(text)
				.append(CSV_ROW_SEPARATOR).toString();*/
		String ogType = feed.getOgType();
		String ogUrl = feed.getOgUrl();
		String ogTitle = feed.getOgTitle();
		String ogImage = feed.getOgImage();
		if(ogType == null || ogType.trim().isEmpty())
			return null;
		if(ogUrl == null || ogUrl.trim().isEmpty())
			return null;
		if(ogTitle == null || ogTitle.trim().isEmpty())
			return null;
		if(ogImage == null || ogImage.trim().isEmpty())
			return null;
		return new StringBuilder().append(ogType)
				.append(CSV_COL_SEPARATOR).append(ogUrl)
				.append(CSV_COL_SEPARATOR).append(ogTitle)
				.append(CSV_COL_SEPARATOR).append(ogImage)
				.append(CSV_ROW_SEPARATOR).toString();
	}
	
	public static JRssFeed fromString(String csvLineText) {
		String line = csvLineText.replaceAll("[\n\r]", "");
		String[] arr = line.split(CSV_COL_SEPARATOR);
		if(arr.length == 4) {
			String ogType = arr[0];
			String ogUrl = arr[1];
			String ogTitle = arr[2];
			String ogImage = arr[3];
			JRssFeed feed = new JRssFeed();
			feed.setOgUrl(ogUrl);
			feed.setOgTitle(ogTitle);
			feed.setOgType(ogType);
			feed.setOgImage(ogImage);
			return feed;
		}
		return null;
	}
}