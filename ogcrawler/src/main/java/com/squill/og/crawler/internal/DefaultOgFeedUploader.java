package com.squill.og.crawler.internal;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.util.RssFeedUtil;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.feed.web.model.TTL;
import com.squill.og.crawler.IWebCrawlable;
import com.squill.og.crawler.app.SystemPropertyKeys;
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.ISpiderSession;
import com.squill.og.crawler.internal.utils.JSONUtil;

/**
 * 
 * @author Saurav
 *
 */
@Component("defaultOgFeedUploader")
@Scope("prototype")
public class DefaultOgFeedUploader implements IFeedUploader {

	/*private static final String BASE_URL_CONFIG = "BASE_URL";
	private static final String URL_PART_CONFIG = "URL_PART";
	private static final String API_KEY_CONFIG = "API_KEY";
	
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	private static final String APPLICATION_JSON = "application/json";*/

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultOgFeedUploader.class);
	
	private static final String DEFAULT_ARCHIVE_FOLDER = "archive/";
	
	private int count = 0;
	
	@Override
	public void beginEach(ISpiderSession session, IWebCrawlable webCrawlable) {
		if(!webCrawlable.isUploadIndependently()) {
			count++;
		}
	}
	
	@Override
	public void endEach(ISpiderSession session, IWebCrawlable webCrawlable) {
		Map<String, List<JRssFeed>> map = new HashMap<String, List<JRssFeed>>();
		if(!webCrawlable.isUploadIndependently()) {
			count--;
		}
		JRssFeeds feeds = session.getFeeds(webCrawlable);
		if(feeds == null) {
			LOG.debug("No feeds found for :: " + webCrawlable.getUniqueId());
			return;
		}
		List<JRssFeed> list = feeds.getFeeds();
		if(list == null || list.isEmpty()) {
			LOG.debug("No feeds found for :: " + webCrawlable.getUniqueId());
			return;
		}
		map.put(webCrawlable.getUniqueId(), list);
		if (webCrawlable.isUploadIndependently()) {
			try {
				uploadBulk(map, session.getBatchId());
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		} else if (count == 0) {
			try {
				uploadBulk(map, session.getBatchId());
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	protected JRssFeeds adapt(Map<String, List<JRssFeed>> map) {
		JRssFeeds allFeeds = new JRssFeeds();
		Iterator<List<JRssFeed>> itr = map.values().iterator();
		while(itr.hasNext()) {
			List<JRssFeed> list = itr.next();
			if(list == null || list.isEmpty())
				continue;
			allFeeds.getFeeds().addAll(list);
		}
		return allFeeds;
	}
	
	private String resolveArchiveFolder() {
		return System.getProperty(SystemPropertyKeys.WEB_CRAWLERS_CONFIG_DIR) + "/../"
				+ DEFAULT_ARCHIVE_FOLDER;
	}
	
	private void storeInArchive(String id, List<JRssFeed> list) {
		try {
			String filePath = resolveArchiveFilePath(id);
			File file = new File(filePath);
			if(!file.exists()) {
				JRssFeeds c = new JRssFeeds();
				c.getFeeds().addAll(list);
				String json = JSONUtil.serialize(c);
				Files.write(Paths.get(filePath), json.getBytes(), StandardOpenOption.CREATE);
			} else {
				String json = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
				JRssFeeds c = JSONUtil.deserialize(json, JRssFeeds.class, true);
				c.getFeeds().addAll(list);
				json = JSONUtil.serialize(c);
				Files.write(Paths.get(filePath), json.getBytes(), StandardOpenOption.WRITE);
			}
		} catch (Exception e) {
			LOG.error("Failed storing in archive :: " + e.getMessage(), e);
		}
	}
	
	private String resolveArchiveFilePath(String id) {
		DateTime dateTime = new DateTime();
		int day = dateTime.getDayOfMonth();
		int month = dateTime.getMonthOfYear();
		int year = dateTime.getYear();
		String dirPath = resolveArchiveFolder() + id;
		File file = new File(dirPath);
		if(!file.exists()) {
			file.mkdir();
		}
		StringBuilder fileName = new StringBuilder(dirPath);
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
	
	private void storeInArchive(Map<String, List<JRssFeed>> map) {
		Iterator<String> itr = map.keySet().iterator();
		while(itr.hasNext()) {
			String id = itr.next();
			List<JRssFeed> list = map.get(id);
			if(list.isEmpty())
				continue;
			storeInArchive(id, list);
		}
	}

	/*private void uploadBulk(Map<String, List<JRssFeed>> map) throws Exception {
		if(!ServiceIdResolver.isUploadMode())
			return;
		storeInArchive(map);
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			String url = System.getProperty(BASE_URL_CONFIG);
			if(url != null && !url.isEmpty() && url.startsWith("${") && url.endsWith("}")) {
				url = url.substring(0, url.length()-1);
				url = url.replaceFirst("\\$\\{", "");
				url = System.getProperty(url);
			}
			url = url + System.getProperty(URL_PART_CONFIG);
			HttpPut PUT = new HttpPut(url);
			PUT.addHeader(CoreConstants.AUTHORIZATION_HEADER,
					System.getProperty(API_KEY_CONFIG));
			PUT.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
			JRssFeeds rssFeeds = deDuplicate(map);
			String json = JSONUtil.serialize(rssFeeds);
			PUT.setEntity(new StringEntity(json));
			LOG.info("Invoking 'PUT " + url
					+ "' (ML Api) for classification and bulk upload of feeds");
			client.execute(PUT);
		} finally {
			aggregatedFeedsMap.clear();
		}
	}*/
	
	private void uploadBulk(Map<String, List<JRssFeed>> map, long batchId) throws Exception {
		if(!ServiceIdResolver.isUploadMode())
			return;
		storeInArchive(map);
		JRssFeeds rssFeeds = adapt(map);
		LOG.info("Uploading news feeds: Total = " + rssFeeds.getFeeds().size());
		TTL ttl = new TTL();
		ttl.setTime((short) 1);
		ttl.setUnit(TimeUnit.DAYS);
		RssFeedUtil.uploadNewFeeds(rssFeeds, ttl, batchId, true);
	}
}