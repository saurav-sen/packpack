package com.squill.og.crawler.internal;

import com.squill.og.crawler.model.ApiReader;
import com.squill.og.crawler.model.WebCrawler;
import com.squill.og.crawler.model.WebCrawlers;
import com.squill.og.crawler.model.WebTracker;

public final class ServiceIdResolver {
	
	private static final String FILE_STORE_WEB_LINK_TRACKER_SERVICE = "fileStoreWebLinkTrackerService";

	private ServiceIdResolver() {
	}
	
	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
		System.out.println(1528011252738L);
		System.out.println( (System.currentTimeMillis() + 24 * 60 * 60 * 1000 - System.currentTimeMillis()) / (1000 * 60 * 60));
		long l = System.currentTimeMillis() - 1528011252738L;
		System.out.println(l);
		System.out.println(l / (1000 * 60 * 60));
	}
	
	public static boolean isUploadMode() {
		String op_mode = System.getProperty("operation.mode");
		return !"test".equals(op_mode);
	}
	
	public static String resolveWebTrackerServiceId(ApiReader crawlerDef, WebCrawlers crawlerDefParent) {
		if(!isUploadMode())
			return FILE_STORE_WEB_LINK_TRACKER_SERVICE;
		WebTracker webTracker = crawlerDef.getWebLinkTracker();
		if (webTracker == null) {
			webTracker = crawlerDefParent.getWebLinkTracker();
			if(webTracker == null) {
				return null;
			}
		}
		String serviceId = webTracker.getServiceId();
		return serviceId != null ? serviceId : FILE_STORE_WEB_LINK_TRACKER_SERVICE;
	}
	
	public static String resolveWebTrackerServiceId(WebCrawler crawlerDef, WebCrawlers crawlerDefParent) {
		String op_mode = System.getProperty("operation.mode");
		if("test".equals(op_mode))
			return FILE_STORE_WEB_LINK_TRACKER_SERVICE;
		WebTracker webTracker = crawlerDef.getWebLinkTracker();
		if (webTracker == null) {
			webTracker = crawlerDefParent.getWebLinkTracker();
			if(webTracker == null) {
				return null;
			}
		}
		String serviceId = webTracker.getServiceId();
		return serviceId != null ? serviceId : FILE_STORE_WEB_LINK_TRACKER_SERVICE;
	}
}
