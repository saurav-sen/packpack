package com.squill.og.crawler.internal;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.squill.og.crawler.AbstractRobotScope;
import com.squill.og.crawler.ICrawlSchedule;
import com.squill.og.crawler.ILink;
import com.squill.og.crawler.IRobotScope;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.content.handlers.ExpressionContext;
import com.squill.og.crawler.content.handlers.ExpressionContext.EvalContext;
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.IHtmlContentHandler;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.utils.CoreConstants;
import com.squill.og.crawler.model.Config;
import com.squill.og.crawler.model.ContentHandler;
import com.squill.og.crawler.model.FeedUploader;
import com.squill.og.crawler.model.LinkFilter;
import com.squill.og.crawler.model.WebCrawler;
import com.squill.og.crawler.model.WebTracker;

/**
 * 
 * @author Saurav
 *
 */
public class WebsiteImpl implements IWebSite {

	private WebCrawler crawlerDef;
	
	private IHtmlContentHandler contentHandler;
	
	private IWebLinkTrackerService historyTracker;
	
	private static final Logger LOG = LoggerFactory.getLogger(WebsiteImpl.class);
	
	public WebsiteImpl(WebCrawler crawlerDef) {
		this.crawlerDef = crawlerDef;
	}

	@Override
	public String getDomainUrl() {
		return crawlerDef.getDomainUrl();
	}

	@Override
	public IRobotScope getRobotScope() {
		return new AbstractRobotScope() {

			@Override
			public boolean ifScoped(String link) {
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
		if (contentHandler != null) {
			return contentHandler;
		}
		ContentHandler contentHandlerDef = crawlerDef.getContentHandler();
		contentHandler = loadContentHandler(contentHandlerDef);
		if (contentHandler != null) {
			String preClassifiedType = contentHandlerDef.getPreClassifiedType();
			if (preClassifiedType != null) {
				contentHandler.addMetaInfo(
						CoreConstants.PRE_CLASSIFIED_FEED_TYPE,
						preClassifiedType);
			}
			FeedUploader feedUploaderDef = contentHandlerDef.getFeedUploader();
			IFeedUploader feedUploader = loadFeedUploader(feedUploaderDef);
			if (feedUploader != null) {
				contentHandler.setFeedUploader(feedUploader);
			}
		}
		return contentHandler;
	}

	private IFeedUploader loadFeedUploader(FeedUploader feedUploaderDef) {
		IFeedUploader feedUploader = null;
		String uploader = feedUploaderDef.getUploader();
		try {
			feedUploader = AppContext.INSTANCE.findService(
					uploader, IFeedUploader.class);
		} catch (NoSuchBeanDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(feedUploader == null) {
			try {
				Object newInstance = Class.forName(uploader).newInstance();
				if(newInstance instanceof IFeedUploader) {
					feedUploader = (IFeedUploader)newInstance;
				}
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(feedUploader != null) {
			List<Config> configs = feedUploaderDef.getConfig();
			if(configs != null && !configs.isEmpty()) {
				for(Config config : configs) {
					String key = config.getKey();
					String value = config.getValue();
					if(value != null && !value.isEmpty() && value.startsWith("${") && value.endsWith("}")) {
						value = value.substring(0, value.length()-1);
						value = value.replaceFirst("\\$\\{", "");
						value = System.getProperty(value);
					}
					feedUploader.addConfig(key, value);
				}
			}
		}
		return feedUploader;
	}
	
	private IHtmlContentHandler loadContentHandler(ContentHandler contentHandlerDef) {
		IHtmlContentHandler contentHandler = null;
		String handler = contentHandlerDef.getHandler();
		try {
			contentHandler = AppContext.INSTANCE.findService(
					handler, IHtmlContentHandler.class);
		} catch (NoSuchBeanDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(contentHandler == null) {
			try {
				Object newInstance = Class.forName(handler).newInstance();
				if(newInstance instanceof IHtmlContentHandler) {
					contentHandler = (IHtmlContentHandler)newInstance;
				}
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return contentHandler;
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
	public boolean shouldCheckRobotRules() {
		return crawlerDef.isRobotRulesExists();
	}
	
	@Override
	public IWebLinkTrackerService getTrackerService() {
		if(historyTracker != null)
			return historyTracker;
		WebTracker webTracker = crawlerDef.getWebTracker();
		if(webTracker == null)
			return null;
		String serviceId = webTracker.getServiceId();
		try {
			historyTracker = AppContext.INSTANCE.findService(
					serviceId, IWebLinkTrackerService.class);
		} catch (NoSuchBeanDefinitionException e) {
			LOG.error(e.getMessage(), e);
		}
		if(historyTracker == null) {
			try {
				Object newInstance = Class.forName(serviceId).newInstance();
				if(newInstance instanceof IWebLinkTrackerService) {
					historyTracker = (IWebLinkTrackerService)newInstance;
				}
			} catch (InstantiationException e) {
				LOG.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return historyTracker;
	}
}