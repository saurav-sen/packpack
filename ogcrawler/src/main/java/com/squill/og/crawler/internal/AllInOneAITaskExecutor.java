package com.squill.og.crawler.internal;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.hooks.GeoLocation;
import com.squill.og.crawler.hooks.IArticleTextSummarizer;
import com.squill.og.crawler.hooks.IFeedClassificationResolver;
import com.squill.og.crawler.hooks.IGeoLocationResolver;
import com.squill.og.crawler.hooks.ISpiderSession;
import com.squill.og.crawler.hooks.ITaxonomyResolver;
import com.squill.og.crawler.internal.utils.FeedClassifierUtil;
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
	
	public Map<String, List<JRssFeed>> executeTasks(Map<String, List<JRssFeed>> feedsMap) {
		return executeAITasks(feedsMap, session);
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

	private Map<String, List<JRssFeed>> executeAITasks(Map<String, List<JRssFeed>> feedsMap, ISpiderSession session) {
		IWebSite currentWebSite = (IWebSite) session.getCurrentWebCrawlable();
		String domainUrl = currentWebSite.getDomainUrl();
		IGeoLocationResolver geoLocationResolver = currentWebSite.getTargetLocationResolver();
		ITaxonomyResolver taxonomyResolver = currentWebSite.getTaxonomyResolver();
		try {
			Iterator<String> itr = feedsMap.keySet().iterator();
			while(itr.hasNext()) {
				String key = itr.next();
				List<JRssFeed> feeds = feedsMap.get(key);
				if(feeds == null)
					continue;
				for (JRssFeed feed : feeds) {
					String classifier = classifyFeedType(feed);
					if(classifier != null) {
						feed.setOgType(classifier);
					}
					IArticleTextSummarizer articleTextSummarizer = currentWebSite
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
						JTaxonomy[] taxonomies = taxonomyResolver.resolveTaxonomies(feed.getOgUrl(), feed.getOgTitle());
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
