package com.squill.og.crawler.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.services.exception.PackPackException;
import com.squill.og.crawler.ICrawlSchedule;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.WebSiteSpider;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;

@Component
@Scope("singleton")
public class WebSpiderService {

	private ScheduledExecutorService pool;

	private IWebLinkTrackerService trackerService;

	@PostConstruct
	public void startup() throws PackPackException {
		pool = Executors
				.newScheduledThreadPool(1, new ControlledInstantiator());
	}

	public void shutdown() throws PackPackException {
		if (pool != null) {
			pool.shutdown();
		}
	}

	public void crawlWebSites(List<IWebSite> webSites) {
		if (webSites == null || webSites.isEmpty())
			return;
		List<Future<?>> list = new ArrayList<Future<?>>();
		for (IWebSite webSite : webSites) {
			WebSiteSpider spider = new WebSiteSpider(webSite, trackerService);
			ICrawlSchedule schedule = webSite.getSchedule();
			long period = schedule.getPeriodicDelay();
			TimeUnit timeUnit = schedule.getTimeUnit();
			if (period < 0 || timeUnit == null) {
				period = 0;
				if (timeUnit == null) {
					period = 2;
					timeUnit = TimeUnit.SECONDS;
				}
			}
			Future<?> future = pool.scheduleAtFixedRate(spider,
					schedule.getInitialDelay(), period, timeUnit);
			list.add(future);
		}
		for (Future<?> future : list) {
			while (!future.isDone()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setTrackerService(IWebLinkTrackerService trackerService) {
		this.trackerService = trackerService;
	}
}