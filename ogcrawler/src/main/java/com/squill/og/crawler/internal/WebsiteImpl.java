package com.squill.og.crawler.internal;

import java.util.concurrent.TimeUnit;

import com.squill.og.crawler.ICrawlSchedule;
import com.squill.og.crawler.IHtmlContentHandler;
import com.squill.og.crawler.IRobotScope;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.model.Scheduler;
import com.squill.og.crawler.model.ScopeDef;

/**
 * 
 * @author Saurav
 *
 */
public class WebsiteImpl implements IWebSite {

	private String domainUrl;

	private ScopeDef scopeDef;

	private String contentHandler;

	private Scheduler scheduler;

	private boolean historyTracking;

	private boolean robotRulesExists;

	public WebsiteImpl(String domainUrl, ScopeDef scopeDef,
			String contentHandler, Scheduler scheduler) {
		this(domainUrl, scopeDef, contentHandler, scheduler, false);
	}

	public WebsiteImpl(String domainUrl, ScopeDef scopeDef,
			String contentHandler, Scheduler scheduler, boolean historyTracking) {
		this(domainUrl, scopeDef, contentHandler, scheduler, false, true);
	}

	public WebsiteImpl(String domainUrl, ScopeDef scopeDef,
			String contentHandler, Scheduler scheduler,
			boolean historyTracking, boolean robotRulesExists) {
		this.domainUrl = domainUrl;
		this.scopeDef = scopeDef;
		this.scheduler = scheduler;
		this.historyTracking = historyTracking;
		this.robotRulesExists = robotRulesExists;
	}

	@Override
	public String getDomainUrl() {
		return domainUrl;
	}

	@Override
	public IRobotScope getRobotScope() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IHtmlContentHandler getContentHandler() {
		return AppContext.INSTANCE.findService(contentHandler,
				IHtmlContentHandler.class);
	}

	@Override
	public ICrawlSchedule getSchedule() {
		return new ICrawlSchedule() {

			@Override
			public TimeUnit getTimeUnit() {
				String timeUnit = scheduler.getTimeUnit().toUpperCase();
				TimeUnit unit = null;
				try {
					unit = TimeUnit.valueOf(timeUnit);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return unit != null ? unit : TimeUnit.DAYS;
			}

			@Override
			public long getPeriodicDelay() {
				return scheduler.getPeriodicDelay();
			}

			@Override
			public long getInitialDelay() {
				return scheduler.getInitialDelay();
			}
		};
	}

	@Override
	public boolean needToTrackCrawlingHistory() {
		return historyTracking;
	}

	@Override
	public boolean shouldCheckRobotRules() {
		return robotRulesExists;
	}
}