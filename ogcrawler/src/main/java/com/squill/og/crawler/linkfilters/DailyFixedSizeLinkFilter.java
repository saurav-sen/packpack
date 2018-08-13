package com.squill.og.crawler.linkfilters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.hooks.ILinkFilter;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.utils.DateTimeUtil;
import com.squill.og.crawler.model.WebSpiderTracker;

/**
 * 
 * @author Saurav
 *
 */
public abstract class DailyFixedSizeLinkFilter implements ILinkFilter {
	
	private int maxNumberOfLinksPerDay = 0;
	
	private IWebLinkTrackerService trackerService;
	
	private static final String KEY_PREFIX = "DFSLF";
	
	private static final Logger LOG = LoggerFactory.getLogger(DailyFixedSizeLinkFilter.class);
	
	private boolean needToRefresh = true;
	
	protected DailyFixedSizeLinkFilter(int maxNumberOfLinksPerDay) {
		this.maxNumberOfLinksPerDay = maxNumberOfLinksPerDay;
	}
	
	private void refresh() {
		if(!needToRefresh) {
			return;
		}
		IWebLinkTrackerService service = getTrackerService();
		String todaysKey = today() + todayKeySuffix();
		long ttlSeconds = 24 * 60 * 60;
		service.addValue(KEY_PREFIX, todaysKey, String.valueOf(0),
				ttlSeconds);
		needToRefresh = false;
	}

	@Override
	public boolean isScoped(String linkUrl) {
		refresh();
		IWebLinkTrackerService service = getTrackerService();
		if (service == null) {
			return true;
		}
		WebSpiderTracker trackedInfo = service.getTrackedInfo(linkUrl);
		if(trackedInfo == null) { // Older Link (Already crawled link)
			return true;
		}
		int intValue = 1;
		String todaysKey = today() + todayKeySuffix();
		String value = service.getValue(KEY_PREFIX, todaysKey);
		if(value != null) {
			try {
				intValue = Integer.parseInt(value.trim()) + 1;
			} catch (NumberFormatException e) {
				LOG.error(e.getMessage(), e);
				return true;
			}
		}
		return intValue <= maxNumberOfLinksPerDay;
	}
	
	protected String today() {
		return DateTimeUtil.today();
	}
	
	protected abstract String todayKeySuffix();
	
	protected String toDateString(int dd, int mm, int yyyy) {
		return DateTimeUtil.toDateString(dd, mm, yyyy);
	}

	public void setTrackerService(IWebLinkTrackerService trackerService) {
		this.trackerService = trackerService;
	}

	protected IWebLinkTrackerService getTrackerService() {
		return trackerService;
	}
	
	@Override
	public void incrementLinkCount() {
		IWebLinkTrackerService service = getTrackerService();
		if (service == null) {
			return;
		}
		int intValue = 1;
		String todaysKey = today() + todayKeySuffix();
		long ttlSeconds = 24 * 60 * 60;
		String value = service.getValue(KEY_PREFIX, todaysKey);
		if(value != null) {
			try {
				intValue = Integer.parseInt(value.trim()) + 1;
			} catch (NumberFormatException e) {
				LOG.error(e.getMessage(), e);
				return;
			}
		}
		service.addValue(KEY_PREFIX, todaysKey, String.valueOf(intValue),
				ttlSeconds);
	}
}
