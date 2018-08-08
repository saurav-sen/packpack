package com.squill.og.crawler;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.crawlercommons.robots.BaseRobotRules;
import com.squill.crawlercommons.sitemaps.SiteMapNews;
import com.squill.crawlercommons.sitemaps.SiteMapURL;

public abstract class AbstractRobotScope implements IRobotScope {

	private BaseRobotRules robotRules;
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractRobotScope.class);
	
	private boolean halted = false;
	
	private int linkCount;
	
	@Override
	public void setRobotRules(BaseRobotRules robotRules) {
		this.robotRules = robotRules;
	}
	
	protected abstract boolean ifScoped(String link);
	
	@Override
	public final boolean isScoped(String link) {
		if(isHalted())
			return false;
		if(!robotRules.isAllowed(link))
			return false;
		return ifScoped(link);
	}
	
	@Override
	public long getDefaultCrawlDelay() {
		long defaultInMillis = 2 * 1000;
		if(robotRules == null) {
			return defaultInMillis;
		}
		long delay = robotRules.getCrawlDelay() * 1000;
		if(delay < defaultInMillis) {
			return defaultInMillis;
		}
		return delay;
	}
	
	@Override
	public void halt() {
		halted = true;
	}
	
	protected boolean isHalted() {
		return halted && linkCount >= 3;
	}
	
	@Override
	public boolean isScopedSiteMap(SiteMapURL siteMapURL) {
		if(isHalted())
			return false;
		if (siteMapURL == null)
			return false;
		SiteMapNews siteMapNews = siteMapURL.getSiteMapNews();
		if (siteMapNews == null)
			return true;
		String publication_date = siteMapNews.getPublication_date();
		if (publication_date == null)
			return false;
		try {
			DateTime dateTime = new DateTime(publication_date);
			int dd0 = dateTime.getDayOfMonth();
			int mm0 = dateTime.getMonthOfYear();
			int yyyy0 = dateTime.getYear();

			Calendar c = Calendar.getInstance();
			int dd1 = c.get(Calendar.DAY_OF_MONTH);
			int mm1 = c.get(Calendar.MONTH) + 1;
			int yyyy1 = c.get(Calendar.YEAR);

			return dd0 == dd1 && mm0 == mm1 && yyyy0 == yyyy1;
		} catch (Exception e) {
			LOG.error("Error Parsing " + publication_date, e.getMessage(), e);
			return false;
		}
	}
	
	public abstract boolean ifScopedSiteMapUrl(String sitemapUrl);
	
	@Override
	public final boolean isScopedSiteMapUrl(String sitemapUrl) {
		if(isHalted()) {
			return false;
		}
		return ifScopedSiteMapUrl(sitemapUrl);
	}
	
	protected abstract List<? extends ILink> getIfAnyLeftOverLinks();
	
	@Override
	public final List<? extends ILink> getAnyLeftOverLinks() {
		if(isHalted()) {
			return Collections.emptyList();
		}
		return getIfAnyLeftOverLinks();
	}
	
	@Override
	public final void incrementLinkCount() {
		linkCount++;
	}
}
