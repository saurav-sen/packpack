package com.squill.og.crawler.internal;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.app.SystemPropertyKeys;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.utils.EncryptionUtil;
import com.squill.og.crawler.internal.utils.JSONUtil;
import com.squill.og.crawler.model.WebSpiderTracker;
import com.squill.services.exception.OgCrawlException;

/**
 * 
 * @author Saurav
 *
 */
@Component("fileStoreWebLinkTrackerService")
@Scope("prototype")
public class FileStoreWebLinkTrackerService implements IWebLinkTrackerService {


	private Properties db;

	private static final Logger LOG = LoggerFactory
			.getLogger(FileStoreWebLinkTrackerService.class);

	private IWebSite webSite;

	@Override
	public void init(IWebSite webSite) {
		this.webSite = webSite;
		try {
			db = new Properties();
			db.load(new FileReader(new File(
					resolveHistoryTrackerFilePath(webSite))));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	private String resolveHistoryTrackerFilePath(IWebSite webSite) {
		return System.getProperty(SystemPropertyKeys.WEB_CRAWLERS_CONFIG_DIR)
				+ File.separator + webSite.getUniqueId();
	}

	@Override
	public void dispose() {
		save();
	}

	private void save() {
		try {
			db.store(new FileWriter(new File(
					resolveHistoryTrackerFilePath(webSite))), "");
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void upsertCrawledInfo(String link, WebSpiderTracker value,
			long ttlSeconds, boolean updateLastModifiedTime) {
		if(updateLastModifiedTime) {
			updateLastModifedTime(value);
		}
		try {
			String key = EncryptionUtil.generateMD5HashKey(link, false, false);
			String json = JSONUtil.serialize(value);
			String base64EncodedJson = new String(Base64.getEncoder().encode(
					json.getBytes()));
			db.put(key, base64EncodedJson);
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (OgCrawlException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			save();
		}
	}
	
	private void updateLastModifedTime(WebSpiderTracker value) {
		if (value == null || value.getLink() == null
				|| value.getLink().trim().isEmpty())
			return;
		try {
			URL url = new URL(value.getLink());
			URLConnection connection = url.openConnection();
			String lastModified = connection.getHeaderField("Last-Modified");
			if(lastModified != null) {
				value.setLastModifiedSince(lastModified);
			}
		} catch (MalformedURLException e) {
			LOG.debug(e.getMessage(), e);
		} catch (IOException e) {
			LOG.debug(e.getMessage(), e);
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
		} catch (OgCrawlException e) {
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