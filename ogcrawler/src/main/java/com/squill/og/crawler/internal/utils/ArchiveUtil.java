package com.squill.og.crawler.internal.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.util.SystemPropertyUtil;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.feed.web.model.UploadType;

public final class ArchiveUtil {

	private static final Logger LOG = LoggerFactory
			.getLogger(ArchiveUtil.class);
	
	public static final String DEFAULT_ID = "newsapi.org";
	
	public static final int DEFAULT_MAX_TIME_DIFF_IN_HOURS = 6; // 6 Hours
	
	private ArchiveUtil() {
	}
	
	public static void storeNotificationMemento(CircularQueue<NotifyMsg> queue) {
		NotifyMsgs memento = new NotifyMsgs();
		Iterator<NotifyMsg> itr = queue.iterator();
		while(itr.hasNext()) {
			memento.getMsgs().add(itr.next());
		}
		try {
			String filePath = resolveNotifyMsgsFilePath();
			File file = new File(filePath);
			String json = JSONUtil.serialize(memento);
			if(!file.exists()) {
				LOG.debug("Writing notification messages to file @ " + filePath);
				Files.write(Paths.get(filePath), json.getBytes(), StandardOpenOption.CREATE);
			} else {
				LOG.debug("Writing notification messages to file @ " + filePath);
				Files.write(Paths.get(filePath), json.getBytes(), StandardOpenOption.WRITE);
			}
		} catch (Exception e) {
			LOG.error(
					"Failed loading recent notification messages from archive :: "
							+ e.getMessage(), e);
		}
	}
	
	public static CircularQueue<NotifyMsg> loadNotificationMemento() {
		SortedSet<NotifyMsg> result = new TreeSet<NotifyMsg>(
				new Comparator<NotifyMsg>() {
					@Override
					public int compare(NotifyMsg o1, NotifyMsg o2) {
						long l = o1.getTimestamp() - o2.getTimestamp();
						if (l == 0)
							return 0;
						if (l > 0) {
							return 1;
						}
						return -1;
					}
				});
		try {
			String filePath = resolveNotifyMsgsFilePath();
			File file = new File(filePath);
			if(file.exists()) {
				String json = new String(Files.readAllBytes(Paths.get(filePath)),
						"UTF-8");
				NotifyMsgs c = JSONUtil.deserialize(json, NotifyMsgs.class, true);
				if (c != null) {
					result.addAll(c.getMsgs());
				}
			}
		} catch (Exception e) {
			LOG.error(
					"Failed loading recent notification messages from archive :: "
							+ e.getMessage(), e);
		}
		CircularQueue<NotifyMsg> queue = new CircularQueue<NotifyMsg>(50);
		Iterator<NotifyMsg> itr = result.iterator();
		while(itr.hasNext()) {
			queue.add(itr.next());
		}
		return queue;
	}
	
	private static String resolveNotifyMsgsFilePath() {
		String dirPath = SystemPropertyUtil.getDefaultArchiveFolder();
		File file = new File(dirPath);
		if(!file.exists()) {
			file.mkdir();
		}
		if(!dirPath.endsWith(File.separator)) {
			dirPath = dirPath + File.separator;
		}
		dirPath = dirPath + "notification";
		StringBuilder fileName = new StringBuilder(dirPath).append(".json");
		return fileName.toString();
	}
	
	public static JRssFeeds storeInArchive(String id, List<JRssFeed> list) {
		JRssFeeds c = new JRssFeeds();
		LOG.debug("storeInArchive(String id, List<JRssFeed> list)");
		try {
			String filePath = resolveArchiveFilePath(id);
			File file = new File(filePath);
			if(!file.exists()) {
				c.getFeeds().addAll(list);
				String json = JSONUtil.serialize(c);
				LOG.debug("Writing to file @ " + filePath);
				Files.write(Paths.get(filePath), json.getBytes(), StandardOpenOption.CREATE);
			} else {
				String json = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
				c = JSONUtil.deserialize(json, JRssFeeds.class, true);
				c.getFeeds().addAll(list);
				json = JSONUtil.serialize(c);
				LOG.debug("Writing to file @ " + filePath);
				Files.write(Paths.get(filePath), json.getBytes(), StandardOpenOption.WRITE);
			}
		} catch (Exception e) {
			LOG.error("Failed storing in archive :: " + e.getMessage(), e);
		}
		return c;
	}
	
	public static List<JRssFeed> getFeedsUploadedFromArchive() {
		return getFeedsUploadedFromArchive(DEFAULT_ID);
	}
	
	public static List<JRssFeed> getFeedsUploadedFromArchive(String id) {
		return getFeedsUploadedFromArchive(id, DEFAULT_MAX_TIME_DIFF_IN_HOURS);
	}
	
	public static List<JRssFeed> getFeedsUploadedFromArchive(String id, int maxTimeDiffInHours) {
		return getFeedsUploadedFromArchive(id, maxTimeDiffInHours, null);
	}
	
	public static List<JRssFeed> getFeedsUploadedFromArchive(String id, int maxTimeDiffInHours, UploadType uploadType) {
		List<JRssFeed> result = new LinkedList<JRssFeed>();
		try {
			long cTime = System.currentTimeMillis();
			String filePath = resolveArchiveFilePath(id);
			File file = new File(filePath);
			if (file.exists()) {
				String json = new String(
						Files.readAllBytes(Paths.get(filePath)), "UTF-8");
				JRssFeeds c = JSONUtil.deserialize(json, JRssFeeds.class, true);
				if (c != null) {
					Iterator<JRssFeed> itr = c.getFeeds().iterator();
					while(itr.hasNext()) {
						JRssFeed feed = itr.next();
						if (uploadType != null) {
							String ogType = feed.getUploadType();
							UploadType uploadType2 = UploadType.resolve(ogType);
							if(uploadType != uploadType2)
								continue;
						}						
						long uTime = feed.getUploadTime();
						long diff = cTime - uTime;
						diff = Math.abs(diff);
						int hours = (int) (diff / (1000 * 60 * 60));
						if(hours <= maxTimeDiffInHours) {
							result.add(feed);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Failed reading from archive :: " + e.getMessage(), e);
		}
		return result;
	}
	
	private static String resolveArchiveFilePath(String id) {
		DateTime dateTime = new DateTime();
		int day = dateTime.getDayOfMonth();
		int month = dateTime.getMonthOfYear();
		int year = dateTime.getYear();
		String dirPath = SystemPropertyUtil.getDefaultArchiveFolder();
		File file = new File(dirPath);
		if(!file.exists()) {
			file.mkdir();
		}
		dirPath = dirPath + id;
		StringBuilder fileName = new StringBuilder(dirPath).append("-");
		if(day < 10) {
			fileName.append("0");
		}
		fileName.append(String.valueOf(day));
		fileName.append("-");
		if(month < 10) {
			fileName.append("0");
		}
		fileName.append(String.valueOf(month));
		fileName.append("-");
		fileName.append(String.valueOf(year));
		fileName.append(".json");
		return fileName.toString();
	}
	
	public static Map<String, List<JRssFeed>> storeInArchive(Map<String, List<JRssFeed>> map) {
		Map<String, List<JRssFeed>> result = new HashMap<String, List<JRssFeed>>();
		LOG.debug("Storing in archive");
		Iterator<String> itr = map.keySet().iterator();
		while(itr.hasNext()) {
			String id = itr.next();
			List<JRssFeed> list = map.get(id);
			if(list.isEmpty())
				continue;
			JRssFeeds c = storeInArchive(id, list);
			if(c != null) {
				result.put(id, c.getFeeds());
			}
		}
		return result;
	}
}