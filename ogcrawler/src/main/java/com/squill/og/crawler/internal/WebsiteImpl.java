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
import com.squill.og.crawler.hooks.IArticleTextSummarizer;
import com.squill.og.crawler.hooks.IGeoLocationResolver;
import com.squill.og.crawler.hooks.IHtmlContentHandler;
import com.squill.og.crawler.hooks.ITaxonomyResolver;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.utils.CoreConstants;
import com.squill.og.crawler.model.ArticleSummarizer;
import com.squill.og.crawler.model.ContentHandler;
import com.squill.og.crawler.model.GeoTagResolver;
import com.squill.og.crawler.model.LinkFilter;
import com.squill.og.crawler.model.TaxonomyClassifier;
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

	private IGeoLocationResolver geoLocationResolver;
	
	private boolean isGeoLocationResolverLoadTried = false;

	private ITaxonomyResolver taxonomyResolver;
	
	private IArticleTextSummarizer articleTextSummarizer;
	
	private boolean isTaxonomyResolverLoadTried = false;
	
	private boolean isTextSummarizerResolverLoadTried = false;

	private IWebLinkTrackerService historyTracker;
	
	private static final Logger LOG = LoggerFactory
			.getLogger(WebsiteImpl.class);

	public WebsiteImpl(WebCrawler crawlerDef) {
		this.crawlerDef = crawlerDef;
	}

	@Override
	public String getUniqueId() {
		String id = crawlerDef.getId();
		return id.replaceAll(" ", "_");
	}
	
	@Override
	public boolean isUploadIndependently() {
		return crawlerDef.isUploadIndependently();
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
	public IGeoLocationResolver getTargetLocationResolver() {
		if (geoLocationResolver == null && !isGeoLocationResolverLoadTried) {
			GeoTagResolver geoTagResolver = crawlerDef.getGeoTagResolver();
			if (geoTagResolver != null) {
				String resolver = geoTagResolver.getResolver();
				if (resolver != null && !resolver.trim().isEmpty()) {
					geoLocationResolver = loadGeoTargetLocationResolver(geoTagResolver);
				}
			}
			isGeoLocationResolverLoadTried = true;
		}
		return geoLocationResolver;
	}

	@Override
	public ITaxonomyResolver getTaxonomyResolver() {
		if (taxonomyResolver == null && !isTaxonomyResolverLoadTried) {
			TaxonomyClassifier taxonomyClassifier = crawlerDef
					.getTaxonomyClassifier();
			if (taxonomyClassifier != null) {
				String resolver = taxonomyClassifier.getResolver();
				if (resolver != null && !resolver.trim().isEmpty()) {
					taxonomyResolver = loadTaxonomyClassifier(taxonomyClassifier);
				}
			}
			isTaxonomyResolverLoadTried = true;
		}
		return taxonomyResolver;
	}
	
	@Override
	public IArticleTextSummarizer getArticleTextSummarizer() {
		if (articleTextSummarizer == null && !isTextSummarizerResolverLoadTried) {
			ArticleSummarizer articleSummarizer = crawlerDef
					.getArticleSummarizer();
			if (articleSummarizer != null) {
				String resolver = articleSummarizer.getResolver();
				if (resolver != null && !resolver.trim().isEmpty()) {
					articleTextSummarizer = loadArticleTextSummarizer(articleSummarizer);
				}
			}
			isTextSummarizerResolverLoadTried = true;
		}
		return articleTextSummarizer;
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
		}
		return contentHandler;
	}

	private IHtmlContentHandler loadContentHandler(
			ContentHandler contentHandlerDef) {
		IHtmlContentHandler contentHandler = null;
		String handler = contentHandlerDef.getHandler().trim();
		try {
			contentHandler = AppContext.INSTANCE.findService(handler,
					IHtmlContentHandler.class);
		} catch (NoSuchBeanDefinitionException e) {
			LOG.error(e.getMessage(), e);
		}
		if (contentHandler == null) {
			try {
				Object newInstance = Class.forName(handler).newInstance();
				if (newInstance instanceof IHtmlContentHandler) {
					contentHandler = (IHtmlContentHandler) newInstance;
				}
			} catch (InstantiationException e) {
				LOG.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return contentHandler;
	}

	private IGeoLocationResolver loadGeoTargetLocationResolver(
			GeoTagResolver goGeoTagResolver) {
		IGeoLocationResolver geoLocationResolver = null;
		String resolver = goGeoTagResolver.getResolver().trim();
		try {
			geoLocationResolver = AppContext.INSTANCE.findService(resolver,
					IGeoLocationResolver.class);
		} catch (NoSuchBeanDefinitionException e) {
			LOG.error(e.getMessage(), e);
		}
		if (geoLocationResolver == null) {
			try {
				Object newInstance = Class.forName(resolver).newInstance();
				if (newInstance instanceof IGeoLocationResolver) {
					geoLocationResolver = (IGeoLocationResolver) newInstance;
				}
			} catch (InstantiationException e) {
				LOG.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return geoLocationResolver;
	}

	private ITaxonomyResolver loadTaxonomyClassifier(
			TaxonomyClassifier taxonomyClassifier) {
		ITaxonomyResolver taxonomyResolver = null;
		String resolver = taxonomyClassifier.getResolver().trim();
		try {
			taxonomyResolver = AppContext.INSTANCE.findService(resolver,
					ITaxonomyResolver.class);
		} catch (NoSuchBeanDefinitionException e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage(), e);
		}
		if (taxonomyResolver == null) {
			try {
				Object newInstance = Class.forName(resolver).newInstance();
				if (newInstance instanceof ITaxonomyResolver) {
					taxonomyResolver = (ITaxonomyResolver) newInstance;
				}
			} catch (InstantiationException e) {
				LOG.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return taxonomyResolver;
	}
	
	private IArticleTextSummarizer loadArticleTextSummarizer(
			ArticleSummarizer articleSummarizer) {
		IArticleTextSummarizer articleTextSummarizer = null;
		String resolver = articleSummarizer.getResolver().trim();
		try {
			articleTextSummarizer = AppContext.INSTANCE.findService(resolver,
					IArticleTextSummarizer.class);
		} catch (NoSuchBeanDefinitionException e) {
			LOG.error(e.getMessage(), e);
		}
		if (articleTextSummarizer == null) {
			try {
				Object newInstance = Class.forName(resolver).newInstance();
				if (newInstance instanceof IArticleTextSummarizer) {
					articleTextSummarizer = (IArticleTextSummarizer) newInstance;
				}
			} catch (InstantiationException e) {
				LOG.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return articleTextSummarizer;
	}

	@Override
	public ICrawlSchedule getSchedule() {
		return new ICrawlSchedule() {

			@Override
			public TimeUnit getTimeUnit() {
				String timeUnit = crawlerDef.getScheduler().getTimeUnit()
						.toUpperCase();
				TimeUnit unit = null;
				try {
					unit = TimeUnit.valueOf(timeUnit);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
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
		if (historyTracker != null)
			return historyTracker;
		WebTracker webTracker = crawlerDef.getWebLinkTracker();
		if (webTracker == null)
			return null;
		String serviceId = webTracker.getServiceId();
		try {
			historyTracker = AppContext.INSTANCE.findService(serviceId,
					IWebLinkTrackerService.class);
		} catch (NoSuchBeanDefinitionException e) {
			LOG.error(e.getMessage(), e);
		}
		if (historyTracker == null) {
			try {
				Object newInstance = Class.forName(serviceId).newInstance();
				if (newInstance instanceof IWebLinkTrackerService) {
					historyTracker = (IWebLinkTrackerService) newInstance;
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
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof IWebSite) {
			return this.getUniqueId().equals(((IWebSite)obj).getUniqueId());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.getUniqueId().hashCode();
	}
}