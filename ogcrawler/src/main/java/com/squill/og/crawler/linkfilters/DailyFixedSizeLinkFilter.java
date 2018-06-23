package com.squill.og.crawler.linkfilters;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.hooks.ILinkFilter;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;

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
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		String dd = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
		String mm = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
		String yyyy = String.valueOf(year);
		return dd + "/" + mm + "/" + yyyy;
	}
	
	protected String toDateString(int dd, int mm, int yyyy) {
		StringBuilder date = new StringBuilder();
		if(dd < 10) {
			date.append("0");
		}
		date.append(String.valueOf(dd));
		date.append("/");
		if(mm < 10) {
			date.append("0");
		}
		date.append(String.valueOf(mm));
		date.append("/");
		date.append(String.valueOf(yyyy));
		return date.toString();
	}

	public void setTrackerService(IWebLinkTrackerService trackerService) {
		this.trackerService = trackerService;
	}

	protected IWebLinkTrackerService getTrackerService() {
		return trackerService;
	}
}
