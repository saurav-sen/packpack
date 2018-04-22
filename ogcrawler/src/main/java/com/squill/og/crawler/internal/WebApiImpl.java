package com.squill.og.crawler.internal;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.squill.og.crawler.ICrawlSchedule;
import com.squill.og.crawler.IWebApi;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.hooks.IApiRequestExecutor;
import com.squill.og.crawler.hooks.IArticleTextSummarizer;
import com.squill.og.crawler.hooks.IGeoLocationResolver;
import com.squill.og.crawler.hooks.ITaxonomyResolver;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.model.ApiReader;
import com.squill.og.crawler.model.ApiRequestExecutor;
import com.squill.og.crawler.model.ArticleSummarizer;
import com.squill.og.crawler.model.GeoTagResolver;
import com.squill.og.crawler.model.TaxonomyClassifier;
import com.squill.og.crawler.model.WebTracker;

public class WebApiImpl implements IWebApi {
	
	private ApiReader crawlerDef;
	
	private IApiRequestExecutor apiRequestExecutor;

	private IGeoLocationResolver geoLocationResolver;

	private boolean isGeoLocationResolverLoadTried = false;

	private ITaxonomyResolver taxonomyResolver;

	private IArticleTextSummarizer articleTextSummarizer;

	private boolean isTaxonomyResolverLoadTried = false;

	private boolean isTextSummarizerResolverLoadTried = false;

	private IWebLinkTrackerService historyTracker;

	private static final Logger LOG = LoggerFactory
			.getLogger(WebApiImpl.class);

	public WebApiImpl(ApiReader crawlerDef) {
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
	public IApiRequestExecutor getApiExecutor() {
		if(apiRequestExecutor == null) {
			ApiRequestExecutor apiRequestExecutorDef = crawlerDef.getApiRequestExecutor();
			if(apiRequestExecutorDef != null) {
				String executor = apiRequestExecutorDef.getExecutor();
				if(executor != null && !executor.trim().isEmpty()) {
					apiRequestExecutor = loadApiRequestExecutorInstance(executor);
				}
			}
		}
		return apiRequestExecutor;
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
	
	private IApiRequestExecutor loadApiRequestExecutorInstance(String executor) {
		IApiRequestExecutor apiRequestExecutor = null;
		try {
			apiRequestExecutor = AppContext.INSTANCE.findService(executor,
					IApiRequestExecutor.class);
		} catch (NoSuchBeanDefinitionException e) {
			LOG.error(e.getMessage(), e);
		}
		if (apiRequestExecutor == null) {
			try {
				Object newInstance = Class.forName(executor).newInstance();
				if (newInstance instanceof IApiRequestExecutor) {
					apiRequestExecutor = (IApiRequestExecutor) newInstance;
				}
			} catch (InstantiationException e) {
				LOG.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return apiRequestExecutor;
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
		if (obj instanceof IWebSite) {
			return this.getUniqueId().equals(((IWebSite) obj).getUniqueId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getUniqueId().hashCode();
	}
}
