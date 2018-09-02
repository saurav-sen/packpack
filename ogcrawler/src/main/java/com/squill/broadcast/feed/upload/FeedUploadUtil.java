package com.squill.broadcast.feed.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.util.SystemPropertyUtil;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeeds;

/**
 * 
 * @author Saurav
 *
 */
public class FeedUploadUtil {

	public static final String PRE_CLASSIFIED_FILE_PREFIX = "PreClass_";

	private static final Logger ERROR = LoggerFactory
			.getLogger(FeedUploadUtil.class);

	public static void main1(String[] args) {
		Calendar c = Calendar.getInstance();
		System.out.println(PRE_CLASSIFIED_FILE_PREFIX
				+ c.get(Calendar.DAY_OF_MONTH) + "_" + c.get(Calendar.MONTH)
				+ "_" + c.get(Calendar.YEAR) + ".json");
	}

	public static void main(String[] args) throws Exception {
		Calendar c = Calendar.getInstance();
		//c.add(Calendar.DATE, -1);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		System.out.println(hour);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		System.out.println(String.valueOf(day) + "_" + String.valueOf(month)
				+ "_" + String.valueOf(year));
		System.out.println((day < 10 ? "0" + String.valueOf(day) : String
				.valueOf(day))
				+ "_"
				+ (month < 10 ? "0" + String.valueOf(month) : String
						.valueOf(month)) + "_" + String.valueOf(year));
	}

	private FeedUploadUtil() {
	}

	public static JRssFeeds reloadSelectiveFeeds(boolean ignoreOlder) {
		String workingDirectory = SystemPropertyUtil.getDefaultArchiveRefreshmentFolder();
		if (workingDirectory == null) {
			return null;
		}
		File mlWCDir = new File(workingDirectory);
		if (!mlWCDir.exists()) {
			return null;
		}
		if (!mlWCDir.isDirectory()) {
			return null;
		}
		File[] files =  mlWCDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".json");
			}
		});
		
		if (files == null || files.length == 0) {
			return null;
		}
		
		FeedSelectionStrategy strategy = null;
		Map<String, List<JRssFeed>> finalFeeds = null;
		try {
			for (File file : files) {
				if (isLatestFileAvailable(file)) {
					finalFeeds = readFile(file);
				}
			}
			if(!ignoreOlder) {
				if(finalFeeds != null && !finalFeeds.isEmpty()) {
					strategy = FeedSelector.INSTANCE
							.createNewStrategy(SystemPropertyUtil
									.getFeedSelectionStrategyName());
					if (strategy != null) {
						for (File file : files) {
							Map<String, List<JRssFeed>> map = readFile(file);
							strategy.applyStrategy(map);
						}
					} else {
						ERROR.error("Failed to load feed selection strategy identified/configured as <"
								+ SystemPropertyUtil.getFeedSelectionStrategyName()
								+ ">");
					}
				}
			}
		} catch (IOException e) {
			ERROR.error(e.getMessage(), e);
		}

		if (strategy != null) {
			finalFeeds = strategy.getFinalSelection();
		}
		if(finalFeeds == null)
			return null;
		Iterator<List<JRssFeed>> itr = finalFeeds.values().iterator();
		List<JRssFeed> arrayList = new ArrayList<JRssFeed>();
		while (itr.hasNext()) {
			arrayList.addAll(itr.next());
		}
		strategy = null;
		finalFeeds = null;
		JRssFeeds result = new JRssFeeds();
		result.setFeeds(arrayList);
		return result;
	}

	private static boolean isLatestFileAvailable(File file) {
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		// TODAY
		if(file.getName().endsWith(
				(day < 10 ? "0" + String.valueOf(day) : String.valueOf(day))
						+ "_"
						+ (month < 10 ? "0" + String.valueOf(month) : String
								.valueOf(month)) + "_" + String.valueOf(year)
						+ ".json")
				|| file.getName().endsWith(
						String.valueOf(day) + "_" + String.valueOf(month) + "_"
								+ String.valueOf(year) + ".json")) {
			return true;
		}
		// YESTERDAY
		c.add(Calendar.DATE, -1);
		day = c.get(Calendar.DAY_OF_MONTH);
		month = c.get(Calendar.MONTH);
		year = c.get(Calendar.YEAR);
		if(file.getName().endsWith(
				(day < 10 ? "0" + String.valueOf(day) : String.valueOf(day))
						+ "_"
						+ (month < 10 ? "0" + String.valueOf(month) : String
								.valueOf(month)) + "_" + String.valueOf(year)
						+ ".json")
				|| file.getName().endsWith(
						String.valueOf(day) + "_" + String.valueOf(month) + "_"
								+ String.valueOf(year) + ".json")) {
			int hour = c.get(Calendar.HOUR_OF_DAY);
			if(hour < 12) {
				return true;
			}
		}
		return false;
	}

	private static Map<String, List<JRssFeed>> readFile(File file)
			throws IOException {
		if (file.getName().endsWith(".json")) {
			return readJsonFile(file);
		} else {
			return readCsvFile(file);
		}
	}
	
	private static Map<String, List<JRssFeed>> readJsonFile(File file)
			throws IOException {
		Map<String, List<JRssFeed>> map = new HashMap<String, List<JRssFeed>>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			StringBuilder json = new StringBuilder();
			String line = reader.readLine();
			while (line != null) {
				json.append(line);
				line = reader.readLine();
			}
			JRssFeeds feedsContainer = JSONUtil.deserialize(json.toString(),
					JRssFeeds.class);

			if (feedsContainer == null) {
				return map;
			}

			List<JRssFeed> feeds = feedsContainer.getFeeds();
			if (feeds == null) {
				return map;
			}

			for (JRssFeed feed : feeds) {
				String ogType = feed.getOgType();
				List<JRssFeed> list = map.get(ogType);
				if (list == null) {
					list = new LinkedList<JRssFeed>();
					map.put(ogType, list);
				}
				list.add(feed);
			}
		} catch (PackPackException e) {
			ERROR.error(e.getMessage(), e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				ERROR.error(e.getMessage(), e);
			}
		}
		return map;
	}

	private static Map<String, List<JRssFeed>> readCsvFile(File file)
			throws IOException {
		Map<String, List<JRssFeed>> map = new HashMap<String, List<JRssFeed>>();
		BufferedReader fileReader = null;
		try {
			fileReader = new BufferedReader(new FileReader(file));
			String line = fileReader.readLine();
			while (line != null) {
				JRssFeed feed = CsvUtil.fromString(line);
				if (feed == null) {
					ERROR.error("Failed to load Feed using CsvUtil#fromString("
							+ line + ")");
					ERROR.error("NULL feed received while loading using "
							+ "CsvUtilfromString(...), as mentioned above");
					continue;
				}
				String ogType = feed.getOgType();
				List<JRssFeed> list = map.get(ogType);
				if (list == null) {
					list = new LinkedList<JRssFeed>();
					map.put(ogType, list);
				}
				list.add(feed);
				line = fileReader.readLine();
			}
		} finally {
			try {
				if (fileReader != null) {
					fileReader.close();
				}
			} catch (IOException e) {
				ERROR.error(e.getMessage(), e);
			}
		}
		return map;
	}
}