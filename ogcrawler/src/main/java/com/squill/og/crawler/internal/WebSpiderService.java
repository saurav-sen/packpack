package com.squill.og.crawler.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.og.crawler.ICrawlSchedule;
import com.squill.og.crawler.ICrawlable;
import com.squill.og.crawler.Spider;
import com.squill.og.crawler.SpiderSession;
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.ISpiderSession;
import com.squill.services.exception.OgCrawlException;

@Component
@Scope("singleton")
public class WebSpiderService {

	private ScheduledExecutorService pool;

	private static Logger LOG = LoggerFactory.getLogger(WebSpiderService.class);

	@PostConstruct
	public void startup() throws OgCrawlException {
		pool = Executors
				.newScheduledThreadPool(2, new ControlledInstantiator());
	}

	public void shutdown() throws OgCrawlException {
		if (pool != null) {
			pool.shutdown();
		}
	}

	public void startCrawling(List<? extends ICrawlable> crawlables, IFeedUploader feedUploader) {
		if (crawlables == null || crawlables.isEmpty())
			return;
		List<Future<?>> list = new ArrayList<Future<?>>();
		int count = 0;
		int len = crawlables.size();
		ISpiderSession session = SpiderSessionFactory.INSTANCE.createNewSession(feedUploader);
		for (int i = 0; i < len; i++) {
			ICrawlable crawlable = crawlables.get(i);

			ICrawlSchedule schedule = crawlable.getSchedule();
			long period = schedule.getPeriodicDelay();
			TimeUnit timeUnit = schedule.getTimeUnit();
			if (period < 0 || timeUnit == null) {
				period = 0;
				if (timeUnit == null) {
					period = 1;
					timeUnit = TimeUnit.DAYS;
				}
			}
			long crawlSchedulePeriodicTimeInMillis = crawlSchedulePeriodicTimeInMillis(
					period, timeUnit);
			Spider spider = SpiderFactory.INSTANCE.createNewSpiderInstance(
					crawlable, crawlSchedulePeriodicTimeInMillis,
					crawlable.getTrackerService(), (SpiderSession) session);
			if(spider == null) {
				LOG.error("Failed to initialize Spider instance for <" + crawlable.getUniqueId() + ">");
				continue;
			}
			Future<?> future = pool.scheduleAtFixedRate(spider,
					schedule.getInitialDelay(),
					crawlSchedulePeriodicTimeInMillis, TimeUnit.MILLISECONDS);
			list.add(future);
			count++;
			if (count >= 10) {
				try {
					// To optimize concurrency.
					Thread.sleep(30 * 60 * 1000);
				} catch (InterruptedException e) {
					LOG.debug(e.getMessage(), e);
				} finally {
					count = 0;
				}
			}
			/*
			 * if(count == 20) { // Max concurrent submission allowed
			 * waitFor(list.subList(0, 20/2)); count = 0; }
			 */
		}
		// Main thread should keep waiting forever.
		waitFor(list);
		feedUploader.postCompleteAll(session);
	}

	private long crawlSchedulePeriodicTimeInMillis(long period,
			TimeUnit timeUnit) {
		long result = period;
		switch (timeUnit) {
		case DAYS:
			result = result * 24 * 60 * 60 * 1000;
			break;
		case HOURS:
			result = result * 60 * 60 * 1000;
			break;
		default:
			result = result * 24 * 60 * 60 * 1000;
			break;
		}
		return result;
	}

	private void waitFor(List<Future<?>> list) {
		Iterator<Future<?>> itr = list.iterator();
		while (itr.hasNext()) {
			Future<?> future = itr.next();
			while (!future.isDone()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			itr.remove();
		}
	}
}