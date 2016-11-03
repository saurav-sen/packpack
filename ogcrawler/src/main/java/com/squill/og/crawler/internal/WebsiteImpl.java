package com.squill.og.crawler.internal;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.squill.og.crawler.ICrawlSchedule;
import com.squill.og.crawler.IHtmlContentHandler;
import com.squill.og.crawler.ILink;
import com.squill.og.crawler.IRobotScope;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.content.handlers.ExpressionContext;
import com.squill.og.crawler.content.handlers.ExpressionContext.EvalContext;
import com.squill.og.crawler.model.LinkFilter;
import com.squill.og.crawler.model.WebCrawler;

/**
 * 
 * @author Saurav
 *
 */
public class WebsiteImpl implements IWebSite {

	private WebCrawler crawlerDef;

	public WebsiteImpl(WebCrawler crawlerDef) {
		this.crawlerDef = crawlerDef;
	}

	@Override
	public String getDomainUrl() {
		return crawlerDef.getDomainUrl();
	}

	@Override
	public IRobotScope getRobotScope() {
		return new IRobotScope() {

			@Override
			public boolean isScoped(String link) {
				LinkFilter linkFilter = crawlerDef.getLinkFilter();
				if (linkFilter == null)
					return true;
				EvalContext ctx = new EvalContext(link);
				ExpressionContext.set(ctx);
				return new LinkFilterConditionEvaluator().evalExp(linkFilter
						.getCondition());
			}

			@Override
			public int getDefaultCrawlDelay() {
				return 2;
			}

			@Override
			public List<? extends ILink> getAnyLeftOverLinks() {
				return Collections.emptyList();
			}
		};
	}

	@Override
	public IHtmlContentHandler getContentHandler() {
		return AppContext.INSTANCE.findService(crawlerDef.getContentHandler(),
				IHtmlContentHandler.class);
	}

	@Override
	public ICrawlSchedule getSchedule() {
		return new ICrawlSchedule() {

			@Override
			public TimeUnit getTimeUnit() {
				String timeUnit = crawlerDef.getScheduler().getTimeUnit().toUpperCase();
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
				return crawlerDef.getScheduler().getPeriodicDelay();
			}

			@Override
			public long getInitialDelay() {
				return crawlerDef.getScheduler().getInitialDelay();
			}
		};
	}

	@Override
	public boolean needToTrackCrawlingHistory() {
		return crawlerDef.isHistoryTracking();
	}

	@Override
	public boolean shouldCheckRobotRules() {
		return crawlerDef.isRobotRulesExists();
	}
}