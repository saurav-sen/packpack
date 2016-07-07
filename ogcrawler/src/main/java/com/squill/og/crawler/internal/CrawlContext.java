package com.squill.og.crawler.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.CookieStore;

import com.squill.og.crawler.Report;

/**
 *
 * @author Saurav
 * @since 14-Mar-2015
 *
 */
public class CrawlContext {

	private CookieStore cookieStore;
	
	private String downloadHomeDir;
	
	private String userId;
	
	private Map<String, Object> scheduledCrawlableLinksMap = new HashMap<String, Object>();
	
	private Object OBJECT = new Object();
	
	private PageLink logoutLink;
	
	private List<Report> reports = new ArrayList<Report>();
	
	private String hostURLPrefix;
	
	public CookieStore getCookieStore() {
		return cookieStore;
	}

	public void setCookieStore(CookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

	public String getDownloadHomeDir() {
		return downloadHomeDir;
	}

	public void setDownloadHomeDir(String downloadHomeDir) {
		this.downloadHomeDir = downloadHomeDir;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String mrno) {
		this.userId = mrno;
	}
	
	public void scheduledForCrawl(PageLink pageLink) {
		scheduledCrawlableLinksMap.put(pageLink.getLink(), OBJECT);
	}
	
	public boolean isScheduledForCrawl(PageLink pageLink) {
		return scheduledCrawlableLinksMap.get(pageLink.getLink()) != null;
	}

	public PageLink getLogoutLink() {
		return logoutLink;
	}

	public void setLogoutLink(PageLink logoutLink) {
		this.logoutLink = logoutLink;
	}
	
	public void invalidate() throws Exception {
	}
	
	public List<Report> getReports() {
		return Collections.unmodifiableList(reports);
	}
	
	public void addReport(Report report) {
		reports.add(report);
	}

	public String getHostURLPrefix() {
		return hostURLPrefix;
	}

	public void setHostURLPrefix(String hostURLPrefix) {
		this.hostURLPrefix = hostURLPrefix;
	}
}