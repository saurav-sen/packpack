package com.squill.og.crawler.internal;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.utils.EncryptionUtil;
import com.squill.og.crawler.internal.utils.JSONUtil;
import com.squill.og.crawler.model.WebSpiderTracker;
import com.squill.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
@Component("defaultWebLinkTrackerService")
@Scope("singleton")
public class DefaultWebLinkTrackerService implements IWebLinkTrackerService {

	private static final String HISTORY_TRACKER_FILE = "history.tracker.file";
	private static final String DEFAULT_HISTORY_TRACKER_FILE_PATH = "../conf/web-tracker.db";

	private Properties db;

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultWebLinkTrackerService.class);

	@PostConstruct
	private void init() {
		try {
			db = new Properties();
			db.load(new FileReader(new File(resolveHistoryTrackerFilePath())));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	private String resolveHistoryTrackerFilePath() {
		String property = System.getProperty(HISTORY_TRACKER_FILE);
		if (property != null && !property.trim().isEmpty())
			return property;
		return DEFAULT_HISTORY_TRACKER_FILE_PATH;
	}

	@Override
	public void dispose() {
		save();
	}

	private void save() {
		try {
			db.store(new FileWriter(new File(resolveHistoryTrackerFilePath())),
					"");
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void addCrawledInfo(String link, WebSpiderTracker value,
			long ttlSeconds) {
		try {
			String key = EncryptionUtil.generateMD5HashKey(link, false, false);
			String json = JSONUtil.serialize(value);
			String base64EncodedJson = new String(Base64.getEncoder().encode(
					json.getBytes()));
			db.put(key, base64EncodedJson);
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (PackPackException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			save();
		}
	}

	@Override
	public WebSpiderTracker getTrackedInfo(String link) {
		try {
			String key = EncryptionUtil.generateMD5HashKey(link, false, false);
			if (!db.containsKey(key)) {
				return null;
			}
			String base64EncodedJson = db.getProperty(key);
			String json = new String(Base64.getDecoder().decode(
					base64EncodedJson));
			return JSONUtil.deserialize(json, WebSpiderTracker.class);
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (PackPackException e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public void clearAll() {
		db.clear();
		save();
	}
}