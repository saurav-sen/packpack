package com.pack.pack.data.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.feed.selection.strategy.FeedSelectionStrategy;
import com.pack.pack.feed.selection.strategy.FeedSelector;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.JRssFeeds;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
public class FeedUploadUtil {

	public static final String PRE_CLASSIFIED_FILE_PREFIX = "PreClass_";

	private static final Logger ERROR = LoggerFactory
			.getLogger(FeedUploadUtil.class);

	private FeedUploadUtil() {
	}

	public static JRssFeeds reloadSelectiveFeeds() {
		String mlWC = SystemPropertyUtil.getMlWorkingDirectory();
		if (mlWC == null) {
			return null;
		}
		File mlWCDir = new File(mlWC);
		if (!mlWCDir.exists()) {
			return null;
		}
		if (!mlWCDir.isDirectory()) {
			return null;
		}
		File[] files = mlWCDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName()
						.startsWith(PRE_CLASSIFIED_FILE_PREFIX);
			}
		});
		if (files == null) {
			return null;
		}
		FeedSelectionStrategy strategy = FeedSelector.INSTANCE
				.createNewStrategy(SystemPropertyUtil
						.getFeedSelectionStrategyName());
		for (File file : files) {
			try {
				Map<String, List<JRssFeed>> map = readFile(file);
				strategy.applyStrategy(map);
			} catch (IOException e) {
				ERROR.error(e.getMessage(), e);
			}
		}
		Map<String, List<JRssFeed>> map = strategy.getFinalSelection();
		Iterator<List<JRssFeed>> itr = map.values().iterator();
		List<JRssFeed> arrayList = new ArrayList<JRssFeed>();
		while (itr.hasNext()) {
			arrayList.addAll(itr.next());
		}
		JRssFeeds result = new JRssFeeds();
		result.setFeeds(arrayList);
		return result;
	}

	private static Map<String, List<JRssFeed>> readFile(File file)
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