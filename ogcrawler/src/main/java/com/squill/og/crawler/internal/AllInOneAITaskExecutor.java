package com.squill.og.crawler.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.IWebCrawlable;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.hooks.GeoLocation;
import com.squill.og.crawler.hooks.IArticleTextSummarizer;
import com.squill.og.crawler.hooks.IFeedClassificationResolver;
import com.squill.og.crawler.hooks.IGeoLocationResolver;
import com.squill.og.crawler.hooks.ISpiderSession;
import com.squill.og.crawler.hooks.ITaxonomyResolver;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.utils.FeedClassifierUtil;
import com.squill.og.crawler.model.WebSpiderTracker;
import com.squill.og.crawler.model.web.JGeoTag;
import com.squill.og.crawler.model.web.JRssFeed;
import com.squill.og.crawler.model.web.JTaxonomy;
import com.squill.og.crawler.text.summarizer.TextSummarization;

public class AllInOneAITaskExecutor {
	
	private static final Logger LOG = LoggerFactory.getLogger(AllInOneAITaskExecutor.class);
	
	private ISpiderSession session;
	
	public AllInOneAITaskExecutor(ISpiderSession session) {
		this.session = session;
	}
	
	public Map<String, List<JRssFeed>> executeTasks(Map<String, List<JRssFeed>> feedsMap, IWebCrawlable webCrawlable) {
		return executeAITasks(feedsMap, session, webCrawlable);
	}

	private IFeedClassificationResolver getClassificationResolver() {
		return new IFeedClassificationResolver() {

			@Override
			public String resolvePrimaryClassifierType(String feedTitle,
					String feedDescription, String url) {
				// TODO Need to integrate AI based classifier engine here
				return null;
			}
			
			@Override
			public List<String> resolveIPTCTypes(String feedTitle,
					String feedDescription, String url) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	private String classifyFeedType(JRssFeed feed) {
		String classifier = FeedClassifierUtil.classify(feed);
		if (classifier == null) {
			IFeedClassificationResolver classificationResolver = getClassificationResolver();
			if (classificationResolver != null) {
				return classificationResolver.resolvePrimaryClassifierType(
						feed.getOgTitle(), feed.getOgDescription(),
						feed.getOgUrl());
			}
		}
		return null;
	}
	
	private Map<String, List<JRssFeed>> deDuplicateFeeds(Map<String, List<JRssFeed>> feedsMap) {
		return feedsMap;
	}
	
	private String resolveDomainUrl(String linkUrl) {
		try {
			URL url = new URL(linkUrl);
			return url.getProtocol() + "://" + url.getHost();
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}
	
	private Map<String, List<JRssFeed>> executeAITasks(Map<String, List<JRssFeed>> feedsMap, ISpiderSession session, IWebCrawlable webCrawlable) {
		IWebLinkTrackerService webLinkTrackerService = webCrawlable.getTrackerService();
		IGeoLocationResolver geoLocationResolver = webCrawlable.getTargetLocationResolver();
		ITaxonomyResolver taxonomyResolver = webCrawlable.getTaxonomyResolver();
		try {
			Iterator<String> itr = feedsMap.keySet().iterator();
			while(itr.hasNext()) {
				String key = itr.next();
				List<JRssFeed> feeds = feedsMap.get(key);
				if(feeds == null)
					continue;
				for (JRssFeed feed : feeds) {
					String link = feed.getOgUrl();
					WebSpiderTracker info = webLinkTrackerService.getTrackedInfo(link);
					if(info != null)
						continue;
					
					info = new WebSpiderTracker();
					info.setLastCrawled(System.currentTimeMillis());
					info.setLink(link);
					webLinkTrackerService.addCrawledInfo(link, info, 24 * 60 * 60);
					
					String classifier = classifyFeedType(feed);
					if(classifier != null) {
						feed.setOgType(classifier);
					}
					IArticleTextSummarizer articleTextSummarizer = webCrawlable
							.getArticleTextSummarizer();
					if (articleTextSummarizer != null) {
						TextSummarization response = articleTextSummarizer
								.summarize(feed.getOgUrl(), feed.getOgTitle(),
										feed.getOgDescription());
						if (response != null) {
							feed.setArticleSummaryText(response
									.extractedAllSummary(false));
							feed.setFullArticleText(response.getText());
						}
					}
					if(geoLocationResolver != null) {
						String domainUrl = null;
						if(webCrawlable instanceof IWebSite) {
							domainUrl = ((IWebSite)webCrawlable).getDomainUrl();
						} else {
							domainUrl = resolveDomainUrl(feed.getOgUrl());
						}
						GeoLocation[] geoLocations = geoLocationResolver.resolveGeoLocations(feed.getOgUrl(), domainUrl, feed);
						if(geoLocations != null && geoLocations.length > 0) {
							for(GeoLocation geoLocation : geoLocations) {
								JGeoTag geoTag = new JGeoTag();
								geoTag.setLatitude(geoLocation.getLatitude());
								geoTag.setLongitude(geoLocation.getLongitude());
								feed.getGeoTags().add(geoTag);
							}
						}
					}
					
					if(taxonomyResolver != null) {
						JTaxonomy[] taxonomies = taxonomyResolver.resolveTaxonomies(feed.getOgTitle(), feed.getOgUrl());
						if(taxonomies != null && taxonomies.length > 0) {
							for(JTaxonomy taxonomy : taxonomies) {
								feed.getTaxonomies().add(taxonomy);
							}
						}
					}
					
				}
			}
			/*IFeedUploader feedUploader = currentWebSite.getFeedUploader();
			if(feedUploader != null) {
				feedUploader.uploadBulk(rssFeeds);
			}*/
			return deDuplicateFeeds(feedsMap);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return feedsMap;
		}
	}
}
