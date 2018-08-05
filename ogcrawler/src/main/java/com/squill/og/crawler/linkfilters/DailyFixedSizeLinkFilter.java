package com.squill.og.crawler.linkfilters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.hooks.ILinkFilter;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.utils.DateTimeUtil;

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
	
	protected DailyFixedSizeLinkFilter(int maxNumberOfLinksPerDay) {
		this.maxNumberOfLinksPerDay = maxNumberOfLinksPerDay;
	}

	@Override
	public boolean isScoped(String linkUrl) {
		IWebLinkTrackerService service = getTrackerService();
		if (service == null) {
			return true;
		}
		int intValue = 1;
		String todaysKey = today();
		long ttlSeconds = 24 * 60 * 60;
		String value = service.getValue(KEY_PREFIX, todaysKey);
		if(value != null) {
			try {
				intValue = Integer.parseInt(value.trim()) + 1;
			} catch (NumberFormatException e) {
				LOG.error(e.getMessage(), e);
				return true;
			}
		}
		service.addValue(KEY_PREFIX, todaysKey, String.valueOf(intValue),
				ttlSeconds);
		return intValue <= maxNumberOfLinksPerDay;
	}
	
	protected String today() {
		return DateTimeUtil.today();
	}
	
	protected String toDateString(int dd, int mm, int yyyy) {
		return DateTimeUtil.toDateString(dd, mm, yyyy);
	}

	public void setTrackerService(IWebLinkTrackerService trackerService) {
		this.trackerService = trackerService;
	}

	protected IWebLinkTrackerService getTrackerService() {
		return trackerService;
	}
}
