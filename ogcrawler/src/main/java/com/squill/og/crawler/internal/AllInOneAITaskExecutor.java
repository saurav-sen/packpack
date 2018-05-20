package com.squill.og.crawler.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.feed.web.model.JGeoTag;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JTaxonomy;
import com.squill.og.crawler.IWebCrawlable;
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.hooks.GeoLocation;
import com.squill.og.crawler.hooks.IArticleTextSummarizer;
import com.squill.og.crawler.hooks.IGeoLocationResolver;
import com.squill.og.crawler.hooks.ISpiderSession;
import com.squill.og.crawler.hooks.ITaxonomyResolver;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.utils.FeedClassifierUtil;
import com.squill.og.crawler.model.WebSpiderTracker;
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

	private String classifyFeedType(JRssFeed feed) {
		return FeedClassifierUtil.classify(feed);
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
				Iterator<JRssFeed> feedsItr = feeds.iterator();
				while(feedsItr.hasNext()) {
					JRssFeed feed = feedsItr.next();
					String link = feed.getOgUrl();
					WebSpiderTracker info = webLinkTrackerService.getTrackedInfo(link);
					if(session.isThresholdReached()) {
						LOG.debug("Threshold Reached for the day, will skip all remaining");
					}
					if((info != null && info.isUploadCompleted()) || session.isThresholdReached()) {
						if(info != null && info.isUploadCompleted()) {
							LOG.debug("Already read earlier thereby skipping link @ " + link);
						}
						feedsItr.remove();
						continue;
					}
					
					if(info == null) {
						info = new WebSpiderTracker();
					}
					info.setLastCrawled(System.currentTimeMillis());
					info.setLink(link);
					long linkInfoTtlSeconds = 30 * 60 * 60;
					webLinkTrackerService.upsertCrawledInfo(link, info, linkInfoTtlSeconds, false);
					
					boolean needToUpsertLinkInfo = false;
					
					if(info.getArticleSummaryText() == null) {
						IArticleTextSummarizer articleTextSummarizer = webCrawlable
								.getArticleTextSummarizer();
						if (articleTextSummarizer != null) {
							session.incrementCrawledCount(1);
							if(session.isThresholdReached()) {
								feedsItr.remove();
								continue;
							}
							TextSummarization response = articleTextSummarizer
									.summarize(feed.getOgUrl(), feed.getOgTitle(),
											feed.getOgDescription());
							if (response != null) {
								feed.setArticleSummaryText(response
										.extractedAllSummary(false));
								feed.setFullArticleText(response.getText());
								
								info.setArticleSummaryText(response
										.extractedAllSummary(false));
								info.setFullArticleText(response.getText());
							}
						}
						needToUpsertLinkInfo = true;
					} else {
						feed.setArticleSummaryText(info.getArticleSummaryText());
						feed.setFullArticleText(info.getFullArticleText());
					}
					
					if(!info.isGeoTagsResolved()) {
						needToUpsertLinkInfo = true;
						if(geoLocationResolver != null) {
							String domainUrl = null;
							if(webCrawlable instanceof IWebSite) {
								domainUrl = ((IWebSite)webCrawlable).getDomainUrl();
							} else {
								domainUrl = resolveDomainUrl(feed.getOgUrl());
							}
							session.incrementCrawledCount(1);
							if(session.isThresholdReached()) {
								feedsItr.remove();
								continue;
							}
							GeoLocation[] geoLocations = geoLocationResolver.resolveGeoLocations(feed.getOgUrl(), domainUrl, feed);
							if(geoLocations != null && geoLocations.length > 0) {
								for(GeoLocation geoLocation : geoLocations) {
									JGeoTag geoTag = new JGeoTag();
									geoTag.setLatitude(geoLocation.getLatitude());
									geoTag.setLongitude(geoLocation.getLongitude());
									feed.getGeoTags().add(geoTag);
									info.getGeoTags().add(geoTag);
								}
							}
							info.setGeoTagsResolved(true);
						}
					} else {
						feed.getGeoTags().addAll(info.getGeoTags());
					}
					
					if(info.getTaxonomies().isEmpty()) {
						needToUpsertLinkInfo = true;
						if(taxonomyResolver != null) {
							session.incrementCrawledCount(1);
							if(session.isThresholdReached()) {
								feedsItr.remove();
								continue;
							}
							JTaxonomy[] taxonomies = taxonomyResolver.resolveTaxonomies(feed.getOgTitle(), feed.getOgUrl());
							if(taxonomies != null && taxonomies.length > 0) {
								for(JTaxonomy taxonomy : taxonomies) {
									feed.getTaxonomies().add(taxonomy);
									info.getTaxonomies().add(taxonomy);
								}
							}
						}
					} else {
						feed.getTaxonomies().addAll(info.getTaxonomies());
					}
					
					if(needToUpsertLinkInfo) {
						webLinkTrackerService.upsertCrawledInfo(link, info, linkInfoTtlSeconds, false);
					}
					
					String classifier = classifyFeedType(feed);
					if(classifier != null) {
						feed.setFeedType(classifier);
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
