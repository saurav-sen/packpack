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
import com.squill.og.crawler.rss.RSSConstants;
import com.squill.og.crawler.text.summarizer.TextSummarization;
import com.squill.services.exception.OgCrawlException;

public class AllInOneAITaskExecutor {
	
	private static final Logger LOG = LoggerFactory.getLogger(AllInOneAITaskExecutor.class);
	
	private ISpiderSession session;
	
	public AllInOneAITaskExecutor(ISpiderSession session) {
		this.session = session;
	}
	
	public void executeTasks(Map<String, List<JRssFeed>> feedsMap, IWebCrawlable webCrawlable) {
		executeAITasks(feedsMap, session, webCrawlable);
	}

	private String classifyFeedType(JRssFeed feed) {
		return FeedClassifierUtil.classify(feed);
	}
	
	/*private Map<String, List<JRssFeed>> deDuplicateFeeds(Map<String, List<JRssFeed>> feedsMap) {
		return feedsMap;
	}*/
	
	private String resolveDomainUrl(String linkUrl) {
		try {
			URL url = new URL(linkUrl);
			return url.getProtocol() + "://" + url.getHost();
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}
	
	private void executeAITasks(Map<String, List<JRssFeed>> feedsMap, ISpiderSession session, IWebCrawlable webCrawlable) {
		IWebLinkTrackerService webLinkTrackerService = webCrawlable.getTrackerService();

		try {
			Iterator<String> itr = feedsMap.keySet().iterator();
			while(itr.hasNext()) {
				String key = itr.next();
				List<JRssFeed> feeds = feedsMap.get(key);
				if(feeds == null)
					continue;
				LOG.debug("Total Feeds from " + key + " is = " + feeds.size());
				Iterator<JRssFeed> feedsItr = feeds.iterator();
				while(feedsItr.hasNext()) {
					JRssFeed feed = feedsItr.next();
					feed.setOgType(feed.getFeedType());
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
					
					boolean isNew = false;
					if(info == null) {
						info = new WebSpiderTracker();
						info.setWebCrawlerId(webCrawlable.getUniqueId());
						isNew = true;
					}
					
					info.setLastCrawled(System.currentTimeMillis());
					info.setLink(link);
					webLinkTrackerService.upsertCrawledInfo(link, info,
							RSSConstants.DEFAULT_TTL_WEB_TRACKING_INFO, false);
					
					JRssFeed oldFeed = info.getFeedToUpload();
					if(oldFeed != null) {
						if(oldFeed.getFeedType() == null) {
							oldFeed.setFeedType(feed.getFeedType());
							oldFeed.setOgType(feed.getOgType());
							webLinkTrackerService.upsertCrawledInfo(link, info,
									RSSConstants.DEFAULT_TTL_WEB_TRACKING_INFO, false);
						}
						continue;
					}
					
					String domainUrl = null;
					if(webCrawlable instanceof IWebSite) {
						domainUrl = ((IWebSite)webCrawlable).getDomainUrl();
					} else {
						domainUrl = resolveDomainUrl(feed.getOgUrl());
					}
					
					executeDocumentSummarization(
							info, feed, webCrawlable, isNew);
					executeDocumentClassification(info, feed,
									webCrawlable, domainUrl, isNew);
					executeDocumentGeoTagging(info, feed,
									webCrawlable, domainUrl, isNew);
					
					info.setFeedToUpload(feed);
					webLinkTrackerService.upsertCrawledInfo(link, info,
							RSSConstants.DEFAULT_TTL_WEB_TRACKING_INFO,
							false);
				}
			}
			/*IFeedUploader feedUploader = currentWebSite.getFeedUploader();
			if(feedUploader != null) {
				feedUploader.uploadBulk(rssFeeds);
			}*/
			//return deDuplicateFeeds(feedsMap);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			//return feedsMap;
		}
	}
	
	private boolean executeDocumentSummarization(WebSpiderTracker info, JRssFeed feed,
			IWebCrawlable webCrawlable, boolean isNew) throws OgCrawlException {
		boolean needToUpsertLinkInfo = false;
		String link = feed.getOgUrl();
		if (info.getArticleSummaryText() == null) {
			if (!isNew) {
				LOG.debug("Article Summary Text is NULL @ " + link);
			}
			IArticleTextSummarizer articleTextSummarizer = webCrawlable
					.getArticleTextSummarizer();
			if (articleTextSummarizer != null) {
				session.incrementCrawledCount(1);
				if (session.isThresholdReached()) {
					return false;
				}
				TextSummarization response = articleTextSummarizer.summarize(
						feed.getOgUrl(), feed.getOgTitle(),
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
		return needToUpsertLinkInfo;
	}
	
	private boolean executeDocumentClassification(WebSpiderTracker info, JRssFeed feed,
			IWebCrawlable webCrawlable, String domainUrl, boolean isNew) throws Exception {
		boolean needToUpsertLinkInfo = false;
		String link = feed.getOgUrl();
		ITaxonomyResolver taxonomyResolver = webCrawlable.getTaxonomyResolver();
		if(info.getTaxonomies().isEmpty()) {
			if(!isNew) {
				LOG.debug("Taxonomies NOT resolved @ " + link);
			}
			needToUpsertLinkInfo = true;
			JTaxonomy[] taxonomies = null;
			ITaxonomyResolver basicTaxonomyResolver = AppContext.INSTANCE
					.findService("basicTaxonomyResolver",
							ITaxonomyResolver.class);
			if (basicTaxonomyResolver != null
					&& basicTaxonomyResolver.canResolve(
							feed.getOgUrl(), domainUrl, feed)) {
				taxonomies = basicTaxonomyResolver
						.resolveTaxonomies(feed.getOgTitle(),
								feed.getOgUrl(), domainUrl);
			} else if (taxonomyResolver != null) {
				session.incrementCrawledCount(1);
				if (session.isThresholdReached()) {
					return false;
				}
				taxonomies = taxonomyResolver.resolveTaxonomies(
						feed.getOgTitle(), feed.getOgUrl(), domainUrl);
			}
			if(taxonomies != null && taxonomies.length > 0) {
				for(JTaxonomy taxonomy : taxonomies) {
					feed.getTaxonomies().add(taxonomy);
					info.getTaxonomies().add(taxonomy);
				}
			}
		} else {
			if(!isNew) {
				LOG.debug("Taxonomies NOT resolved @ " + link);
			}
			feed.getTaxonomies().addAll(info.getTaxonomies());
		}
		
		String classifier = classifyFeedType(feed);
		if(classifier != null) {
			feed.setFeedType(classifier);
		}
		return needToUpsertLinkInfo;
	}
	
	private boolean executeDocumentGeoTagging(WebSpiderTracker info, JRssFeed feed,
			IWebCrawlable webCrawlable, String domainUrl, boolean isNew) throws Exception {
		boolean needToUpsertLinkInfo = false;
		String link = feed.getOgUrl();
		IGeoLocationResolver geoLocationResolver = webCrawlable.getTargetLocationResolver();
		if(!info.isGeoTagsResolved()) {
			needToUpsertLinkInfo = true;
			if(geoLocationResolver != null) {
				GeoLocation[] geoLocations = null;
				IGeoLocationResolver basicGeoLocationResolver = AppContext.INSTANCE
						.findService("basicGeoLocationResolver",
								IGeoLocationResolver.class);
				if (geoLocationResolver.canResolve(feed.getOgUrl(), domainUrl,
						feed)) {
					session.incrementCrawledCount(1);
					if (session.isThresholdReached()) {
						return false;
					}
					geoLocations = geoLocationResolver.resolveGeoLocations(
							feed.getOgUrl(), domainUrl, feed);
				}
				if ((geoLocations == null || geoLocations.length == 0)
						&& basicGeoLocationResolver.canResolve(feed.getOgUrl(),
								domainUrl, feed)) {
					geoLocations = basicGeoLocationResolver
							.resolveGeoLocations(feed.getOgUrl(), domainUrl,
									feed);
				}
				
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
			if(!isNew) {
				LOG.debug("Geo Tag NOT resolved @ " + link);
			}
			feed.getGeoTags().addAll(info.getGeoTags());
		}
		return needToUpsertLinkInfo;
	}
}
